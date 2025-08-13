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

import com.example.googledrive.domain.File;
import com.example.googledrive.service.impl.FileServiceImpl;

public class FileServiceUnitTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private FileServiceImpl FileServiceImpl;

    private File sampleFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleFile = new File(
                1, 1, 1, 0, "testFile", "/testFile", 1, "active",
                Instant.now(), Instant.now());
    }

    @Test
    void testCreateFile_Success() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<File>>any(), eq("/testFile")))
                .thenReturn(sampleFile);
        // Mock the queryForObject(String, Class, Object...) overload for Integer return
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), any(Object[].class)))
                .thenReturn(1);
        File result = FileServiceImpl.createFile(
                1, 1, 1, "testFile", "/testFile", 1,  "active", sampleFile.getCreatedAt(), sampleFile.getModifiedDate());
        assertNotNull(result);
        assertEquals("testFile", result.getName());
    }

    @Test
    void testCreateFile_NullName() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> FileServiceImpl.createFile(
                        1, 1, 0, null, "/testFile", 1, "active", Instant.now(), Instant.now()));
                        
        assertTrue(ex.getMessage().contains("Name cannot be null"));
    }

    @Test
    void testCreateFile_NullOwnerId() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> FileServiceImpl.createFile(
                        1, 0, 0, "testFile", "/testFile", 1, "active", Instant.now(), Instant.now()));
        assertTrue(ex.getMessage().contains("OwnerId cannot be null"));
    }

    @Test
    void testCreateFile_NullCreatedAt() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> FileServiceImpl.createFile(
                        1, 1, 0, "testFile", "/testFile", 1, "active", null, Instant.now()));
                        
        assertTrue(ex.getMessage().contains("CreatedAt cannot be null"));
    }
    

    @Test
    void testGetFileByPath_Found() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<File>>any(), eq("/testFile")))
                .thenReturn(sampleFile);
        File result = FileServiceImpl.getFileByPath("/testFile");
        assertNotNull(result);
        assertEquals("testFile", result.getName());
    }

    @Test
    void testGetFileByPath_NotFound() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<File>>any(), eq("/unknown")))
                .thenThrow(new EmptyResultDataAccessException(1));
        assertThrows(EmptyResultDataAccessException.class, () -> FileServiceImpl.getFileByPath("/unknown"));
    }

    @Test
    void testGetAllFiles() {
        List<File> Files = Arrays.asList(sampleFile);
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<File>>any())).thenReturn(Files);
        List<File> result = FileServiceImpl.getAllFile();
        assertEquals(1, result.size());
        assertEquals("testFile", result.get(0).getName());
    }

    @Test
    void testDeleteFileById() {
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(1);
        int rows = FileServiceImpl.deleteFileById(1);
        assertEquals(1, rows);
    }

    @Test
    void testGetFileById_Found() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<File>>any(), eq(1)))
                .thenReturn(sampleFile);
        File result = FileServiceImpl.getFileById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetFileById_NotFound() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<File>>any(), eq(2)))
                .thenThrow(new EmptyResultDataAccessException(1));
        assertThrows(EmptyResultDataAccessException.class, () -> FileServiceImpl.getFileById(2));
    }

    @Test
    void testUpdateFileById() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<File>>any(), eq(1)))
                .thenReturn(sampleFile);
        when(jdbcTemplate.update(anyString(), any(), any(), eq(1))).thenReturn(1);
        int rows = FileServiceImpl.updateFileById(1, "updatedFile");
        assertEquals(0, rows);
    }
}