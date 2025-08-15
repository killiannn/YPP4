package com.example.googledrive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.example.googledrive.controller.FolderController;
import com.example.googledrive.dto.FolderDTO;
import com.example.googledrive.service.interf.FolderService;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"/schema.sql", "/data.sql"})
public class FolderControllerIntegrationTest {

    @Autowired
    private FolderService folderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FolderController folderController;

    @BeforeEach
    void setUp() {
        // Clear database to ensure clean state
        jdbcTemplate.execute("DELETE FROM Folder");
        jdbcTemplate.execute("DELETE FROM Users");
        
        // Initialize controller with injected service
        folderController = new FolderController(folderService);
    }

    @Test
    void testGetFolderByOwnerId_Success() {
        ResponseEntity<FolderDTO> response = folderController.getFolderByOwnerId(1);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        FolderDTO folder = response.getBody();
        assertNotNull(folder);
        assertEquals(4, folder.getId());
        assertEquals("SubFolder3", folder.getName());
        assertEquals("/1/2/3/4", folder.getPath());
        assertEquals(0, folder.getSize());
    }

    @Test
    void testGetFolderByOwnerId_NotFound() {
        ResponseEntity<FolderDTO> response = folderController.getFolderByOwnerId(999);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testGetFolderByOwnerId_NullOwnerId() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> folderController.getFolderByOwnerId(null));
        assertTrue(exception.getMessage().contains("OwnerId cannot be null"));
    }
}