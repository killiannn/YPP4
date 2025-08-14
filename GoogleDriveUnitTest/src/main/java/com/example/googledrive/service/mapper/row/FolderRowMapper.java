package com.example.googledrive.service.mapper.row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.springframework.stereotype.Component;

import com.example.googledrive.domain.Folder;

@Component
public class FolderRowMapper extends BaseRowMapper<Folder> {

    @Override
    protected Folder mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        Folder folder = new Folder(
            rs.getInt("Id"),
            rs.getInt("ParentId"),
            rs.getInt("OwnerId"),
            rs.getString("Name"),
            rs.getString("Path"),
            rs.getString("Status"),
            rs.getInt("Size"),
            rs.getObject("CreatedAt", Instant.class),
            rs.getObject("UpdatedAt", Instant.class)
        );
        return folder;
    }
}