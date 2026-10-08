package com.norival.norival_backend.domain.repository;

import com.norival.norival_backend.domain.model.VideoArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoArchiveRepository extends JpaRepository<VideoArchive, Long> {
}
