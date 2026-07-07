package io.github.seonghun.springredis.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findTop100ByOrderByScoreDesc();
}
