package com.example.googledrive.service.interf;

import java.time.Instant;
import java.util.List;

import com.example.googledrive.domain.File;

public interface FileService {

    File getFileById(int id);

    List<File> getAllFile();

    int updateFileById(int id, String Name);

    int deleteFileById(int id);

    File createFile(int folderId, int ownerId, int size, String name, String path, int fileTypeId, String status, 
            Instant CreatedAt, Instant modifiedDate);



}

