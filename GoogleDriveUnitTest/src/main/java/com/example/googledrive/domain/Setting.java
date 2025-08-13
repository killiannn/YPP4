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
public class Setting {
    private int Id;
    private String SettingKey;
    private String SettingValue;
    private String Description;
}