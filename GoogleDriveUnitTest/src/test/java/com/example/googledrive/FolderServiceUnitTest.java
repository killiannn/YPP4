package com.example.googledrive;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.example.googledrive.service.impl.FolderServiceImpl;
import com.example.googledrive.entity.Folder;

public class FolderServiceUnitTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private FolderServiceImpl folderServiceImpl;

    private Folder sampleFolder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleFolder = new Folder(
                1, null, 1, "testfolder", "/testfolder", "active",
                 0, Instant.now(), Instant.now());
    }

    @Test
    void testCreateFolder_Success() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Folder>>any(), eq("/testfolder")))
                .thenReturn(sampleFolder);
        // Mock the queryForObject(String, Class, Object...) overload for Integer return
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), any(Object[].class)))
                .thenReturn(1);
        Folder result = folderServiceImpl.createFolder(
                0, 1, "testfolder", "/testfolder", "active", 0, sampleFolder.getCreatedAt(), sampleFolder.getUpdatedAt());
        assertNotNull(result);
        assertEquals("testfolder", result.getName());
    }

    @Test
    void testCreateFolder_NullName() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> folderServiceImpl.createFolder(
                        0, 1, null, "/testfolder", "active", 0, Instant.now(), Instant.now()));
        assertTrue(ex.getMessage().contains("Name cannot be null"));
    }

    @Test
    void testCreateFolder_NullOwnerId() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> folderServiceImpl.createFolder(
                        0, 0, "testfolder", "/testfolder", "active", 0, Instant.now(), Instant.now()));
        assertTrue(ex.getMessage().contains("OwnerId cannot be null"));
    }

    @Test
    void testCreateFolder_NullCreatedAt() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> folderServiceImpl.createFolder(
                        0, 1, "testfolder", "/testfolder", "active", 0, null, Instant.now()));
        assertTrue(ex.getMessage().contains("CreatedAt cannot be null"));
    }
    

    @Test
    void testGetFolderByPath_Found() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Folder>>any(), eq("/testfolder")))
                .thenReturn(sampleFolder);
        Folder result = folderServiceImpl.getFolderByPath("/testfolder");
        assertNotNull(result);
        assertEquals("testfolder", result.getName());
    }

    @Test
    void testGetFolderByPath_NotFound() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Folder>>any(), eq("/unknown")))
                .thenThrow(new EmptyResultDataAccessException(1));
        assertThrows(EmptyResultDataAccessException.class, () -> folderServiceImpl.getFolderByPath("/unknown"));
    }

    @Test
    void testGetAllFolders() {
        List<Folder> folders = Arrays.asList(sampleFolder);
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Folder>>any())).thenReturn(folders);
        List<Folder> result = folderServiceImpl.getAllFolder();
        assertEquals(1, result.size());
        assertEquals("testfolder", result.get(0).getName());
    }

    @Test
    void testDeleteFolderById() {
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(1);
        int rows = folderServiceImpl.deleteFolderById(1);
        assertEquals(1, rows);
    }

    @Test
    void testGetFolderById_Found() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Folder>>any(), eq(1)))
                .thenReturn(sampleFolder);
        Folder result = folderServiceImpl.getFolderById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetFolderById_NotFound() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Folder>>any(), eq(2)))
                .thenThrow(new EmptyResultDataAccessException(1));
        assertThrows(EmptyResultDataAccessException.class, () -> folderServiceImpl.getFolderById(2));
    }

    @Test
    void testUpdateFolderById() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Folder>>any(), eq(1)))
                .thenReturn(sampleFolder);
        when(jdbcTemplate.update(anyString(), any(), any(), eq(1))).thenReturn(1);
        int rows = folderServiceImpl.updateFolderById(1, "updatedfolder");
        assertEquals(0, rows);
    }
}