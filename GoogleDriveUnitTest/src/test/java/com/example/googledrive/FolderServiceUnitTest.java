package com.example.googledrive;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.example.googledrive.dto.FolderDTO;
import com.example.googledrive.service.impl.FolderServiceImpl;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql")
public class FolderServiceUnitTest {

    @Autowired
    private FolderServiceImpl folderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Clear existing data to avoid conflicts
        jdbcTemplate.execute("DELETE FROM Folder");
        jdbcTemplate.execute("DELETE FROM Users");
    }

    @Test
    void testFindByOwnerId_Success() {
        Optional<FolderDTO> result = folderService.findByOwnerId(1);
        assertTrue(result.isPresent());
        FolderDTO folder = result.get();
        assertEquals(4, folder.getId());
        assertEquals("SubFolder3", folder.getName());
        assertEquals("/1/2/3/4", folder.getPath());
        assertEquals(0, folder.getSize());
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