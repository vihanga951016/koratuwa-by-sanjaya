package com.ssd.koratuwabackend.controllers;

import com.ssd.koratuwabackend.beans.requests.PaginateRequest;
import com.ssd.koratuwabackend.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrdersController {

    private final OrderService orderService;

    @PostMapping("/get-all/farmer/{farmerId}")
    public ResponseEntity getAllOrders(@PathVariable Integer farmerId, @RequestBody PaginateRequest paginateRequest,
                                       HttpServletRequest request) {
        return orderService.getAllOrders(farmerId, paginateRequest, request);
    }

}
