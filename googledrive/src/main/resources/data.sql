-- Insert test data
INSERT INTO [User] (Name, Email, PasswordHash, CreatedAt) VALUES
    ('User1', 'user1@example.com', 'hash1', GETDATE()),
    ('User2', 'user2@example.com', 'hash2', GETDATE());

INSERT INTO Permission (Name) VALUES
    ('reader'), ('contributor'), ('owner');

INSERT INTO ObjectType (Name) VALUES
    ('folder'), ('file');

INSERT INTO Folder (ParentId, OwnerId, Name, Size, CreatedAt, Path, Status) VALUES
    (NULL, 1, 'RootFolder', 0, GETDATE(), '/1', 'active'), -- FolderId: 1
    (1, 1, 'SubFolder1', 0, GETDATE(), '/1/2', 'active'),   -- FolderId: 2
    (2, 1, 'SubFolder2', 0, GETDATE(), '/1/2/3', 'active'), -- FolderId: 3
    (3, 1, 'SubFolder3', 0, GETDATE(), '/1/2/3/4', 'active'); -- FolderId: 4

INSERT INTO Share (Sharer, ObjectId, ObjectTypeId, CreatedAt) VALUES
    (1, 1, 1, GETDATE()), -- ShareId: 1 (RootFolder)
    (1, 2, 1, GETDATE()), -- ShareId: 2 (SubFolder1)
    (1, 3, 1, GETDATE()); -- ShareId: 3 (SubFolder2)

INSERT INTO SharedUser (ShareId, UserId, PermissionId) VALUES
    (1, 2, 1), -- User2 has 'reader' on RootFolder
    (2, 2, 1), -- User2 has 'reader' on SubFolder1
    (3, 2, 1); -- User2 has 'reader' on SubFolder2