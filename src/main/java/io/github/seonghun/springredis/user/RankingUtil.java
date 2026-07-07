package io.github.seonghun.springredis.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class RankingUtil {
    private final StringRedisTemplate stringRedisTemplate;

    public void setScore(String key, String member, double score) {
        stringRedisTemplate.opsForZSet().add(key, member, score);
    }

    public void addAll(String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        stringRedisTemplate.opsForZSet().add(key, tuples);
    }

    public List<ZSetOperations.TypedTuple<String>> top100(String key) {
        return new ArrayList<>(stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 99));
    }
}
