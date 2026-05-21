package com.example.spareparts.service;

import com.example.spareparts.model.HistoryRecord;
import com.example.spareparts.model.Part;
import com.example.spareparts.repository.HistoryRepository;
import com.example.spareparts.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private PartRepository partRepo;

    @Autowired
    private HistoryRepository historyRepo;

    public List<Part> getAllParts(Long userId) {
        return partRepo.findByUserIdOrderByName(userId);
    }

    @Transactional
    public Part addOrRestock(Long userId, String name, int quantity, String unit) {
        Optional<Part> existing = partRepo.findByUserIdAndName(userId, name);
        Part part = existing.orElseGet(() -> {
            Part p = new Part();
            p.setUserId(userId);
            p.setName(name);
            p.setQuantity(0);
            return p;
        });

        if (unit != null && !unit.isBlank()) {
            part.setUnit(unit.trim());
        }
        part.setQuantity(part.getQuantity() + quantity);
        part = partRepo.save(part);

        HistoryRecord hr = new HistoryRecord();
        hr.setUserId(userId);
        hr.setPartName(name);
        hr.setType("IN");
        hr.setQuantity(quantity);
        hr.setRecordDate(LocalDate.now());
        historyRepo.save(hr);

        return part;
    }

    @Transactional
    public void consume(Long userId, String name, int quantity, LocalDate date) {
        Part part = partRepo.findByUserIdAndName(userId, name)
                .orElseThrow(() -> new RuntimeException("备件「" + name + "」不存在"));

        part.setQuantity(part.getQuantity() - quantity);
        partRepo.save(part);

        HistoryRecord hr = new HistoryRecord();
        hr.setUserId(userId);
        hr.setPartName(name);
        hr.setType("OUT");
        hr.setQuantity(quantity);
        hr.setRecordDate(date != null ? date : LocalDate.now());
        historyRepo.save(hr);
    }

    @Transactional
    public void deletePart(Long userId, Long id) {
        Part part = partRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("备件不存在"));
        if (!part.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        historyRepo.deleteByPartNameAndUserId(part.getName(), userId);
        partRepo.delete(part);
    }

    public List<HistoryRecord> getAllHistory(Long userId) {
        return historyRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void deleteHistory(Long userId, Long id) {
        HistoryRecord hr = historyRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        if (!hr.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        historyRepo.delete(hr);
    }

    public Map<String, Object> getSummary(Long userId) {
        List<Part> parts = partRepo.findByUserIdOrderByName(userId);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalTypes", parts.size());
        summary.put("totalQuantity", parts.stream().mapToInt(Part::getQuantity).sum());
        summary.put("lowStock", parts.stream().filter(p -> p.getQuantity() <= 2).count());
        return summary;
    }
}
