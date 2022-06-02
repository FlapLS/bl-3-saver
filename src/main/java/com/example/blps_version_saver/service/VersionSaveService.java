package com.example.blps_version_saver.service;

import com.example.blps_version_saver.model.SectionEntity;
import com.example.blps_version_saver.model.VersionEntity;

import java.util.Map;

public interface VersionSaveService {
    void saveChanges(Map<String, Object> message) throws Exception;
//    void saveChangesByUnauthorizedUser(String newText, String ip, SectionEntity section) throws Exception;
}
