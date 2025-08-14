package com.example.googledrive.service.mapper.row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import com.example.googledrive.service.mapper.row.BaseRowMapper;
import org.springframework.stereotype.Component;
import com.example.googledrive.domain.Folder;

@Component
public class FolderRowMapper extends BaseRowMapper<Folder> {

    @Override
    protected Folder mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        Folder folder =  new Folder();
        folder.setId(rs.getInt("Id"));
        folder.setName(rs.getString("Name"));
        folder.setParentId(rs.getInt("ParentId"));
        folder.setOwnerId(rs.getInt("OwnerId"));
        folder.setPath(rs.getString("Path"));
        folder.setStatus(rs.getString("Status"));
        folder.setSize(rs.getInt("Size"));
        folder.setCreatedAt(rs.getObject("CreatedAt", Instant.class));
        folder.setUpdatedAt(rs.getObject("UpdatedAt", Instant.class));
        return folder;
    }
}