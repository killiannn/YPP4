package com.example.googledrive.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.googledrive.service.interf.FolderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    // Get folder by ownerId
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<com.example.googledrive.dto.FolderDTO> getFolderByOwnerId(
            @PathVariable Integer ownerId) {
        return folderService.findByOwnerId(ownerId)
                .map(org.springframework.http.ResponseEntity::ok)
                .orElseGet(() -> org.springframework.http.ResponseEntity.notFound().build());
    }
}