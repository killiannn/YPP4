package com.example.googledrive.service.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.googledrive.entity.Folder;

public class FolderRowMapper extends BaseRowMapper<Folder> {

    @Override
    protected Folder mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        return new Folder(
                rs.getInt("FolderId"),
                rs.getInt("ParentId"),
                rs.getInt("OwnerId"),
                rs.getString("Name"),
                rs.getString("Path"),
                rs.getString("Status"),
                rs.getInt("Size"),
                rs.getTimestamp("CreatedAt").toInstant(),
                rs.getTimestamp("UpdatedAt").toInstant());
    }

}