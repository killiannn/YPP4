package com.example.googledrive.service.mapper.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.googledrive.domain.User;

public class UserRowMapper extends BaseRowMapper<User> {

    @Override
    protected User mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getInt("Id"),
                rs.getString("Username"),
                rs.getString("PasswordHash"),
                rs.getString("Email"),
                rs.getTimestamp("LastActive").toInstant(),
                rs.getTimestamp("CreatedAt").toInstant(),
                rs.getInt("UsedCapacity"),
                rs.getInt("Capacity"),
                rs.getString("PictureUrl"));
    }

}