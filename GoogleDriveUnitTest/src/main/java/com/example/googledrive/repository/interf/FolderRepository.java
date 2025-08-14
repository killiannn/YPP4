package com.example.googledrive.repository.interf;


import com.example.googledrive.dto.FolderDTO;

import java.util.Optional;

public interface FolderRepository {
    
    Optional<FolderDTO> findByOwnerId(Integer ownerId);
}