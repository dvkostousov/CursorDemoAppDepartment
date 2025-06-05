package com.example.cursordemodepartmentapp.service;

import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import java.util.List;

public interface DepartmentService {
    DepartmentInfo getCard(Long id);
    Long createCard(DepartmentInfo card);
    void updateCard(DepartmentInfo card);
    void deleteCard(Long id);
    List<DepartmentInfo> getAllCards();
} 