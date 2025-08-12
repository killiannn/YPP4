package com.example.googledrive.service.interf;


import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.googledrive.entity.User;
import com.example.googledrive.entity.Folder;
import com.example.googledrive.entity.SearchHistory;

public interface QueryService {
    List<User> getUserInformation(int userId);
    List<Map<String , String>> getUserSetting(int userId);
    List<Map<String ,Object>> getRecentSuggestion(int userId, int limit);
    List<Folder> getUserFolders(int userId);
    List<Map<String , Object>> getUserFiles(int userId);
    List<Map<String , Object>> getStarredObject(int userId);
    List<Map<String , Object>> getRecentFiles(int userId);
    List<Map<String , Object>> getSharedWithMe(int userId);
    List<Map<String , Object>> getTrash(int userId);
    Map<String , Long> getStorageInfo (int userId);
    List<Map<String , Object>> getFilesBySize (int userId);
    List<Map<String , Object>> getProductsBought (int userId);
    List<SearchHistory> getSearchHistory (int userId);
    List<Map<String , Object>> getBannedUsers (int userId);
    List<Folder> getSubFolders (String pathSuffix);
    List<Map<String, Object>> searchRelevantFiles(Set<String> terms);



}

