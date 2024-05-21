package com.ssd.koratuwabackend.services;

import com.ssd.koratuwabackend.beans.UserBean;
import com.ssd.koratuwabackend.beans.UserLoginBean;
import com.ssd.koratuwabackend.beans.requests.LoginRequest;
import com.ssd.koratuwabackend.beans.requests.PaginateRequest;
import com.ssd.koratuwabackend.beans.requests.PasswordResetRequest;
import com.ssd.koratuwabackend.beans.requests.Sorting;
import com.ssd.koratuwabackend.beans.requests.user.FamersListRequest;
import com.ssd.koratuwabackend.beans.responses.LoginResponse;
import com.ssd.koratuwabackend.common.constants.ApplicationConstant;
import com.ssd.koratuwabackend.common.enums.JwtTypes;
import com.ssd.koratuwabackend.common.enums.UserTypes;
import com.ssd.koratuwabackend.common.exceptions.KoratuwaAppExceptions;
import com.ssd.koratuwabackend.common.http.HttpResponse;
import com.ssd.koratuwabackend.common.security.impls.JwtUserDetailsService;
import com.ssd.koratuwabackend.common.services.AuthService;
import com.ssd.koratuwabackend.common.utils.HashUtils;
import com.ssd.koratuwabackend.repositories.UserLoginRepository;
import com.ssd.koratuwabackend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class UserServices {

    private static Logger logger = LogManager.getLogger(UserServices.class);

    @Value("${user.profile.storage.path}")
    private String profilePath;

    private final AuthService authService;
    private final JwtUserDetailsService jwtUserDetailsService;

    private final UserRepository userRepository;
    private final UserLoginRepository userLoginRepository;

    public ResponseEntity farmerLogin(LoginRequest loginRequest, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.visitor);

            UserBean userBean = userRepository.getFarmerByEmail(loginRequest.getEmail());

            if (userBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Incorrect email"));
            }

            if (!HashUtils.checkEncrypted(loginRequest.getPassword(), userBean.getPassword())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Incorrect password"));
            }

            if (!userBean.isRegistrationApproved()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("User Registration Request is pending. Please wait"));
            }

            UserLoginBean alreadyLogin = userLoginRepository.getAlreadyLoginBean(userBean.getId());

            if (alreadyLogin != null) {
                alreadyLogin.setLogoutTime(new Date());
                userLoginRepository.save(alreadyLogin);
            }

            UserLoginBean loginBean = UserLoginBean.builder()
                    .loginTime(new Date())
                    .user(userBean)
                    .token(authService.createUserLoginToken(userBean, loginRequest.getPassword()))
                    .build();

            UserLoginBean savedLoginBean = userLoginRepository.save(loginBean);

            LoginResponse loginResponse = LoginResponse.builder()
                    .id(userBean.getId())
                    .address(userBean.getAddress())
                    .email(userBean.getEmail())
                    .phone(userBean.getPhone())
                    .name(userBean.getName())
                    .role(userBean.getRole())
                    .picture(userBean.getProfilePicture())
                    .loginTime(savedLoginBean.getLoginTime().toString())
                    .token(savedLoginBean.getToken()).build();

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(loginResponse));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity customerLogin(LoginRequest loginRequest, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.visitor);

            UserBean userBean = userRepository.getCustomerByEmail(loginRequest.getEmail());

            if (userBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Incorrect email"));
            }

            if (!HashUtils.checkEncrypted(loginRequest.getPassword(), userBean.getPassword())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Incorrect password"));
            }

            UserLoginBean alreadyLogin = userLoginRepository.getAlreadyLoginBean(userBean.getId());

            if (alreadyLogin != null) {
                alreadyLogin.setLogoutTime(new Date());
                userLoginRepository.save(alreadyLogin);
            }

            UserLoginBean loginBean = UserLoginBean.builder()
                    .loginTime(new Date())
                    .user(userBean)
                    .token(authService.createUserLoginToken(userBean, loginRequest.getPassword()))
                    .build();

            UserLoginBean savedLoginBean = userLoginRepository.save(loginBean);

            LoginResponse loginResponse = LoginResponse.builder()
                    .id(userBean.getId())
                    .address(userBean.getAddress())
                    .email(userBean.getEmail())
                    .phone(userBean.getPhone())
                    .name(userBean.getName())
                    .picture(userBean.getProfilePicture())
                    .role(userBean.getRole())
                    .loginTime(savedLoginBean.getLoginTime().toString())
                    .token(savedLoginBean.getToken()).build();

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(loginResponse));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity adminLogin(LoginRequest loginRequest, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.visitor);

            UserBean userBean = userRepository.getAdminByEmail(loginRequest.getEmail());

            if (userBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Incorrect email"));
            }

            if (!HashUtils.checkEncrypted(loginRequest.getPassword(), userBean.getPassword())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Incorrect password"));
            }

            UserLoginBean alreadyLogin = userLoginRepository.getAlreadyLoginBean(userBean.getId());

            if (alreadyLogin != null) {
                alreadyLogin.setLogoutTime(new Date());
                userLoginRepository.save(alreadyLogin);
            }

            UserLoginBean loginBean = UserLoginBean.builder()
                    .loginTime(new Date())
                    .user(userBean)
                    .token(authService.createUserLoginToken(userBean, loginRequest.getPassword()))
                    .build();

            UserLoginBean savedLoginBean = userLoginRepository.save(loginBean);

            LoginResponse loginResponse = LoginResponse.builder()
                    .id(userBean.getId())
                    .address(userBean.getAddress())
                    .email(userBean.getEmail())
                    .phone(userBean.getPhone())
                    .name(userBean.getName())
                    .picture(userBean.getProfilePicture())
                    .role(userBean.getRole())
                    .loginTime(savedLoginBean.getLoginTime().toString())
                    .token(savedLoginBean.getToken()).build();

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(loginResponse));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity register(UserBean userBean, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.visitor);

            List<UserBean> list = userRepository.findAll();

            if (list.size() > 0) {
                UserBean userObject = userRepository.getUserBeanByEmail(userBean.getEmail());

                if (userObject != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                            .responseFail("User is already exist"));
                }

                boolean approval = false;

                if (userBean.getType().equals(UserTypes.customer.name())) {
                    approval = true;
                }

                UserBean newUserBean = UserBean.builder()
                        .name(userBean.getName())
                        .address(userBean.getAddress())
                        .email(userBean.getEmail())
                        .phone(userBean.getPhone())
                        .password(HashUtils.hash(userBean.getPassword()))
                        .role(JwtTypes.user.name())
                        .type(userBean.getType())
                        .registrationApproved(approval).build();

                userRepository.save(newUserBean);

                return ResponseEntity.ok()
                        .body(new HttpResponse<>().responseOk("User registration done"));

            } else {

                UserBean adminBean = UserBean.builder()
                        .name(userBean.getName())
                        .address(userBean.getAddress())
                        .email(userBean.getEmail())
                        .phone(userBean.getPhone())
                        .password(HashUtils.hash(userBean.getPassword()))
                        .role(JwtTypes.admin.name())
                        .type(UserTypes.admin.name())
                        .registrationApproved(true).build();

                userRepository.save(adminBean);

                return ResponseEntity.ok()
                        .body(new HttpResponse<>().responseOk("Admin registration done"));
            }

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity updateProfileImage(MultipartFile file, HttpServletRequest request) throws IOException {
        try {
            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user, JwtTypes.admin);

            Integer uid = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);

            UserBean userBean = userRepository.getUserBeanByIdAndDeletedIsFalse(uid);

            if (userBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("User not found"));
            }

            saveProfileImage(userBean.getId(), userBean, file);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("User image update successfully"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity updateUser(UserBean userBean, HttpServletRequest request) {
        try {

            UserBean dbUser = userRepository.getUserBeanByIdAndDeletedIsFalse(userBean.getId());

            if (dbUser == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("User not found"));
            }

            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user, JwtTypes.admin);

            boolean accessStatus = jwtUserDetailsService.editableAccessFilter(claims, userBean.getId());

            if (!accessStatus) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You have no permission to do this task"));
            }

            boolean modified = false;

            if (userBean.getEmail() != null
                    && !userBean.getEmail().equals("")
                    && !userBean.getEmail().equals(dbUser.getEmail())) {
                modified = true;
                dbUser.setEmail(userBean.getEmail());
            }

            if (userBean.getName() != null
                    && !userBean.getName().equals("")
                    && !userBean.getName().equals(dbUser.getName())) {
                modified = true;
                dbUser.setName(userBean.getName());
            }

            if (userBean.getAddress() != null
                    && !userBean.getAddress().equals("")
                    && !userBean.getAddress().equals(dbUser.getAddress())) {
                modified = true;
                dbUser.setAddress(userBean.getAddress());
            }

            if (userBean.getPhone() != null
                    && !userBean.getPhone().equals("")
                    && !userBean.getPhone().equals(dbUser.getPhone())) {
                modified = true;
                dbUser.setPhone(userBean.getPhone());
            }

           if (modified) {
               userRepository.save(dbUser);
           }

           return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("User registration done"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity getUserData(Integer uid, HttpServletRequest request) {
        try {
            UserBean dbUser = userRepository.getUserBeanByIdAndDeletedIsFalse(uid);

            if (dbUser == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("User not found"));
            }

            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user, JwtTypes.admin);

            boolean accessStatus = jwtUserDetailsService.visibilityAccessFilter(claims);

            if (!accessStatus) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You have no permission to do this task"));
            }

            UserBean userBean;

            if (!claims.get(ApplicationConstant.JWT_USER_ROLE).equals(JwtTypes.admin.name())) {
                userBean = userRepository.getUserForUser(uid);
            } else {
                userBean = userRepository.getUserBeanByIdAndDeletedIsFalse(uid);
            }

            if (userBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("User not found"));
            }

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(userBean));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity resetPassword(PasswordResetRequest resetRequest, HttpServletRequest request) {
        try {
            UserBean dbUser = userRepository.getUserBeanByIdAndDeletedIsFalse(resetRequest.getId());

            if (dbUser == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("User not found"));
            }

            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user, JwtTypes.admin);

            boolean accessStatus = jwtUserDetailsService.editableAccessFilter(claims, dbUser.getId());

            if (!accessStatus) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You have no permission to do this task"));
            }

            if (!HashUtils.checkEncrypted(resetRequest.getOldPassword(), dbUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Incorrect password"));
            }

            dbUser.setPassword(HashUtils.hash(resetRequest.getNewPassword()));
            userRepository.save(dbUser);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Reset your password"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity logout(Integer uid, HttpServletRequest request) {
        try {
            UserBean dbUser = userRepository.getUserBeanByIdAndDeletedIsFalse(uid);

            if (dbUser == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("User not found"));
            }

            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user, JwtTypes.admin);

            boolean accessStatus = jwtUserDetailsService.editableAccessFilter(claims, dbUser.getId());

            if (!accessStatus) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You have no permission to do this task"));
            }

            UserLoginBean loginBean = userLoginRepository.getAlreadyLoginBean(uid);

            if (loginBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You are not login"));
            }

            loginBean.setLogoutTime(new Date());

            userLoginRepository.save(loginBean);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("User logout"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity getRegistrationApprovalList(PaginateRequest paginateRequest,
                                                      HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.admin);

            Page<UserBean> reportData;

            if (paginateRequest.getSearchText().equals("") && paginateRequest.getSearchText() == null) {
                reportData = userRepository.getRequestedFarmers(PageRequest.of(paginateRequest.getPage(),
                        paginateRequest.getSize(), Sort.by(Sorting.getSort(paginateRequest.getSort()))));
            } else {
                reportData = userRepository.getRequestedFarmers(paginateRequest.getSearchText(), PageRequest.of(paginateRequest.getPage(),
                        paginateRequest.getSize(), Sort.by(Sorting.getSort(paginateRequest.getSort()))));
            }

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(reportData));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity farmerRegistrationApproval(Integer id, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.admin);

            UserBean farmer = userRepository.getUnregisteredFarmerData(id);

            if (farmer == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Farmer not found"));
            }

            farmer.setRegistrationApproved(true);

            userRepository.save(farmer);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Farmer Registration Approved"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity getFarmerDetails(Integer id, HttpServletRequest request) {
        try {

            jwtUserDetailsService.authenticate(request, JwtTypes.visitor, JwtTypes.user,
                    JwtTypes.admin);

            UserBean userBean = userRepository.getFarmerData(id);

            if (userBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("farmer not found"));
            }

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(userBean));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity listOfFarmers(PaginateRequest paginateRequest, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.admin);

            Page<UserBean> reportData;

            if (paginateRequest.getSearchText().equals("") && paginateRequest.getSearchText() == null) {
                reportData = userRepository.getAllFarmers(PageRequest.of(paginateRequest.getPage(),
                        paginateRequest.getSize(), Sort.by(Sorting.getSort(paginateRequest.getSort()))));
            } else {
                reportData = userRepository.getAllFarmers(paginateRequest.getSearchText(), PageRequest.of(paginateRequest.getPage(),
                        paginateRequest.getSize(), Sort.by(Sorting.getSort(paginateRequest.getSort()))));
            }

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(reportData));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    private void saveProfileImage(Integer id, UserBean user, MultipartFile profileImage)
            throws IOException {
        if(!profileImage.isEmpty()) {

            String originalFilename  = StringUtils.cleanPath(profileImage.getOriginalFilename());
            String fileExtension = FilenameUtils.getExtension(originalFilename);
            String fileName = id + "." + fileExtension;

            byte[] bytes = profileImage.getBytes();

            Path folder = Paths.get(profilePath + "users\\", fileName)
                    .toAbsolutePath().normalize();

            Files.write(folder, bytes);

            if (user.getProfilePicture() != null) {
                Path previousFilePath = Paths.get(user.getProfilePicture());
                Files.deleteIfExists(previousFilePath);
            }

            String name = createImageName(id, fileExtension);

            user.setProfilePicture("http://localhost/img/koratuwa/users/" + name);
        }
    }

    private String createImageName(Integer id, String fileExtension){
        return id + "." + fileExtension;
    }

    public ResponseEntity deleteUser(Integer id, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.admin);

            UserBean userBean = userRepository.getUserBeanByIdAndDeletedIsFalse(id);

            if (userBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("user not found"));
            }

            userBean.setDeleted(true);

            userRepository.save(userBean);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("User deleted successfully."));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }
}
