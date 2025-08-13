package com.example.googledrive.service.mapper.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.googledrive.domain.File;

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