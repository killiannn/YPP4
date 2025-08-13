package com.example.googledrive.repository.interf;

import com.example.googledrive.domain.Folder;

import java.util.List;

public interface FolderRepository {
    Folder create(Folder folder);
    List<Folder> findAll();
    Folder findById(Integer id);
    Folder findByPath(String path);
    int update(Integer id, String name, String path);
    int delete(Integer id);
}