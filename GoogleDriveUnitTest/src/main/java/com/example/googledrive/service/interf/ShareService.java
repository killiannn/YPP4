package com.example.googledrive.service.interf;

import java.time.Instant;
import java.util.List;
import com.example.googledrive.entity.Share;

public interface ShareService {

    Share getShareById(int id);

    List<Share> getAllShare();

    int updateShareById(int id);

    int deleteShareById(int id);

    Share createShare(
            Integer sharer, Integer objectId, Integer objectTypeId,
            Instant createdAt, Instant expiredAt);

    List<Share> getSharesByObjectIdAndType(int objectId, int objectTypeId);

    List<Share> getSharesBySharerId(int sharerId); 
    List<Share> getSharesByObjectId(int objectId);

    List<Share> getSharesByObjectTypeId(int objectTypeId);
    List<Share> getSharesBySharerIdAndObjectTypeId(int sharerId, int objectTypeId);

    List<Share> getSharesBySharerIdAndObjectId(int sharerId, int objectId);
}

