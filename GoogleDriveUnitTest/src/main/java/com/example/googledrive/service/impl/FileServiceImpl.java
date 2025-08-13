package com.example.googledrive.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.example.googledrive.domain.File;
import com.example.googledrive.service.interf.FileService;
import com.example.googledrive.service.mapper.row.FileRowMapper;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final JdbcTemplate jdbcTemplate;
    private final FileRowMapper FileRowMapper = new FileRowMapper();

    @Override
	public File createFile(int folderId, int ownerId, int size, String name, String path, int fileTypeId, String status, 
			Instant CreatedAt, Instant ModifiedDate) {
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
                "INSERT INTO File (FolderId, OwnerId, Size, Name, Path, FileTypeId, Status, CreatedAt, ModifiedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                folderId, ownerId, size, name, path, fileTypeId, status,
                java.sql.Timestamp.from(CreatedAt), java.sql.Timestamp.from(ModifiedDate));
        String selectSql = "SELECT * FROM File WHERE Path = ?";
        return jdbcTemplate.queryForObject(selectSql, FileRowMapper, path);
	}

    @Override
    public List<File> getAllFile() {
        String sql = "SELECT * FROM File";
        return jdbcTemplate.query(sql, FileRowMapper);
    }

    @Override
    public File getFileById(int id) {
        String sql = "SELECT * FROM File WHERE Id = ?";
        return jdbcTemplate.queryForObject(sql, FileRowMapper, id);
    }
   
    @Override
    public int deleteFileById(int id) {
        String sql = "DELETE FROM File WHERE Id = ?";
        return jdbcTemplate.update(sql, id);
    }

	@Override
	public int updateFileById(int id, String Name) {
        File currentFile = getFileById(id);
        currentFile.setName(Name.isBlank() ? currentFile.getName() : Name);
        return jdbcTemplate.update(
                "UPDATE File SET Name = ? WHERE Id = ?",
                currentFile.getName(), id);
        
	}

	public File getFileByPath(String string) {
        String sql = "SELECT * FROM File WHERE Path = ?";
        return jdbcTemplate.queryForObject(sql, FileRowMapper, string);
	}

	
}

