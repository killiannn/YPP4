package com.example.googledrive.service.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.googledrive.entity.Share;

public class ShareRowMapper extends BaseRowMapper<Share> {

    @Override
    protected Share mapRowInternal(ResultSet rs, int rowNum) throws SQLException {
        return new Share(
                rs.getInt("ShareId"),
                rs.getInt("Sharer"),
                rs.getInt("ObjectId"),
                rs.getInt("ObjectTypeId"),
                rs.getTimestamp("CreatedAt").toInstant(),
                rs.getTimestamp("ExpiredAt") != null ? rs.getTimestamp("ExpiredAt").toInstant() : null);
    }

}