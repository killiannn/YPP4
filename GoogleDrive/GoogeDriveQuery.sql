USE GoogleDrive;
GO

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
	f.ModifiedDate as FileUpdateTime
from [File] f
join [User] u on f.OwnerId = u.Id
where u.id = 1
	and f.Status = 'active'
order by f.ModifiedDate;

--get user information
select 
	u.Name as UserName,
	u.Email as UserEmail,
	u.LastLogin as UserLastLogin
from [User] u
where u.id = 1;

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