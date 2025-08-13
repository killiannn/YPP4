package com.example.googledrive.service.interf;

import java.time.Instant;
import java.util.List;

import com.example.googledrive.dto.FolderDTO;

public interface FolderService {

    FolderDTO createFolder(int parentId, int ownerId, String name, int size, String path, String status,  Instant CreatedAt, Instant UpdatedAt);

    List<FolderDTO> getAllFolder();

    FolderDTO getFolderById(int id);

    FolderDTO getFolderByPath(String path);

    int updateFolderById(int id, String Name);

    int deleteFolderById(int id);

    


}

