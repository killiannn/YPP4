-- Insert test data into Users table
INSERT INTO Users (Id, Username, Email, PasswordHash, CreatedAt) VALUES
    (1, 'User1', 'user1@example.com', 'hash1', CURRENT_TIMESTAMP),
    (2, 'User2', 'user2@example.com', 'hash2', CURRENT_TIMESTAMP);

-- Insert test data into Folder table
INSERT INTO Folder (Id, ParentId, OwnerId, Name, Size, CreatedAt, Path, Status, UpdatedAt) VALUES
    (1, NULL, 1, 'RootFolder', 0, CURRENT_TIMESTAMP, '/1', 'active', TIMESTAMPADD(MINUTE, -3, CURRENT_TIMESTAMP)),
    (2, 1, 1, 'SubFolder1', 0, CURRENT_TIMESTAMP, '/1/2', 'active', TIMESTAMPADD(MINUTE, -2, CURRENT_TIMESTAMP)),
    (3, 2, 1, 'SubFolder2', 0, CURRENT_TIMESTAMP, '/1/2/3', 'active', TIMESTAMPADD(MINUTE, -1, CURRENT_TIMESTAMP)),
    (4, 3, 1, 'SubFolder3', 0, CURRENT_TIMESTAMP, '/1/2/3/4', 'active', CURRENT_TIMESTAMP),
    (5, NULL, 2, 'AnotherFolder', 0, CURRENT_TIMESTAMP, '/another/folder', 'active', CURRENT_TIMESTAMP);