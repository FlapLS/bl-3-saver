package com.example.blps_version_saver.repository;

import com.example.blps_version_saver.model.VersionEntity;
import org.springframework.data.repository.CrudRepository;

public interface VersionRepo  extends CrudRepository<VersionEntity, Integer> {
}
