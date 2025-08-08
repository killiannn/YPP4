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
public class FileContent {
    private int Id;
    private int FileId;
    private String ContentChunk;
    private int ChunkIndex;
    private int DocumentLength;
    private Instant CreatedAt;
}