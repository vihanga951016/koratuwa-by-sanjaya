package com.ssd.koratuwabackend.common.security.impls;

import com.ssd.koratuwabackend.beans.UserBean;
import com.ssd.koratuwabackend.beans.UserLoginBean;
import com.ssd.koratuwabackend.common.constants.ApplicationConstant;
import com.ssd.koratuwabackend.common.enums.JwtTypes;
import com.ssd.koratuwabackend.common.exceptions.AuthorizationException;
import com.ssd.koratuwabackend.common.security.utils.JwtTokenUtil;
import com.ssd.koratuwabackend.repositories.UserLoginRepository;
import com.ssd.koratuwabackend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class JwtUserDetailsService implements UserDetailsService {

    private static Logger logger = LogManager.getLogger(JwtUserDetailsService.class);

    private final UserRepository userRepository;
    private final UserLoginRepository userLoginRepository;

    private final JwtTokenUtil tokenUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(Arrays.stream(JwtTypes.values()).anyMatch(jwtTypes -> username.equals(jwtTypes.name()))) {
            return new User(username, ApplicationConstant.JWT_PW, new ArrayList<>());
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    public Claims authenticate(HttpServletRequest request, JwtTypes... jwtTypes) throws AuthorizationException {
        logger.info(request.getRequestURI() + " | Access Rights | " + Arrays.toString(jwtTypes));
        String requestTokenHeader = request.getHeader(ApplicationConstant.AUTH_HEADER);

        String jwtToken = null;

        if(requestTokenHeader != null &&
                requestTokenHeader.startsWith(ApplicationConstant.AUTH_HEADER_PREFIX)) {
            jwtToken = requestTokenHeader.substring(ApplicationConstant.AUTH_HEADER_PREFIX.length());
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        if(tokenUtil.validateToken(jwtToken, jwtTypes)){
            return tokenUtil.getAllClaimsFromToken(jwtToken);
        }

        throw new AuthorizationException("You have no permissions");
    }

    public boolean editableAccessFilter(Claims claims, Integer incomingId) {
        Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);
        String role = claims.get(ApplicationConstant.JWT_USER_ROLE, String.class);

        logger.info("Claimed User         : " + claimedUserId);
        logger.info("Claimed User Role    : " + role);

        UserBean claimedUser = userRepository.getUserBeanById(claimedUserId);
        if (claimedUser == null) {
            logger.info("claimed user not found");
            return false;
        }

        if (!claimedUser.getRole().equals(role)) {
            logger.info("user role mismatch");
            return false;
        }

        UserLoginBean loginBean = userLoginRepository.getAlreadyLoginBean(claimedUserId);
        if (loginBean == null) {
            logger.info("user not login");
            return false;
        }

        if (!role.equals(JwtTypes.admin.name()) && incomingId != null) {
            if (!incomingId.equals(claimedUserId)) {
                logger.info(role + " has no access");
                return false;
            }
        }

        return true;
    }

    public boolean visibilityAccessFilter(Claims claims) {
        Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);
        String role = claims.get(ApplicationConstant.JWT_USER_ROLE, String.class);

        logger.info("Claimed User         : " + claimedUserId);
        logger.info("Claimed User Role    : " + role);

        UserBean claimedUser = userRepository.getUserBeanById(claimedUserId);
        if (claimedUser == null) {
            logger.info("claimed user not found");
            return false;
        }

        if (!claimedUser.getRole().equals(role)) {
            logger.info("user role mismatch");
            return false;
        }

        UserLoginBean loginBean = userLoginRepository.getAlreadyLoginBean(claimedUserId);
        if (loginBean == null) {
            logger.info("user not login");
            return false;
        }

        return true;
    }
}
