package com.bwl.apiusers.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "INVALID_TOKENS")
public class InvalidToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "TOKEN")
    private String token;

    public InvalidToken(String token) {
        this.token = token;
    }
}
