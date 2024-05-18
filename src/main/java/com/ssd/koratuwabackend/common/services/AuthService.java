package com.ssd.koratuwabackend.common.services;

import com.ssd.koratuwabackend.beans.UserBean;
import com.ssd.koratuwabackend.common.constants.ApplicationConstant;
import com.ssd.koratuwabackend.common.enums.JwtTypes;
import com.ssd.koratuwabackend.common.security.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class AuthService {

    private final JwtTokenUtil jwtTokenUtil;
    private static Logger LOGGER = LogManager.getLogger(AuthService.class);

    public String createUserLoginToken(UserBean userBean, String password){
        Map<String, Object> claims = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        long expireTime = calendar.getTimeInMillis();
        LOGGER.info("User login expires on >>> " + calendar.getTime().toString());

        String userType = null;

        LOGGER.info(userBean.getRole() + " is trying to login");

        if (userBean.getRole().equals(JwtTypes.admin.name())){
            userType = JwtTypes.admin.name();
        } else if(userBean.getRole().equals(JwtTypes.user.name())) {
            userType = JwtTypes.user.name();
        } else {
            LOGGER.error("User type is not defined");
        }

        claims.put(ApplicationConstant.JWT_USER_ID, userBean.getId());
        claims.put(ApplicationConstant.JWT_USER_ROLE, userBean.getRole());

        return jwtTokenUtil.generateTokenWithExp
                (new User(userType, password, new ArrayList<>()), expireTime, claims);
    }

//    public AuthRequestBean userAuthorities(Claims claims, String accessKey,
//                                           Integer accessId) {
//
//        Integer claimedUser = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);
//
//        String userType = claims.getSubject();
//
//        if(userType.equals(JwtTypes.admin.name())) {
//            return new AuthRequestBean(ApplicationConstant.ALLOW,
//                    false, ApplicationConstant.ALL_PRIVILEGE);
//        } else {
//            UserPermissionsBean permission = permissionRepository
//                    .getEntityByUserIdAndAccessKey(claimedUser, accessKey, accessId);
//
//            if(permission == null) {
//
//                LOGGER.error("error");
//
//                return new AuthRequestBean(ApplicationConstant.NOT_ALLOWED,
//                        true, null);
//            }
//
//            return new AuthRequestBean(ApplicationConstant.ALLOW,
//                    false, permission.getAccessId());
//        }
//    }
}
