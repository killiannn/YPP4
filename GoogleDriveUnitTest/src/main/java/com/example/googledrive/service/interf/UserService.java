package com.example.googledrive.service.interf;

import java.time.Instant;
import java.util.List;

import com.example.googledrive.domain.User;

public interface UserService {

    User getUserById(int id);

    User getUserByEmail(String email);

    List<User> getAllUser();

    int updateUserById(int id, String username, String pictureUrl);

    int deleteUserById(int id);

    User createUser(String Username, String Email, String PasswordHash, Instant LastLogin, Instant CreatedAt,
            String PictureUrl, int UsedCapacity, int Capacity);

	List<User> getLastLogin();

}

