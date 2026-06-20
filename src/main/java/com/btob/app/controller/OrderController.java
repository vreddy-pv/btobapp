package com.btob.app.controller;

import com.btob.app.domain.entity.SalesOrder;
import com.btob.app.domain.repository.SalesOrderRepository;
import com.btob.app.dto.ApiResponse;
import com.btob.app.dto.OrderRequestDTO;
import com.btob.app.dto.SalesOrderDTO;
import com.btob.app.exception.ResourceNotFoundException;
import com.btob.app.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final SalesOrderRepository orderRepository;
    private final OrderService orderService;

    public OrderController(SalesOrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public ApiResponse<Page<SalesOrderDTO>> listOrders(
            @RequestParam(required = false) String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<SalesOrderDTO> result;

        if (accountNumber != null && !accountNumber.isBlank()) {
            result = orderRepository.findByAccountIdOrderByOrderDateDesc(
                            Long.parseLong(accountNumber), pageRequest)
                    .map(SalesOrderDTO::fromEntity);
        } else {
            result = orderRepository.findAll(pageRequest).map(SalesOrderDTO::fromEntity);
        }

        return ApiResponse.success(result);
    }

    @GetMapping("/{orderNumber}")
    public ApiResponse<SalesOrderDTO> getOrder(@PathVariable String orderNumber) {
        SalesOrder order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("SalesOrder", orderNumber));
        return ApiResponse.success(SalesOrderDTO.fromEntity(order));
    }

    @PostMapping
    public ApiResponse<SalesOrderDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        SalesOrderDTO order = orderService.createOrder(request);
        return ApiResponse.success(order);
    }

    @PatchMapping("/{orderNumber}/status")
    public ApiResponse<SalesOrderDTO> updateStatus(
            @PathVariable String orderNumber,
            @RequestBody StatusUpdateRequest request) {

        SalesOrder order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("SalesOrder", orderNumber));
        order.setStatus(SalesOrder.Status.valueOf(request.status.toUpperCase()));
        SalesOrder saved = orderRepository.save(order);
        return ApiResponse.success(SalesOrderDTO.fromEntity(saved));
    }

    public static class StatusUpdateRequest {
        public String status;
    }
}
