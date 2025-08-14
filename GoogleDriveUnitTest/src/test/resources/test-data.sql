CREATE TABLE Users (
    Id INT PRIMARY KEY,
    Username VARCHAR(50),
    Email VARCHAR(100)
);

CREATE TABLE Folder (
    Id INT PRIMARY KEY,
    OwnerId INT,
    Name VARCHAR(255),
    Path VARCHAR(255),
    Status VARCHAR(50),
    Size INT,
    CreatedAt TIMESTAMP,
    UpdatedAt TIMESTAMP,
    FOREIGN KEY (OwnerId) REFERENCES Users(Id)
);

INSERT INTO Users (Id, Username, Email) VALUES (1, 'testuser', 'testuser@example.com');

INSERT INTO Folder (Id, OwnerId, Name, Path, Status, Size, CreatedAt, UpdatedAt)
VALUES (1, 1, 'testfolder', '/testfolder', 'active', 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);