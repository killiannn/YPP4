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
public class TermBM25 {
    private int Id;
    private String Term;
    private float BM25;
    private Instant LastUpdated;
}