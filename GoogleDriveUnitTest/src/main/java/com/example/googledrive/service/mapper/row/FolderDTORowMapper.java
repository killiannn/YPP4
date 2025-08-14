package com.example.googledrive.service.mapper.row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import com.example.googledrive.service.mapper.row.BaseRowMapper;
import org.springframework.stereotype.Component;
import com.example.googledrive.dto.FolderDTO;

@Component
public class FolderDTORowMapper extends BaseRowMapper<FolderDTO> {

    @Override
    protected FolderDTO mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        FolderDTO folderDTO =  new FolderDTO();
        folderDTO.setId(rs.getInt("Id"));
        folderDTO.setName(rs.getString("Name"));
        folderDTO.setParentId(rs.getInt("ParentId"));
        folderDTO.setOwnerId(rs.getInt("OwnerId"));
        folderDTO.setPath(rs.getString("Path"));
        folderDTO.setStatus(rs.getString("Status"));
        folderDTO.setSize(rs.getInt("Size"));
        folderDTO.setCreatedAt(rs.getObject("CreatedAt", Instant.class));
        folderDTO.setUpdatedAt(rs.getObject("UpdatedAt", Instant.class));
        return folderDTO;
    }

}