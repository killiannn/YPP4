package com.example.googledrive.entity;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchIndex {
    private int Id;
    private int ObjectId;
    private int ObjectTypeId;
    private String Term;
    private int TermFrequency;
    private int DocumentLength;
    private List TermPositions;
}