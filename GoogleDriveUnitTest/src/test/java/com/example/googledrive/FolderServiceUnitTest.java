package com.example.googledrive;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.example.googledrive.dto.FolderDTO;
import com.example.googledrive.repository.impl.FolderRepositoryImpl;
import com.example.googledrive.service.impl.FolderServiceImpl;
import com.example.googledrive.service.interf.FolderService;
import com.example.googledrive.service.mapper.dto.FolderDTORowMapper;
import com.example.googledrive.service.mapper.row.FolderRowMapper;


@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.example.googledrive")
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class FolderServiceUnitTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FolderService folderService;
    private FolderDTORowMapper folderDTORowMapper;
    private FolderRowMapper folderRowMapper;

    @BeforeEach
    void setUp() {
        FolderRepositoryImpl folderRepository = new FolderRepositoryImpl();
        folderService = new FolderServiceImpl(folderRepository, folderDTORowMapper);
    }

    @Test
    void testFindByOwnerId_Success() {
        Optional<FolderDTO> result = folderService.findByOwnerId(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("testfolder", result.get().getName());
        assertEquals("/testfolder", result.get().getPath());
        assertEquals(100, result.get().getSize());
    }

    @Test
    void testFindByOwnerId_NotFound() {
        Optional<FolderDTO> result = folderService.findByOwnerId(999);
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByOwnerId_NullOwnerId() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> folderService.findByOwnerId(null));
        assertTrue(ex.getMessage().contains("OwnerId cannot be null"));
    }

}