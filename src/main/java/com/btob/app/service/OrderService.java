package com.btob.app.service;

import com.btob.app.domain.entity.AutoPart;
import com.btob.app.domain.entity.B2BAccount;
import com.btob.app.domain.entity.OrderLineItem;
import com.btob.app.domain.entity.SalesOrder;
import com.btob.app.domain.repository.AutoPartRepository;
import com.btob.app.domain.repository.B2BAccountRepository;
import com.btob.app.domain.repository.SalesOrderRepository;
import com.btob.app.dto.OrderRequestDTO;
import com.btob.app.dto.SalesOrderDTO;
import com.btob.app.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderService {

    private final SalesOrderRepository orderRepository;
    private final B2BAccountRepository accountRepository;
    private final AutoPartRepository partRepository;

    public OrderService(SalesOrderRepository orderRepository,
                        B2BAccountRepository accountRepository,
                        AutoPartRepository partRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.partRepository = partRepository;
    }

    @Transactional
    public SalesOrderDTO createOrder(OrderRequestDTO request) {
        B2BAccount account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("B2BAccount", request.getAccountNumber()));

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        SalesOrder order = new SalesOrder(orderNumber, account);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderRequestDTO.OrderItemDTO item : request.getItems()) {
            AutoPart part = partRepository.findBySku(item.getSku())
                    .orElseThrow(() -> new ResourceNotFoundException("AutoPart", item.getSku()));

            part.reduceInventory(item.getQuantity());
            partRepository.save(part);

            OrderLineItem lineItem = new OrderLineItem(order, part, item.getQuantity(), part.getB2bPrice());
            order.addLineItem(lineItem);
            total = total.add(lineItem.getSubtotal());
        }

        account.setCurrentBalance(account.getCurrentBalance().add(total));
        accountRepository.save(account);

        SalesOrder savedOrder = orderRepository.save(order);
        return SalesOrderDTO.fromEntity(savedOrder);
    }
}
