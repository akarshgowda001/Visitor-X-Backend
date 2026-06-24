package com.visitor_x.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ExportService {

    void exportVisitors(
            HttpServletResponse response);
    void autoSaveToFile();

}