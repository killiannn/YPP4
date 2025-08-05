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
public class Share {
    private Integer id;
    private Integer sharer;
    private Integer objectId;
    private Integer objectTypeId;
    private Instant CreatedAt;
    private Instant ExpiredAt;
}