
package com.example.googledrive;

import com.example.googledrive.entity.SharedUser;
import com.example.googledrive.repository.SharedUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
public class UserServiceUnitTest {

    @Autowired
    private SharedUserRepository sharedUserRepository;

    @Test
    void testPropagateFolderPermissions() {
        // Arrange: Update permission for RootFolder (ShareId=1, UserId=2) to 'contributor' (PermissionId=2)
        Integer shareId = 1; // RootFolder share
        Integer userId = 2;  // User2
        Integer newPermissionId = 2; // contributor

        // Act: Call stored procedure
        sharedUserRepository.propagateFolderPermissions(shareId, userId, newPermissionId);

        // Assert: Check that all subfolders (FolderId 2, 3, 4) have inherited the permission
        List<Integer> subFolderIds = List.of(2, 3, 4); // SubFolder1, SubFolder2, SubFolder3
        List<SharedUser> sharedUsers = sharedUserRepository.findByUserIdAndFolderIds(userId, subFolderIds);

        // Verify each subfolder has the updated permission
        assertEquals(3, sharedUsers.size(), "Should have permissions for all 3 subfolders");
        for (SharedUser su : sharedUsers) {
            assertEquals(newPermissionId, su.getSharedUserByPermissionId(2), 
                        "Subfolder permission should be updated to 'contributor'");
            assertEquals(userId, su.getUserId(), "UserId should match");
        }
    }

    @Test
    void testInvalidShareId() {
        // Arrange: Use an invalid ShareId
        Integer shareId = 999;
        Integer userId = 2;
        Integer permissionId = 2;

        // Act & Assert: Expect an exception
        assertThrows(Exception.class, () -> 
            sharedUserRepository.propagateFolderPermissions(shareId, userId, permissionId),
            "Should throw exception for invalid ShareId");
    }
}