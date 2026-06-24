package com.visitor_x.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long totalVisitors;
    private long todayVisitors;
    private long thisWeekVisitors;
    private long thisMonthVisitors;
}