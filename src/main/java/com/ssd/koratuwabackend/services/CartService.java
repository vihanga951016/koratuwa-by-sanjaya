package com.ssd.koratuwabackend.services;

import com.ssd.koratuwabackend.beans.*;
import com.ssd.koratuwabackend.beans.requests.cart.AddToCartRequest;
import com.ssd.koratuwabackend.common.constants.ApplicationConstant;
import com.ssd.koratuwabackend.common.enums.JwtTypes;
import com.ssd.koratuwabackend.common.enums.OrderStatus;
import com.ssd.koratuwabackend.common.enums.OrderTypes;
import com.ssd.koratuwabackend.common.exceptions.KoratuwaAppExceptions;
import com.ssd.koratuwabackend.common.http.HttpResponse;
import com.ssd.koratuwabackend.common.security.impls.JwtUserDetailsService;
import com.ssd.koratuwabackend.repositories.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class CartService {

    private static Logger logger = LogManager.getLogger(CartService.class);
    private final JwtUserDetailsService jwtUserDetailsService;

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    public ResponseEntity addToCart(Integer customerId, AddToCartRequest addToCartRequest, HttpServletRequest request) {
        try {
            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user);

            Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);

            UserBean customer = userRepository.getCustomerData(claimedUserId);

            if (customer == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You are not a customer"));
            }

            if (!customer.getId().equals(customerId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You have no claims to do this task"));
            }

            ItemBean itemBean = itemRepository.getItemBeanById(addToCartRequest.getItemId());

            if (itemBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Item not found"));
            }

            if (itemBean.getTotalUnits() < addToCartRequest.getUnits()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Total units limit reached"));
            }

            if (!itemBean.isBulk() && itemBean.getMinimumPurchasableUnits() < addToCartRequest.getUnits()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Minimum units limit reached"));
            }

            if (itemBean.isBulk() != addToCartRequest.isBulk()) {
                if (itemBean.isBulk()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                            .responseFail("Purchase must be bulk"));
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                            .responseFail("Purchase must be retail"));
                }
            }

            CartBean previousCartBean = cartRepository.getCartBeanByCustomerIdAndCompletedIsFalse(customerId);

            if (previousCartBean != null) {
                CartItemBean cartItemBean = CartItemBean.builder()
                        .farmer(new UserBean(addToCartRequest.getFarmerId()))
                        .item(new ItemBean(addToCartRequest.getItemId()))
                        .cart(previousCartBean)
                        .bulk(addToCartRequest.isBulk())
                        .units(addToCartRequest.getUnits())
                        .totalPrice(addToCartRequest.getUnits() * itemBean.getUnitPrice())
                        .orderedDate(new Date())
                        .paymentMethod(OrderTypes.cash.name()).build();

                cartItemRepository.save(cartItemBean);

            } else {
                CartBean cartBean = CartBean.builder()
                        .customer(new UserBean(customerId))
                        .completed(false)
                        .build();

                CartBean savedBean = cartRepository.save(cartBean);

                CartItemBean cartItemBean = CartItemBean.builder()
                        .farmer(new UserBean(addToCartRequest.getFarmerId()))
                        .item(new ItemBean(addToCartRequest.getItemId()))
                        .cart(savedBean)
                        .bulk(addToCartRequest.isBulk())
                        .units(addToCartRequest.getUnits())
                        .totalPrice(addToCartRequest.getUnits() * itemBean.getUnitPrice())
                        .orderedDate(new Date())
                        .paymentMethod(OrderTypes.cash.name()).build();

                cartItemRepository.save(cartItemBean);
            }

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Item added to cart"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity removeItemsFromCart(Integer cartItemId, HttpServletRequest request) {
        try {
            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user);

            Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);

            UserBean customer = userRepository.getCustomerData(claimedUserId);

            if (customer == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You are not a customer"));
            }

            CartItemBean cartItemBean = cartItemRepository.getCartItemBeanById(cartItemId);

            if (cartItemBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Cart not found"));
            }

            if (cartItemBean.getCart().isCompleted()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Now you can't remove this item"));
            }

            cartItemRepository.deleteById(cartItemId);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Item removed"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    //todo: this service is temporary service for purchase service
    public ResponseEntity completeCart(Integer customerId, HttpServletRequest request) {
        try {

            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user);

            Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);

            UserBean customer = userRepository.getCustomerData(claimedUserId);

            if (customer == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You are not a customer"));
            }

            if (!customer.getId().equals(customerId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You have no claims to do this task"));
            }

            CartBean cartBean = cartRepository.getCartBeanByCustomerIdAndCompletedIsFalse(customerId);

            if (cartBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Cart not found"));
            }

            List<CartItemBean> cartItemsList = cartItemRepository.getAllByCartId(cartBean.getId());

            if (cartItemsList.size() == 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Cart is empty"));
            }

            List<OrderBean> ordersList = new ArrayList<>();

            for (CartItemBean cartItem: cartItemsList) {

                OrderBean orderBean = OrderBean.builder()
                        .name(cartItem.getItem().getTitle())
                        .orderedDate(cartItem.getOrderedDate())
                        .pricePerUnit(cartItem.getItem().getUnitPrice())
                        .quantity(cartItem.getUnits())
                        .totalPrice(cartItem.getUnits() * cartItem.getItem().getUnitPrice())
                        .paymentType(OrderTypes.cash.name())
                        .status(OrderStatus.pending.name())
                        .item(cartItem.getItem())
                        .customer(customer)
                        .farmer(cartItem.getFarmer()).build();

                ordersList.add(orderBean);

                ItemBean itemBean = itemRepository.getItemBeanById(cartItem.getItem().getId());

                if (itemBean.getTotalUnits() - orderBean.getQuantity() == 0) {
                    itemBean.setTotalUnits(itemBean.getTotalUnits() - orderBean.getQuantity());
                    itemBean.setDeleted(true);
                } else {
                    itemBean.setTotalUnits(itemBean.getTotalUnits() - orderBean.getQuantity());
                }

                itemRepository.save(itemBean);

            }

            cartBean.setCompleted(true);

            cartRepository.save(cartBean);

            orderRepository.saveAll(ordersList);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Thank you for purchase"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }
}
