package com.ssd.koratuwabackend.controllers;

import com.ssd.koratuwabackend.beans.requests.cart.AddToCartRequest;
import com.ssd.koratuwabackend.services.CartService;
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
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add-to-cart/customer/{customerId}")
    public ResponseEntity addToCart(@PathVariable Integer customerId, @RequestBody AddToCartRequest addToCartRequest,
                                    HttpServletRequest request) {
        return cartService.addToCart(customerId, addToCartRequest, request);
    }

    @PostMapping("/remove/item/{cartItemId}")
    public ResponseEntity removeItemFromCart(@PathVariable Integer cartItemId, HttpServletRequest request) {
        return cartService.removeItemsFromCart(cartItemId, request);
    }
    
    @PostMapping("/complete-cart/customer/{customerId}")
    public ResponseEntity completeCart(@PathVariable Integer customerId, HttpServletRequest request) {
        return cartService.completeCart(customerId, request);
    }
}
