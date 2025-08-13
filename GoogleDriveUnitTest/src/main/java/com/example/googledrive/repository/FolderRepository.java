package com.example.googledrive.repository;

import com.example.googledrive.dto.FolderDTO;
import com.example.googledrive.domain.Folder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FolderRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<FolderDTO> folderRowMapper;

    public FolderRepository(JdbcTemplate jdbcTemplate, RowMapper<FolderDTO> folderRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.folderRowMapper = folderRowMapper;
    }

    public FolderDTO create(Folder folder) {
        String sql = "INSERT INTO Folder (ParentId, OwnerId, Name, Size, CreatedAt, UpdatedAt, Path, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, folder.getParentId(), folder.getOwnerId(), folder.getName(), folder.getSize(),
                folder.getCreatedAt(), folder.getUpdatedAt(), folder.getPath(), folder.getStatus());
        return findByPath(folder.getPath());
    }

    public List<FolderDTO> findAll() {
        String sql = "SELECT * FROM Folder";
        return jdbcTemplate.query(sql, folderRowMapper);
    }

    public FolderDTO findById(Integer id) {
        String sql = "SELECT * FROM Folder WHERE Id = ?";
        return jdbcTemplate.queryForObject(sql, folderRowMapper, id);
    }

    public FolderDTO findByPath(String path) {
        String sql = "SELECT * FROM Folder WHERE Path = ?";
        return jdbcTemplate.queryForObject(sql, folderRowMapper, path);
    }

    public int update(Integer id, String name, String path) {
        String sql = "UPDATE Folder SET Name = ?, Path = ? WHERE Id = ?";
        return jdbcTemplate.update(sql, name, path, id);
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM Folder WHERE Id = ?";
        return jdbcTemplate.update(sql, id);
    }
}