package com.project_kata.global_invoices_kata_mngr.infrastructure.controller;

import com.project_kata.global_invoices_kata_mngr.domain.dto.DashboardSummary;
import com.project_kata.global_invoices_kata_mngr.domain.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return dashboardService.getSummary();
    }
}
