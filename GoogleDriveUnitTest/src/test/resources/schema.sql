CREATE TABLE Users (
    Id INT PRIMARY KEY,
    Username VARCHAR(50),
    Email VARCHAR(100),
    PasswordHash VARCHAR(255),
    CreatedAt TIMESTAMP
);

CREATE TABLE Folder (
    Id INT PRIMARY KEY,
    ParentId INT,
    OwnerId INT,
    Name VARCHAR(255),
    Path VARCHAR(255),
    Status VARCHAR(50),
    Size INT,
    CreatedAt TIMESTAMP,
    UpdatedAt TIMESTAMP,
    FOREIGN KEY (OwnerId) REFERENCES Users(Id)
);