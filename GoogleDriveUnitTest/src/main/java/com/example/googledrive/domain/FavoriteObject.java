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
public class FavoriteObject {
    private int Id;
    private int OwnerId;
    private int ObjectId;
    private int ObjectTypeId;
}