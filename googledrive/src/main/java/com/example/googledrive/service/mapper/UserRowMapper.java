package com.example.googledrive.service.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.googledrive.entity.User;

public class UserRowMapper extends BaseRowMapper<User> {

    @Override
    protected User mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getInt("Id"),
                rs.getString("Username"),
                rs.getString("Bio"),
                rs.getString("Email"),
                rs.getTimestamp("LastActive").toInstant(),
                rs.getTimestamp("CreatedAt").toInstant(),
                rowNum, rowNum, rs.getString("PictureUrl"));
    }

}