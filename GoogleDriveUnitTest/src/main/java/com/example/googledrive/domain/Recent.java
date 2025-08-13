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
public class Recent {
    private int Id;
    private int UserId;
    private int ObjectId;
    private int ObjectTypeId;
    private String Log;
    private Instant DateTime;
}