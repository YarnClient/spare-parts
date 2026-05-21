package com.example.spareparts.repository;

import com.example.spareparts.model.HistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryRecord, Long> {

    List<HistoryRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Transactional
    void deleteByPartNameAndUserId(String partName, Long userId);
}
