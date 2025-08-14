package com.example.googledrive.repository.impl;


import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.googledrive.domain.Folder;
import com.example.googledrive.repository.interf.FolderRepository;
import com.example.googledrive.service.mapper.row.FolderRowMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepository {

    private final JdbcTemplate jdbcTemplate = null;
    private final FolderRowMapper folderRowMapper = null;
    
    @Override
    public Optional<Folder> findByOwnerId(Integer ownerId) {
        String sql = """
                    select
                        f.id,
                        f.Name as FolderName,
                        f.Path as FolderPath,
                        f.UpdatedAt as FolderUpdateTime,
                        f.Size
                    from Folder f
                    join Users u on f.OwnerId = u.Id
                    where u.id = ?
                        and f.Status = 'active'
                    order by f.UpdatedAt DESC
                    """;
        List<Folder> folders = jdbcTemplate.query(sql, folderRowMapper, ownerId);
        return folders.isEmpty() ? Optional.empty() : Optional.of(folders.get(0));
    }
}