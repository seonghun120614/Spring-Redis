package io.github.seonghun.springredis.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Log4j2
@RequiredArgsConstructor
public class RankingService {
    private final RankingUtil rankingUtil;
    private final UserRepository userRepository;
    private static final String PREFIX = "ranking:game";

    @Transactional
    public void addScore(String userId, double score) {
        User user = userRepository.findById(UUID.fromString(userId))
                                  .orElseThrow(() -> new IllegalArgumentException("User Not Found"));

        // DB update 먼저
        user.incrementScore(score);

        // Redis Update 후순위
        rankingUtil.setScore(PREFIX, user.getName(), user.getScore());
    }

    @Transactional(readOnly = true)
    public List<RankUsernameScore> top100() {
        List<TypedTuple<String>> top100 = rankingUtil.top100(PREFIX);

        if (top100.isEmpty()) {
            log.info(">>> Cache miss -> Caching Warmup + DB Fallback 실행");
            loadFromDbAndWarmCache();
            top100 = rankingUtil.top100(PREFIX);
        }

        List<TypedTuple<String>> finalTop10 = top100;

        return IntStream.range(0, finalTop10.size())
                        .mapToObj(i -> RankUsernameScore.fromTypedTuple(i + 1, finalTop10.get(i)))
                        .filter(Objects::nonNull)
                        .toList();
    }

    private void loadFromDbAndWarmCache() {
        List<User> users = userRepository.findTop100ByOrderByScoreDesc();
        Set<TypedTuple<String>> tuples = users.stream()
                                              .map(user -> new DefaultTypedTuple<>(user.getName(), user.getScore()))
                                              .collect(Collectors.toSet());
        rankingUtil.addAll(PREFIX, tuples);
    }

    public record RankUsernameScore(long rank, String username, double score) {
        public static RankUsernameScore fromTypedTuple(long rank, TypedTuple<String> tuple) {
            if (tuple.getScore() == null) return null;
            return new RankUsernameScore(rank, tuple.getValue(), tuple.getScore());
        }
    }
}
