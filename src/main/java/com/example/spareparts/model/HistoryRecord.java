package com.example.spareparts.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "part_name", nullable = false, length = 100)
    private String partName;

    @Column(nullable = false, length = 3)
    private String type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
