package com.example.googledrive.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.example.googledrive.domain.Folder;
import com.example.googledrive.service.interf.FolderService;
import com.example.googledrive.service.mapper.row.FolderRowMapper;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final JdbcTemplate jdbcTemplate = null;
    private final FolderRowMapper folderRowMapper = new FolderRowMapper();

    @Override
	public Folder createFolder(int parentId, int ownerId, String name, String path, String status, int size,
			Instant CreatedAt, Instant UpdatedAt) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (CreatedAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        if (ownerId <= 0) {
            throw new IllegalArgumentException("OwnerId cannot be null");
        }
        jdbcTemplate.update(
                "INSERT INTO Folder (ParentId, OwnerId, Name, Size, CreatedAt, UpdatedAt, Path, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                parentId, ownerId, name, size, java.sql.Timestamp.from(CreatedAt), java.sql.Timestamp.from(UpdatedAt), path, status);
        String selectSql = "SELECT * FROM Folder WHERE Path = ?";
        return jdbcTemplate.queryForObject(selectSql, folderRowMapper, path);
	}

    @Override
    public List<Folder> getAllFolder() {
        String sql = "SELECT * FROM Folder";
        return jdbcTemplate.query(sql, folderRowMapper);
    }

    @Override
    public Folder getFolderById(int id) {
        String sql = "SELECT * FROM Folder WHERE Id = ?";
        return jdbcTemplate.queryForObject(sql, folderRowMapper, id);
    }
   
    @Override
    public int deleteFolderById(int id) {
        String sql = "DELETE FROM Folder WHERE Id = ?";
        return jdbcTemplate.update(sql, id);
    }

	@Override
	public int updateFolderById(int id, String Name) {
        Folder currentFolder = getFolderById(id);
        currentFolder.setName(Name.isBlank() ? currentFolder.getName() : Name);
        return jdbcTemplate.update(
                "UPDATE Folder SET Name = ? WHERE Id = ?",
                currentFolder.getName(), id);
        
	}

	public Folder getFolderByPath(String string) {
        String sql = "SELECT * FROM Folder WHERE Path = ?";
        return jdbcTemplate.queryForObject(sql, folderRowMapper, string);
	}

	
}

