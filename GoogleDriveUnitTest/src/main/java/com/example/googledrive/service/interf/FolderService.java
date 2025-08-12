package com.example.googledrive.service.interf;

import java.time.Instant;
import java.util.List;
import com.example.googledrive.entity.Folder;

public interface FolderService {

    Folder getFolderById(int id);

    List<Folder> getAllFolder();

    int updateFolderById(int id, String Name);

    int deleteFolderById(int id);

    Folder createFolder(int parentId, int ownerId, String name, String path, String status, int size, Instant CreatedAt, Instant UpdatedAt);


}

