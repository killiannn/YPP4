package com.example.googledrive.service.interf;

import java.time.Instant;
import java.util.List;
import com.example.googledrive.entity.User;

public interface UserService {
    User createUser(String username, String email, Instant lastActive, Instant createdAt, 
        int UsedCapacity, int Capacity);

    User getUserById(int id);

    User getUserByEmail(String email);

    List<User> getAllUser();

    int updateUserById(int id, String username, String pictureUrl);

    int deleteUserById(int id);
}

