package com.visitor_x.service;

import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.dto.UpdateVisitorRequestDTO;
import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminDashboardService {

    DashboardResponse getDashboard();

    Page<VisitorResponseDTO> getAllVisitors(Pageable pageable);

    VisitorResponseDTO getVisitor(Long id);

    Page<VisitorResponseDTO> searchVisitors(String keyword, Pageable pageable);

    List<VisitorResponseDTO> getTodayVisitors();

    public String deleteVisitor(Long id);

    VisitorResponseDTO updateVisitor(Long id, UpdateVisitorRequestDTO requestDTO);


}