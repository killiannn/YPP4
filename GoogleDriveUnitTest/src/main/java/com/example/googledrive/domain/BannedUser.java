package com.example.googledrive.domain;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannedUser {
    private int Id;
    private int UserId;
    private Instant BannedAt;
    private int BannedUserId;
    
}