package com.example.googledrive.service.interf;

import java.time.Instant;
import java.util.List;

import com.example.googledrive.dto.FolderDTO;

public interface FolderService {

    FolderDTO getFolderById(int id);

    List<FolderDTO> getAllFolder();

    int updateFolderById(int id, String Name);

    int deleteFolderById(int id);

    FolderDTO createFolder(int parentId, int ownerId, String name, int size, String path, String status,  Instant CreatedAt, Instant UpdatedAt);


}

