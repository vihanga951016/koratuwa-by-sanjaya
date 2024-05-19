package com.ssd.koratuwabackend.services;

import com.ssd.koratuwabackend.beans.OrderBean;
import com.ssd.koratuwabackend.beans.UserBean;
import com.ssd.koratuwabackend.beans.requests.PaginateRequest;
import com.ssd.koratuwabackend.beans.requests.Sorting;
import com.ssd.koratuwabackend.common.constants.ApplicationConstant;
import com.ssd.koratuwabackend.common.enums.JwtTypes;
import com.ssd.koratuwabackend.common.exceptions.KoratuwaAppExceptions;
import com.ssd.koratuwabackend.common.http.HttpResponse;
import com.ssd.koratuwabackend.common.security.impls.JwtUserDetailsService;
import com.ssd.koratuwabackend.repositories.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class OrderService {

    private static Logger logger = LogManager.getLogger(OrderService.class);

    @Value("${user.profile.storage.path}")
    private String profilePath;

    private final JwtUserDetailsService jwtUserDetailsService;

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public ResponseEntity getAllOrders(Integer farmerId, PaginateRequest paginateRequest,
                                      HttpServletRequest request) {
        try {

            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user, JwtTypes.admin);

            Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);

            if (!claims.get(ApplicationConstant.JWT_USER_ROLE, String.class).equals(JwtTypes.admin.name())) {

                UserBean farmer = userRepository.getFarmerData(claimedUserId);

                if (farmer == null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                            .responseFail("You are not a farmer"));
                }

                if (!farmerId.equals(farmer.getId())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                            .responseFail("You have no permission for do this"));
                }
            }

            Page<OrderBean> reportData = orderRepository.getOrdersListByFarmer(farmerId, PageRequest.of(paginateRequest.getPage(),
                    paginateRequest.getSize(), Sort.by(Sorting.getSort(paginateRequest.getSort()))));

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(reportData));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }
}
