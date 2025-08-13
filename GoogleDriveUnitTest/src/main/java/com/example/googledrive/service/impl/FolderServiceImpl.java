package com.example.googledrive.service.impl;

import com.example.googledrive.dto.FolderDTO;
import com.example.googledrive.domain.Folder;
import com.example.googledrive.service.mapper.dto.FolderDTOMapper;
import com.example.googledrive.repository.impl.FolderRepositoryImpl;
import com.example.googledrive.service.interf.FolderService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepositoryImpl folderRepository;
    private final FolderDTOMapper folderDTOMapper;

    public FolderServiceImpl(FolderRepositoryImpl folderRepository, FolderDTOMapper folderDTOMapper) {
        this.folderRepository = folderRepository;
        this.folderDTOMapper = folderDTOMapper;
    }

    @Override
    public FolderDTO createFolder(FolderDTO folderDTO) {
        if (folderDTO.getName() == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (folderDTO.getOwnerId() == null) {
            throw new IllegalArgumentException("OwnerId cannot be null");
        }
        if (folderDTO.getCreatedAt() == null) {
            folderDTO.setCreatedAt(Instant.now());
        }
        Folder folder = folderDTOMapper.toEntity(folderDTO);
        Folder createdFolder = folderRepository.create(folder);
        return folderDTOMapper.toDTO(createdFolder);
    }

    @Override
    public List<FolderDTO> getAllFolders() {
        return folderRepository.findAll().stream()
                .map(folderDTOMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FolderDTO getFolderById(Integer id) {
        Folder folder = folderRepository.findById(id);
        return folderDTOMapper.toDTO(folder);
    }

    @Override
    public FolderDTO getFolderByPath(String path) {
        Folder folder = folderRepository.findByPath(path);
        return folderDTOMapper.toDTO(folder);
    }

    @Override
    public FolderDTO updateFolderById(Integer id, FolderDTO folderDTO) {
        if (folderDTO.getName() == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        int rowsAffected = folderRepository.update(id, folderDTO.getName(), folderDTO.getPath());
        if (rowsAffected == 0) {
            throw new IllegalStateException("Folder not found with id: " + id);
        }
        Folder updatedFolder = folderRepository.findById(id);
        return folderDTOMapper.toDTO(updatedFolder);
    }

    @Override
    public void deleteFolderById(Integer id) {
        int rowsAffected = folderRepository.delete(id);
        if (rowsAffected == 0) {
            throw new IllegalStateException("Folder not found with id: " + id);
        }
    }
}