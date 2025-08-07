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
public class User {
    private int Id;
    private String Username;
    private String PasswordHash;
    private String Email;
    private Instant LastLogin;
    private Instant CreatedAt;
    private int UsedCapacity;
    private int Capacity;
    private String PictureUrl;
    public Instant getLastActive() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLastActive'");
    }
}