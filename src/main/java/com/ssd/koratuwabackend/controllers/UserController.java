package com.ssd.koratuwabackend.controllers;

import com.ssd.koratuwabackend.beans.UserBean;
import com.ssd.koratuwabackend.beans.requests.LoginRequest;
import com.ssd.koratuwabackend.beans.requests.PaginateRequest;
import com.ssd.koratuwabackend.beans.requests.PasswordResetRequest;
import com.ssd.koratuwabackend.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserServices userServices;

    @PostMapping("/login/farmer")
    public ResponseEntity farmerLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userServices.farmerLogin(loginRequest, request);
    }

    @PostMapping("/login/customer")
    public ResponseEntity customerLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userServices.customerLogin(loginRequest, request);
    }

    @PostMapping("/login/admin")
    public ResponseEntity adminLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userServices.adminLogin(loginRequest, request);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserBean userBean, HttpServletRequest request) {
        return userServices.register(userBean, request);
    }

    @PostMapping("/user-profile-update")
    public ResponseEntity profileImage(@RequestParam(value = "profileImage", required = false) MultipartFile file,
                                       HttpServletRequest request) throws IOException {
        return userServices.updateProfileImage(file, request);
    }

    @PostMapping("/update")
    public ResponseEntity update(@RequestBody UserBean userBean, HttpServletRequest request) {
        return userServices.updateUser(userBean, request);
    }

    @GetMapping("/get-user/{uid}")
    public ResponseEntity getUser(@PathVariable Integer uid, HttpServletRequest request) {
        return userServices.getUserData(uid, request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestBody PasswordResetRequest resetRequest, HttpServletRequest request) {
        return userServices.resetPassword(resetRequest, request);
    }

    @PostMapping("/{id}/logout")
    public ResponseEntity userLogout(@PathVariable Integer id, HttpServletRequest request) {
        return userServices.logout(id, request);
    }

    @PostMapping("/requesting-farmers")
    public ResponseEntity registrationApprovalList(@RequestBody PaginateRequest paginateRequest,
                                                   HttpServletRequest request) {
        return userServices.getRegistrationApprovalList(paginateRequest, request);
    }

    @PostMapping("/{id}/approve-registration")
    public ResponseEntity registrationApproval(@PathVariable Integer id, HttpServletRequest request) {
        return userServices.farmerRegistrationApproval(id, request);
    }

    @GetMapping("/{id}/get-farmer")
    public ResponseEntity getFarmer(@PathVariable Integer id, HttpServletRequest request) {
        return userServices.getFarmerDetails(id, request);
    }

    @PostMapping("/get-all-farmers")
    public ResponseEntity getAllFarmers(@RequestBody PaginateRequest paginateRequest, HttpServletRequest request) {
        return userServices.listOfFarmers(paginateRequest, request);
    }

    @PostMapping("/{id}/delete-user")
    public ResponseEntity deleteUser(@PathVariable Integer id, HttpServletRequest request) {
        return userServices.deleteUser(id, request);
    }
}
