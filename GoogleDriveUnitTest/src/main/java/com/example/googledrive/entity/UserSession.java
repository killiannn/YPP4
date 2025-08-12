package com.example.googledrive.entity;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSession {
    private int Id;
    private int UserId;
    private String Token;
    private Instant CreatedAt;
    private Instant ExpiredAt;
}