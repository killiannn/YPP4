package com.example.googledrive.service.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.googledrive.entity.File;

public class FileRowMapper extends BaseRowMapper<File> {

    @Override
    protected File mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        return new File(
                rs.getInt("FileId"),
                rs.getInt("FolderId"),
                rs.getInt("OwnerId"),
                rs.getInt("Size"),
                rs.getString("Name"),
                rs.getString("Path"),
                rs.getInt("fileTypeId"),
                rs.getString("Status"),
                rs.getTimestamp("CreatedAt").toInstant(),
                rs.getTimestamp("ModifiedDate").toInstant());
    }

}