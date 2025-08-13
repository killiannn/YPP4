package com.example.googledrive.service.mapper.dto;

import com.example.googledrive.dto.FolderDTO;
import com.example.googledrive.domain.Folder;
import org.springframework.stereotype.Component;

@Component
public class FolderDTOMapper {

    public FolderDTO toDTO(Folder folder) {
        if (folder == null) {
            return null;
        }
        return new FolderDTO(
                folder.getId(),
                folder.getParentId(),
                folder.getOwnerId(),
                folder.getName(),
                folder.getSize(),
                folder.getPath(),
                folder.getStatus(),
                folder.getCreatedAt(),
                folder.getUpdatedAt()
        );
    }

    public Folder toEntity(FolderDTO folderDTO) {
        if (folderDTO == null) {
            return null;
        }
        return new Folder(
                folderDTO.getId(),
                folderDTO.getParentId(),
                folderDTO.getOwnerId(),
                folderDTO.getName(),
                folderDTO.getSize(),
                folderDTO.getPath(),
                folderDTO.getStatus(),
                folderDTO.getCreatedAt(),
                folderDTO.getUpdatedAt()
        );
    }
}