package com.example.googledrive.service.impl;

import com.example.googledrive.dto.FolderDTO;
import com.example.googledrive.domain.Folder;
import com.example.googledrive.service.mapper.dto.FolderDTORowMapper;

import com.example.googledrive.repository.impl.FolderRepositoryImpl;
import com.example.googledrive.service.interf.FolderService;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepositoryImpl folderRepository;
    private final FolderDTORowMapper folderDTOMapper;

    public FolderServiceImpl(FolderRepositoryImpl folderRepository, FolderDTORowMapper folderDTOMapper) {
        this.folderRepository = folderRepository;
        this.folderDTOMapper = folderDTOMapper;
    }

    @Override
    public Optional<FolderDTO> findByOwnerId(Integer ownerId) {
        Optional<Folder> folderOptional = folderRepository.findByOwnerId(ownerId);
        if (folderOptional.isPresent()) {
            Folder folder = folderOptional.get();
            FolderDTO folderDTO = folderDTOMapper.toDTO(folder);
            return Optional.of(folderDTO);
        }
        return Optional.empty();
        
        
    }
    
}