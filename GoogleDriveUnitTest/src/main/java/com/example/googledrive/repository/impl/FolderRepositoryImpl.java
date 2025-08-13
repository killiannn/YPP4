package com.example.googledrive.repository.impl;

import com.example.googledrive.domain.Folder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.googledrive.repository.interf.FolderRepository;
import com.example.googledrive.service.mapper.row.FolderRowMapper;
import java.util.List;

@Repository
public class FolderRepositoryImpl implements FolderRepository {

    private final JdbcTemplate jdbcTemplate;
    private final FolderRowMapper folderRowMapper;

    public FolderRepositoryImpl(JdbcTemplate jdbcTemplate, FolderRowMapper folderRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.folderRowMapper = folderRowMapper;
    }

    @Override
    public Folder create(Folder folder) {
        String sql = "INSERT INTO Folder (ParentId, OwnerId, Name, Size, CreatedAt, UpdatedAt, Path, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, folder.getParentId(), folder.getOwnerId(), folder.getName(), folder.getSize(),
                folder.getCreatedAt(), folder.getUpdatedAt(), folder.getPath(), folder.getStatus());
        return findByPath(folder.getPath());
    }

    @Override
    public List<Folder> findAll() {
        String sql = "SELECT * FROM Folder";
        return jdbcTemplate.query(sql, folderRowMapper);
    }

    @Override
    public Folder findById(Integer id) {
        String sql = "SELECT * FROM Folder WHERE Id = ?";
        return jdbcTemplate.queryForObject(sql, folderRowMapper, id);
    }

    @Override
    public Folder findByPath(String path) {
        String sql = "SELECT * FROM Folder WHERE Path = ?";
        return jdbcTemplate.queryForObject(sql, folderRowMapper, path);
    }

    @Override
    public int update(Integer id, String name, String path) {
        String sql = "UPDATE Folder SET Name = ?, Path = ? WHERE Id = ?";
        return jdbcTemplate.update(sql, name, path, id);
    }

    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM Folder WHERE Id = ?";
        return jdbcTemplate.update(sql, id);
    }
}