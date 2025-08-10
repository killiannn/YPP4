package com.example.drive;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class QueryService {

    // --- In-memory datasets ---
    public List<User> users = new ArrayList<>();
    public List<Setting> settings = new ArrayList<>();
    public List<SettingUser> settingUsers = new ArrayList<>();
    public List<Folder> folders = new ArrayList<>();
    public List<File> files = new ArrayList<>();
    public List<FileType> fileTypes = new ArrayList<>();
    public List<ObjectType> objectTypes = new ArrayList<>();
    public List<Recent> recents = new ArrayList<>();
    public List<FavoriteObject> favoriteObjects = new ArrayList<>();
    public List<Share> shares = new ArrayList<>();
    public List<SharedUser> sharedUsers = new ArrayList<>();
    public List<Permission> permissions = new ArrayList<>();
    public List<Trash> trashes = new ArrayList<>();
    public List<Product> products = new ArrayList<>();
    public List<UserProduct> userProducts = new ArrayList<>();
    public List<Promotion> promotions = new ArrayList<>();
    public List<SearchHistory> searchHistories = new ArrayList<>();
    public List<BannedUser> bannedUsers = new ArrayList<>();
    public List<SearchIndex> searchIndices = new ArrayList<>();
    public List<TermBM25> termBM25s = new ArrayList<>();

    // 1. get user informations
    public Optional<User> getUserInfo(int userId) {
        return users.stream().filter(u -> u.id == userId).findFirst();
    }

    // 2. get user settings
    public List<Map<String, String>> getUserSettings(int userId) {
        return settingUsers.stream()
                .filter(su -> su.userId == userId)
                .map(su -> {
                    Setting s = settings.stream().filter(st -> st.id == su.settingId).findFirst().orElse(null);
                    User u = users.stream().filter(us -> us.id == userId).findFirst().orElse(null);
                    if (s != null && u != null) {
                        Map<String, String> row = new HashMap<>();
                        row.put("UserName", u.name);
                        row.put("SettingKey", s.key);
                        row.put("SettingValue", s.value);
                        return row;
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    // 3. get suggested folders/files of a user (Recent top N)
    public List<Map<String, Object>> getRecentSuggestions(int userId, int limit) {
        return recents.stream()
                .filter(r -> r.userId == userId)
                .sorted(Comparator.comparing((Recent r) -> r.dateTime).reversed())
                .limit(limit)
                .map(r -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("Id", r.id);
                    ObjectType ot = objectTypes.stream().filter(o -> o.id == r.objectTypeId).findFirst().orElse(null);
                    row.put("Type", ot != null ? ot.name : null);
                    String name = (r.objectTypeId == 1)
                            ? folders.stream().filter(f -> f.id == r.objectId).map(f -> f.name).findFirst().orElse(null)
                            : files.stream().filter(f -> f.id == r.objectId).map(f -> f.name).findFirst().orElse(null);
                    row.put("Name", name);
                    row.put("Log", r.log);
                    row.put("DateTime", r.dateTime);
                    return row;
                }).collect(Collectors.toList());
    }

    // 4. get folders of a user (My Drive)
    public List<Folder> getUserFolders(int userId) {
        return folders.stream()
                .filter(f -> f.ownerId == userId && "active".equals(f.status))
                .sorted(Comparator.comparing((Folder f) -> f.updatedAt).reversed())
                .collect(Collectors.toList());
    }

    // 5. get files of a user (My Drive)
    public List<Map<String, Object>> getUserFiles(int userId) {
        return files.stream()
                .filter(f -> f.ownerId == userId && "active".equals(f.status))
                .sorted(Comparator.comparing(f -> f.modifiedDate))
                .map(f -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("FileName", f.name);
                    row.put("FilePath", f.path);
                    row.put("FileUpdateTime", f.modifiedDate);
                    FileType ft = fileTypes.stream().filter(t -> t.id == f.fileTypeId).findFirst().orElse(null);
                    row.put("FileType", ft != null ? ft.name : null);
                    row.put("Icon", ft != null ? ft.icon : null);
                    row.put("Size", f.size);
                    return row;
                }).collect(Collectors.toList());
    }

    // 6. get starred objects of a user
    public List<Map<String, Object>> getStarredObjects(int userId) {
        return favoriteObjects.stream()
                .filter(fo -> fo.ownerId == userId)
                .map(fo -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ObjectId", fo.objectId);
                    ObjectType ot = objectTypes.stream().filter(o -> o.id == fo.objectTypeId).findFirst().orElse(null);
                    row.put("ObjectType", ot != null ? ot.name : null);
                    String name = (fo.objectTypeId == 1)
                            ? folders.stream().filter(f -> f.id == fo.objectId).map(f -> f.name).findFirst().orElse(null)
                            : files.stream().filter(f -> f.id == fo.objectId).map(f -> f.name).findFirst().orElse(null);
                    row.put("Name", name);
                    return row;
                }).collect(Collectors.toList());
    }

    // 7. get recent files of a user
    public List<Map<String, Object>> getRecentFiles(int userId) {
        return recents.stream()
                .filter(r -> r.userId == userId)
                .sorted(Comparator.comparing((Recent r) -> r.dateTime).reversed())
                .map(r -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("Name", (r.objectTypeId == 1)
                            ? folders.stream().filter(f -> f.id == r.objectId).map(f -> f.name).findFirst().orElse(null)
                            : files.stream().filter(f -> f.id == r.objectId).map(f -> f.name).findFirst().orElse(null));
                    row.put("Path", (r.objectTypeId == 1)
                            ? folders.stream().filter(f -> f.id == r.objectId).map(f -> f.path).findFirst().orElse(null)
                            : files.stream().filter(f -> f.id == r.objectId).map(f -> f.path).findFirst().orElse(null));
                    File file = files.stream().filter(f -> f.id == r.objectId).findFirst().orElse(null);
                    FileType ft = file != null
                            ? fileTypes.stream().filter(t -> t.id == file.fileTypeId).findFirst().orElse(null)
                            : null;
                    row.put("FileType", ft != null ? ft.name : null);
                    row.put("LastAccessed", r.dateTime);
                    return row;
                }).collect(Collectors.toList());
    }

    // 8. get shared objects with a user
    public List<Map<String, Object>> getSharedWithMe(int userId) {
        return sharedUsers.stream()
                .filter(su -> su.userId == userId)
                .map(su -> {
                    Share s = shares.stream().filter(sh -> sh.id == su.shareId).findFirst().orElse(null);
                    if (s == null || (s.expiresAt != null && s.expiresAt.isBefore(LocalDateTime.now()))) {
                        return null;
                    }
                    Map<String, Object> row = new HashMap<>();
                    ObjectType ot = objectTypes.stream().filter(o -> o.id == s.objectTypeId).findFirst().orElse(null);
                    row.put("ObjectType", ot != null ? ot.name : null);
                    row.put("Name", (s.objectTypeId == 1)
                            ? folders.stream().filter(f -> f.id == s.objectId).map(f -> f.name).findFirst().orElse(null)
                            : files.stream().filter(f -> f.id == s.objectId).map(f -> f.name).findFirst().orElse(null));
                    Permission p = permissions.stream().filter(pp -> pp.id == su.permissionId).findFirst().orElse(null);
                    row.put("Permission", p != null ? p.name : null);
                    User sharer = users.stream().filter(u -> u.id == s.sharer).findFirst().orElse(null);
                    row.put("SharerName", sharer != null ? sharer.name : null);
                    row.put("ShareCreated", s.createdAt);
                    row.put("ShareExpires", s.expiresAt);
                    return row;
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    // 9. get trash objects of a user
    public List<Map<String, Object>> getTrash(int userId) {
        return trashes.stream()
                .filter(t -> t.userId == userId)
                .sorted(Comparator.comparing((Trash t) -> t.removedDatetime).reversed())
                .map(t -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ObjectId", t.objectId);
                    row.put("Name", (t.objectTypeId == 1)
                            ? folders.stream().filter(f -> f.id == t.objectId).map(f -> f.name).findFirst().orElse(null)
                            : files.stream().filter(f -> f.id == t.objectId).map(f -> f.name).findFirst().orElse(null));
                    row.put("RemovedDatetime", t.removedDatetime);
                    return row;
                }).collect(Collectors.toList());
    }

    // 10. get storage information
    public Map<String, Long> getStorageInfo(int userId) {
        return users.stream()
                .filter(u -> u.id == userId)
                .findFirst()
                .map(u -> {
                    Map<String, Long> m = new HashMap<>();
                    m.put("Capacity", u.capacity);
                    m.put("UsedCapacity", u.usedCapacity);
                    m.put("RemainingCapacity", u.capacity - u.usedCapacity);
                    return m;
                }).orElse(Collections.emptyMap());
    }

    // 11. get files by size
    public List<Map<String, Object>> getFilesBySize(int userId) {
        return files.stream()
                .filter(f -> f.ownerId == userId)
                .sorted(Comparator.comparingLong((File f) -> f.size).reversed())
                .map(f -> Map.of("FileName", f.name, "Size", f.size))
                .collect(Collectors.toList());
    }

    // 12. get products bought by a user
    public List<Map<String, Object>> getProductsBought(int userId) {
        return userProducts.stream()
                .filter(up -> up.userId == userId)
                .map(up -> {
                    Product p = products.stream().filter(pr -> pr.id == up.productId).findFirst().orElse(null);
                    Promotion promo = promotions.stream().filter(pr -> Objects.equals(pr.id, up.promotionId)).findFirst().orElse(null);
                    User u = users.stream().filter(us -> us.id == up.userId).findFirst().orElse(null);
                    Map<String, Object> row = new HashMap<>();
                    row.put("ProductName", p != null ? p.name : null);
                    row.put("UserName", u != null ? u.name : null);
                    row.put("PayingDatetime", up.payingDatetime);
                    row.put("EndDatetime", up.endDatetime);
                    row.put("PromotionName", promo != null ? promo.name : null);
                    return row;
                }).collect(Collectors.toList());
    }

    // 13. get search history
    public List<SearchHistory> getSearchHistory(int userId) {
        return searchHistories.stream()
                .filter(sh -> sh.userId == userId)
                .sorted(Comparator.comparing((SearchHistory sh) -> sh.searchDatetime).reversed())
                .collect(Collectors.toList());
    }

    // 14. get users banned from a user
    public List<Map<String, Object>> getBannedUsers(int userId) {
        return bannedUsers.stream()
                .filter(bu -> bu.userId == userId)
                .sorted(Comparator.comparing((BannedUser bu) -> bu.bannedAt).reversed())
                .map(bu -> {
                    User banned = users.stream().filter(u -> u.id == bu.bannedUserId).findFirst().orElse(null);
                    Map<String, Object> row = new HashMap<>();
                    row.put("BannedUserName", banned != null ? banned.name : null);
                    row.put("BannedUserEmail", banned != null ? banned.email : null);
                    row.put("BannedAt", bu.bannedAt);
                    return row;
                }).collect(Collectors.toList());
    }

    // 15. get subfolders of a folder by path
    public List<Folder> getSubfolders(String pathSuffix) {
        return folders.stream()
                .filter(f -> f.path != null && f.path.endsWith(pathSuffix) && "active".equals(f.status))
                .collect(Collectors.toList());
    }

    // 16. get most relevant files by keyword
    public List<Map<String, Object>> searchRelevantFiles(Set<String> terms) {
        return searchIndices.stream()
                .filter(si -> terms.contains(si.term))
                .map(si -> {
                    TermBM25 bm = termBM25s.stream().filter(t -> t.term.equals(si.term)).findFirst().orElse(null);
                    Map<String, Object> row = new HashMap<>();
                    row.put("ObjectTypeId", si.objectTypeId);
                    row.put("ObjectId", si.objectId);
                    row.put("Term", si.term);
                    row.put("BM25", bm != null ? bm.bm25 : null);
                    return row;
                })
                .sorted(Comparator.comparing((Map<String, Object> r) -> (Double) r.get("BM25")).reversed())
                .collect(Collectors.toList());
    }
}
