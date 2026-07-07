package io.github.seonghun.springredis.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.random.RandomGenerator;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uid;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 2)
    private double score;

    public User(String name) {
        this.name = name;
        this.score = RandomGenerator.getDefault().nextDouble(1, 10000);
    }

    public void incrementScore(double score) {
        this.score += score;
    }
}
