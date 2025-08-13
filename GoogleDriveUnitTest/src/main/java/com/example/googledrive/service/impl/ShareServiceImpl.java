package com.example.googledrive.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.example.googledrive.entity.Share;
import com.example.googledrive.service.interf.ShareService;
import com.example.googledrive.service.mapper.ShareRowMapper;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    private final JdbcTemplate jdbcTemplate;
    private final ShareRowMapper shareRowMapper = new ShareRowMapper();

    @Override
    public Share getShareById(int id) {
        String sql = "SELECT * FROM Shares WHERE ShareId = ?";
        return jdbcTemplate.queryForObject(sql, shareRowMapper, id);
    }
    @Override
    public List<Share> getAllShare() {
        String sql = "SELECT * FROM Shares";
        return jdbcTemplate.query(sql, shareRowMapper);
    }
    @Override
    public int updateShareById(int id) {
        String sql = "UPDATE Shares SET Status = 'Updated' WHERE ShareId = ?";
        return jdbcTemplate.update(sql, id);
    }
    @Override
    public int deleteShareById(int id) {
        String sql = "DELETE FROM Shares WHERE ShareId = ?";
        return jdbcTemplate.update(sql, id);
    }
    @Override
    public Share createShare(Integer sharer, Integer objectId, Integer objectTypeId,
                             Instant createdAt, Instant expiredAt) {
        String sql = "INSERT INTO Shares (Sharer, ObjectId, ObjectTypeId, CreatedAt, ExpiredAt) " +
                     "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, sharer, objectId, objectTypeId, createdAt, expiredAt);
        String selectSql = "SELECT * FROM Shares WHERE Sharer = ? AND ObjectId = ? AND ObjectTypeId = ?";
        return jdbcTemplate.queryForObject(selectSql, shareRowMapper, sharer, objectId, objectTypeId);
    }
    @Override
    public List<Share> getSharesByObjectIdAndType(int objectId, int objectTypeId) {
        String sql = "SELECT * FROM Shares WHERE ObjectId = ? AND ObjectTypeId = ?";
        return jdbcTemplate.query(sql, shareRowMapper, objectId, objectTypeId);
    }
    @Override
    public List<Share> getSharesBySharerId(int sharerId) {
        String sql = "SELECT * FROM Shares WHERE Sharer = ?";
        return jdbcTemplate.query(sql, shareRowMapper, sharerId);
    }
    @Override
    public List<Share> getSharesByObjectId(int objectId) {
        String sql = "SELECT * FROM Shares WHERE ObjectId = ?";
        return jdbcTemplate.query(sql, shareRowMapper, objectId);
    }
    @Override
    public List<Share> getSharesByObjectTypeId(int objectTypeId) {
        String sql = "SELECT * FROM Shares WHERE ObjectTypeId = ?";
        return jdbcTemplate.query(sql, shareRowMapper, objectTypeId);
    }
    @Override
    public List<Share> getSharesBySharerIdAndObjectTypeId(int sharerId, int objectTypeId) {
        String sql = "SELECT * FROM Shares WHERE Sharer = ? AND ObjectTypeId = ?";
        return jdbcTemplate.query(sql, shareRowMapper, sharerId, objectTypeId);
    }
    @Override
    public List<Share> getSharesBySharerIdAndObjectId(int sharerId, int objectId) {
        String sql = "SELECT * FROM Shares WHERE Sharer = ? AND ObjectId = ?";
        return jdbcTemplate.query(sql, shareRowMapper, sharerId, objectId);
    }
}