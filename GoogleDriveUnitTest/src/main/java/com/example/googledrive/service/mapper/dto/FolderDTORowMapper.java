package com.example.googledrive.service.mapper.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import com.example.googledrive.service.mapper.row.BaseRowMapper;
import org.springframework.stereotype.Component;

import com.example.googledrive.domain.Folder;
import com.example.googledrive.dto.FolderDTO;

@Component
public class FolderDTORowMapper extends BaseRowMapper<FolderDTO> {

    @Override
    protected FolderDTO mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        FolderDTO folderDTO =  new FolderDTO();
        folderDTO.setId(rs.getInt("Id"));
        folderDTO.setParentId(rs.getInt("ParentId"));
        folderDTO.setOwnerId(rs.getInt("OwnerId"));
        folderDTO.setName(rs.getString("Name"));
        folderDTO.setPath(rs.getString("Path"));
        folderDTO.setStatus(rs.getString("Status"));
        folderDTO.setSize(rs.getInt("Size"));
        folderDTO.setCreatedAt(rs.getObject("CreatedAt", Instant.class));
        folderDTO.setUpdatedAt(rs.getObject("UpdatedAt", Instant.class));
        return folderDTO;
    }

    public FolderDTO toDTO(Folder folder) {
        if (folder == null) {
            return null;
        }
        return new FolderDTO(
                folder.getId(),
                folder.getParentId(),
                folder.getOwnerId(),
                folder.getName(),
                folder.getPath(),
                folder.getStatus(),
                folder.getSize(),
                folder.getCreatedAt(),
                folder.getUpdatedAt()
        );
    }

    public Folder toEntity(FolderDTO folderDTO) {
        if (folderDTO == null) {
            return null;
        }
        Folder folder = new Folder(
            folderDTO.getId(),
            folderDTO.getParentId(),
            folderDTO.getOwnerId(),
            folderDTO.getName(),
            folderDTO.getPath(),
            folderDTO.getStatus(),
            folderDTO.getSize(),
            folderDTO.getCreatedAt(),
            folderDTO.getUpdatedAt()
        );
        return folder;
    }

}