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

import com.example.googledrive.service.impl.ShareServiceImpl;
import com.example.googledrive.entity.Share;

public class ShareServiceUnitTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private ShareServiceImpl shareServiceImpl;
    private Share sampleShare;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleShare = new Share(
                1, 1, 1, 1, Instant.now(), Instant.now(), "Active");
    }
    @Test
    void testCreateShare_Success() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any())).thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Share>>any(),
                eq(1), eq(1), eq(1))).thenReturn(sampleShare);
        Share result = shareServiceImpl.createShare(
                1, 1, 1, sampleShare.getCreatedAt(), sampleShare.getExpiredAt());
        assertNotNull(result);
        assertEquals(1, result.getSharer());
        assertEquals(1, result.getObjectId());
        assertEquals(1, result.getObjectTypeId());
    }
    @Test
    void testGetShareById_Success() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Share>>any(), eq(1)))
                .thenReturn(sampleShare);
        Share result = shareServiceImpl.getShareById(1);
        assertNotNull(result);
        assertEquals(1, result.getShareId());
        assertEquals(1, result.getSharer());
        assertEquals(1, result.getObjectId());
    }
    @Test
    void testGetShareById_NotFound() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Share>>any(), eq(1)))
                .thenThrow(new EmptyResultDataAccessException(1));
        Exception ex = assertThrows(EmptyResultDataAccessException.class, () -> {
            shareServiceImpl.getShareById(1);
        });
        assertEquals("Incorrect result size: expected 1, actual 0", ex.getMessage());
    }
    @Test
    void testGetAllShare_Success() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Share>>any()))
                .thenReturn(Arrays.asList(sampleShare));
        List<Share> result = shareServiceImpl.getAllShare();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleShare, result.get(0));
    }
    @Test
    void testUpdateShareById_Success() {
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(1);
        int result = shareServiceImpl.updateShareById(1);
        assertEquals(1, result);
    }
    @Test
    void testDeleteShareById_Success() {
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(1);
        int result = shareServiceImpl.deleteShareById(1);
        assertEquals(1, result);
    }
    @Test
    void testGetSharesByObjectIdAndType_Success() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Share>>any(), eq(1), eq(1)))
                .thenReturn(Arrays.asList(sampleShare));
        List<Share> result = shareServiceImpl.getSharesByObjectIdAndType(1, 1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleShare, result.get(0));
    }
    @Test
    void testGetSharesBySharerId_Success() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Share>>any(), eq(1)))
                .thenReturn(Arrays.asList(sampleShare));
        List<Share> result = shareServiceImpl.getSharesBySharerId(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleShare, result.get(0));
    }
    @Test
    void testGetSharesByObjectId_Success() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Share>>any(), eq(1)))
                .thenReturn(Arrays.asList(sampleShare));
        List<Share> result = shareServiceImpl.getSharesByObjectId(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleShare, result.get(0));
    }
    @Test
    void testGetSharesByObjectTypeId_Success() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Share>>any(), eq(1)))
                .thenReturn(Arrays.asList(sampleShare));
        List<Share> result = shareServiceImpl.getSharesByObjectTypeId(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleShare, result.get(0));
    }
    @Test
    void testGetSharesBySharerIdAndObjectTypeId_Success() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Share>>any(), eq(1), eq(1)))
                .thenReturn(Arrays.asList(sampleShare));
        List<Share> result = shareServiceImpl.getSharesBySharerIdAndObjectTypeId(1, 1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleShare, result.get(0));
    }
    @Test
    void testGetSharesBySharerIdAndObjectId_Success() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Share>>any(), eq(1), eq(1)))
                .thenReturn(Arrays.asList(sampleShare));
        List<Share> result = shareServiceImpl.getSharesBySharerIdAndObjectId(1, 1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleShare, result.get(0));
    }
    @Test
    void testCreateShare_InvalidInput() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            shareServiceImpl.createShare(null, 1, 1, Instant.now(), Instant.now());
        });
        assertTrue(ex.getMessage().contains("Sharer cannot be null"));
        ex = assertThrows(IllegalArgumentException.class, () -> {
            shareServiceImpl.createShare(1, null, 1, Instant.now(), Instant.now());
        });
        assertTrue(ex.getMessage().contains("ObjectId cannot be null"));
        ex = assertThrows(IllegalArgumentException.class, () -> {
            shareServiceImpl.createShare(1, 1, null, Instant.now(), Instant.now());
        });
        assertTrue(ex.getMessage().contains("ObjectTypeId cannot be null"));
        ex = assertThrows(IllegalArgumentException.class, () -> {
            shareServiceImpl.createShare(1, 1, 1, null, Instant.now());
        });
        assertTrue(ex.getMessage().contains("CreatedAt cannot be null"));
        ex = assertThrows(IllegalArgumentException.class, () -> {
            shareServiceImpl.createShare(1, 1, 1, Instant.now(), null);
        });
        assertTrue(ex.getMessage().contains("ExpiredAt cannot be null"));
    }

    
}