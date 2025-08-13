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
public class UserProduct {
    private int Id;
    private int UserId;
    private int ProductId;
    private boolean IsFisrtPaying;
    private int PromotionId;
    private Instant PayingDateTime;
    private Instant EndDateTime;
}