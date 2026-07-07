package io.github.seonghun.springredis.user;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @PostMapping("/score")
    public ResponseEntity<Void> addScore(@RequestParam String userId,
                                         @RequestParam double score) {
        rankingService.addScore(userId, score);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top")
    public ResponseEntity<?> top() {
        return ResponseEntity.ok(rankingService.top100());
    }
}