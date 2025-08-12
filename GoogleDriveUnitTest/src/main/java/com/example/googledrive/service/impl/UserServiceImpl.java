package com.example.googledrive.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.example.googledrive.entity.User;
import com.example.googledrive.service.interf.UserService;
import com.example.googledrive.service.mapper.UserRowMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    @Override
    public User createUser(String Username, String Email,String PasswordHash, Instant LastLogin, Instant CreatedAt,
            String PictureUrl, int UsedCapacity, int Capacity) {
        if (Username == null) {
            throw new IllegalArgumentException("Username cannot be null or empty for user");
        }
        if (Email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (CreatedAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        jdbcTemplate.update(
                "INSERT INTO Users (Username,Email,LastActive,CreatedAt,PictureUrl) VALUES (?,?,?,?,?,?,?,?)",
                Username, Email, PasswordHash, java.sql.Timestamp.from(LastLogin), java.sql.Timestamp.from(CreatedAt),
                PictureUrl, UsedCapacity, Capacity);
        return getUserById(jdbcTemplate.queryForObject(
                "SELECT Id FROM Users WHERE Email = ?",
                Integer.class, Email));
    }

    @Override
    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject(
                "SELECT Username,Email FROM Users WHERE Email = ?",
                userRowMapper,
                email);
    }

    @Override
    public List<User> getAllUser() {
        return jdbcTemplate.query("SELECT Username,Email From Users", userRowMapper);
    }

    @Override
    public List<User> getLastLogin() {
        return jdbcTemplate.query("SELECT LastLogin From Users ", userRowMapper);
    }

    @Override
    public int deleteUserById(int id) {
        return jdbcTemplate.update("DELETE FROM Users WHERE Id=?", userRowMapper, id);
    }

    @Override
    public User getUserById(int id) {

        return jdbcTemplate.queryForObject("SELECT Username,Email FROM Users Where Id=?", userRowMapper, id);
    }

    @Override
    public int updateUserById(int id, String username, String pictureUrl) {
        User currentUser = getUserById(id);
        currentUser.setUsername(username.isBlank() ? currentUser.getUsername() : username);
        currentUser.setPictureUrl(pictureUrl.isBlank() ? currentUser.getPictureUrl() : pictureUrl);
        return jdbcTemplate.update(
                "UPDATE Users Set Username = ?, PictureUrl=?  WHERE Id=?",
                userRowMapper,
                currentUser.getUsername(), currentUser.getPictureUrl(), currentUser.getId());
    }


}