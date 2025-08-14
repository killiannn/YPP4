package com.example.googledrive.repository.interf;


import com.example.googledrive.domain.Folder;

import java.util.Optional;

public interface FolderRepository {
    
    Optional<Folder> findByOwnerId(Integer ownerId);
}