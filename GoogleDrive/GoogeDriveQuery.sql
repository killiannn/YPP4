USE GoogleDrive;
GO

--Home
--get user information
select 
	u.Name as UserName,
	u.Email as UserEmail,
	u.LastLogin as UserLastLogin
from [User] u
where u.id = 1;


--My Drive
--get my folder
select
	f.Name as FolderName,
	f.Path as FolderPath,
	f.UpdatedAt as FolderUpdateTime
from Folder f
join [User] u on f.OwnerId = u.Id
where u.id = 1
	and f.Status = 'active'
order by f.UpdatedAt DESC;

--get my file
select 
	f.Name as [FileName],
	f.Path as FilePath,
	f.ModifiedDate as FileUpdateTime,
	ft.Name as FileType
from [File] f
join [User] u on f.OwnerId = u.Id
join FileType ft on f.FileTypeId = ft.Id
where u.id = 1
	and f.Status = 'active'
order by f.ModifiedDate;

--get user setting
select 
	u.Name as UserName,
	s.SettingKey,
	s.SettingValue
from SettingUser su
join [User] u on su.UserId = u.Id
join Setting s on su.SettingId = s.Id
where u.Id =1

--Starred
--get starred
SELECT 
    fo.ObjectId,
    fo.ObjectTypeId,
    ot.Name AS ObjectType,
	case
		when fo.ObjectTypeId = 1 then f.Name
		else fi.Name 
	end as [Name]
FROM FavoriteObject fo
JOIN [User] u ON fo.OwnerId = u.Id
JOIN ObjectType ot ON fo.ObjectTypeId = ot.Id
LEFT JOIN Folder f ON fo.ObjectId = f.Id AND fo.ObjectTypeId = 1
LEFT JOIN [File] fi ON fo.ObjectId = fi.Id AND fo.ObjectTypeId = 2
WHERE fo.OwnerId = 1
ORDER BY ot.Name;

--get recommend folder/file
select
	r.Id,
	case
		when r.ObjectTypeId = 1 then f.Name
		else fi.Name
	end as [Name],
	r.Log,
	r.DateTime
from Recent r
left join Folder f on f.Id = r.ObjectId and r.ObjectTypeId = 1
left join [File] fi on fi.Id = r.ObjectId and r.ObjectTypeId = 2
join [User] u on r.UserId = u.Id
where u.Id = 1
order by r.DateTime DESC;


--get recent
SELECT 
    r.Id,
    case
		when r.ObjectTypeId = 1 then f.Name
		else fi.Name
	end as [Name],
	case
		when r.ObjectTypeId = 1 then f.Path
		else fi.Path
	end as [Name],
    ft.Name AS FileType,
    r.DateTime AS LastAccessed
FROM Recent r
JOIN [User] u ON r.Id = u.Id
LEFT JOIN Folder f ON f.Id = r.ObjectId and r.ObjectTypeId = 1
LEFT JOIN [File] fi ON fi.Id = r.ObjectId and r.ObjectTypeId =2
JOIN FileType ft ON fi.FileTypeId = ft.Id
WHERE r.UserId = 1
ORDER BY r.DateTime DESC;

--get shared with me
SELECT 
    s.ObjectId,
    s.ObjectTypeId,
    ot.Name AS ObjectType,
	case
		when s.ObjectTypeId = 1 then f.Name
		else fi.Name
	end as [Name],
    p.Name AS Permission,
    u2.Name AS SharerName,
    s.CreatedAt AS ShareCreated,
    s.ExpiresAt AS ShareExpires
FROM SharedUser su
JOIN Share s ON su.ShareId = s.Id
JOIN [User] u ON su.Id = u.Id
JOIN [User] u2 ON s.Sharer = u2.Id
JOIN ObjectType ot ON s.ObjectTypeId = ot.Id
JOIN Permission p ON su.PermissionId = p.Id
LEFT JOIN Folder f ON s.ObjectId = f.Id AND s.ObjectTypeId = 1
LEFT JOIN [File] fi ON s.ObjectId = fi.Id AND s.ObjectTypeId = 2
WHERE su.UserId = 1
  AND s.ExpiresAt > GETDATE()
ORDER BY s.CreatedAt DESC;

--get trash
select 
	t.ObjectId,
	t.ObjectTypeId,
	t.RemovedDatetime,
	case
		when t.ObjectTypeId = 1 then f.Name
		else fi.Name
	end as [Name]
from Trash t
join [User] u on t.UserId = u.Id
join ObjectType ot on t.ObjectTypeId = ot.id
left join Folder f on t.ObjectId = f.Id and t.ObjectTypeId = 1
left join [File] fi on t.ObjectId = fi.Id and t.ObjectTypeId =2
where t.UserId = 1
order by t.RemovedDateTime DESC;

--get storage
select
	u.Capacity,
	u.UsedCapacity,
	u.Capacity - u.UsedCapacity as RemainingCapacity
from [User] u
where u.Id = 1
order by RemainingCapacity DESC;

--get list file by size
select
	f.Name as FileName,
	f.Size
from [File] f
join [User] u on f.OwnerId = u.Id
order by f.Size DESC;