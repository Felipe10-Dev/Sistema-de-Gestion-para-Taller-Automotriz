package com.serviteca.buscar.controller;

import com.serviteca.buscar.dto.GlobalSearchResult;
import com.serviteca.buscar.service.GlobalSearchService;
import com.serviteca.shared.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buscar")
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;

    public GlobalSearchController(GlobalSearchService globalSearchService) {
        this.globalSearchService = globalSearchService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GlobalSearchResult>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "all") String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GlobalSearchResult> results = globalSearchService.search(q, tipo, page, size);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
