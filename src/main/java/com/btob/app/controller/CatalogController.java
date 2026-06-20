package com.btob.app.controller;

import com.btob.app.domain.entity.AutoPart;
import com.btob.app.domain.repository.AutoPartRepository;
import com.btob.app.dto.ApiResponse;
import com.btob.app.dto.AutoPartDTO;
import com.btob.app.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final AutoPartRepository partRepository;

    public CatalogController(AutoPartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @GetMapping("/parts")
    public ApiResponse<Page<AutoPartDTO>> listParts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
        Page<AutoPartDTO> result;

        if (category != null && !category.isBlank()) {
            result = partRepository.findByCategoryIgnoreCase(category, pageRequest)
                    .map(AutoPartDTO::fromEntity);
        } else if (q != null && !q.isBlank()) {
            result = partRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            q, q, pageRequest)
                    .map(AutoPartDTO::fromEntity);
        } else {
            result = partRepository.findAll(pageRequest)
                    .map(AutoPartDTO::fromEntity);
        }

        return ApiResponse.success(result);
    }

    @GetMapping("/parts/{sku}")
    public ApiResponse<AutoPartDTO> getPart(@PathVariable String sku) {
        AutoPart part = partRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("AutoPart", sku));
        return ApiResponse.success(AutoPartDTO.fromEntity(part));
    }

    @GetMapping("/parts/{sku}/inventory")
    public ApiResponse<Integer> getInventory(@PathVariable String sku) {
        AutoPart part = partRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("AutoPart", sku));
        return ApiResponse.success(part.getInventoryLevel());
    }
}
