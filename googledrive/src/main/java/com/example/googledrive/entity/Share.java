package com.example.googledrive.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Share")
@Data
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer sharer;
    private Integer objectId;
    private Integer objectTypeId;
}