package com.example.googledrive.service.mapper.row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import com.example.googledrive.domain.Folder;

public class FolderRowMapper extends BaseRowMapper<Folder> {

    @Override
    protected Folder mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        return new Folder(
                rs.getInt("FolderId"),
                rs.getInt("ParentId"),
                rs.getInt("OwnerId"),
                rs.getString("Name"),
                rs.getInt("Size"),
                rs.getString("Path"),
                rs.getString("Status"),
                rs.getTimestamp("CreatedAt") != null ? rs.getTimestamp("CreatedAt").toInstant() : null,
                rs.getTimestamp("UpdatedAt") != null ? rs.getTimestamp("UpdatedAt").toInstant() : null);
    }

}