package com.example.googledrive.service.interf;

import java.util.Optional;

import com.example.googledrive.dto.FolderDTO;

public interface FolderService {
    Optional<FolderDTO> findByOwnerId(Integer ownerId);

}

