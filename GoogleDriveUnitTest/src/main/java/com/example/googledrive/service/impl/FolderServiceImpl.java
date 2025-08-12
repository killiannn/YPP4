package com.example.googledrive.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.example.googledrive.entity.Folder;
import com.example.googledrive.service.interf.FolderService;
import com.example.googledrive.service.mapper.FolderRowMapper;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private JdbcTemplate jdbcTemplate;
    public FolderServiceImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
}

public void create(Folder folder) {
    String sql = "INSERT INTO Folder (ParentId, OwnerId, Name, Size, CreatedAt, UpdatedAt, Path, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, folder.getParentId(), folder.getOwnerId(), folder.getName(), folder.getSize(), folder.getCreatedAt(), folder.getUpdatedAt(), folder.getPath(), folder.getStatus());
}

public List<Folder> findAll() {
    String sql = "SELECT * FROM Folder";
    return jdbcTemplate.query(sql, new FolderRowMapper());
}

public Folder findById(int id) {
    String sql = "SELECT * FROM Folder WHERE Id = ?";
    return jdbcTemplate.queryForObject(sql, new FolderRowMapper(), id);
}
public void update(Folder folder) {
    String sql = "UPDATE Folder SET ParentId = ?, OwnerId = ?, Name = ?, Size = ?, CreatedAt = ?, UpdatedAt = ?, Path = ?, Status = ? WHERE Id = ?";
    jdbcTemplate.update(sql, folder.getParentId(), folder.getOwnerId(), folder.getName(), folder.getSize(), folder.getCreatedAt(), folder.getUpdatedAt(), folder.getPath(), folder.getStatus(), folder.getId());
}

public void delete(int id) {
    String sql = "DELETE FROM Folder WHERE Id = ?";
    jdbcTemplate.update(sql, id);
}
}

