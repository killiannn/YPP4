package com.example.googledrive.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import com.example.googledrive.domain.SharedUser;

import java.util.List;

public interface SharedUserRepository extends JpaRepository<SharedUser, Integer> {
    @Procedure(procedureName = "sp_PropagateFolderPermissions")
    void propagateFolderPermissions(Integer shareId, Integer userId, Integer permissionId);

    @Query("SELECT su FROM SharedUser su WHERE su.userId = :userId AND su.shareId IN " +
           "(SELECT s.id FROM Share s WHERE s.objectTypeId = 1 AND s.objectId IN :folderIds)")
    List<SharedUser> findByUserIdAndFolderIds(Integer userId, List<Integer> folderIds);
}