package com.ssd.koratuwabackend.repositories;

import com.ssd.koratuwabackend.beans.UserBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<UserBean, Integer> {

    UserBean getUserBeanByEmail(String email);

    UserBean getUserBeanById(Integer id);

    UserBean getUserBeanByIdAndDeletedIsFalse(Integer id);

    @Query("SELECT u FROM UserBean u WHERE u.email=:email AND u.type = 'farmer' " +
            "and u.registrationApproved = true and u.deleted = false ")
    UserBean getFarmerByEmail(String email);

    @Query("SELECT u FROM UserBean u WHERE u.email=:email AND u.type = 'customer' " +
            "and u.registrationApproved = true and u.deleted = false ")
    UserBean getCustomerByEmail(String email);

    @Query("SELECT u FROM UserBean u WHERE u.email=:email AND u.type = 'admin' " +
            "and u.registrationApproved = true and u.deleted = false ")
    UserBean getAdminByEmail(String email);

    @Query("SELECT u FROM UserBean u WHERE u.id=:uid and u.deleted = false and u.role='user'")
    UserBean getUserForUser(@Param("uid") Integer uid);

    @Query("SELECT u FROM UserBean u WHERE u.id=:id and u.deleted = false AND u.type='farmer' AND u.registrationApproved = true")
    UserBean getFarmerData(Integer id);

    @Query("SELECT u FROM UserBean u WHERE u.id=:id and u.deleted = false AND u.type='farmer' AND u.registrationApproved = false")
    UserBean getUnregisteredFarmerData(Integer id);

    @Query("SELECT u FROM UserBean u WHERE u.type = 'farmer' and u.registrationApproved = true and u.deleted = false ORDER BY u.id")
    Page<UserBean> getAllUsers(Pageable pageable);

    @Query("SELECT u FROM UserBean u WHERE u.type='farmer' and u.registrationApproved = true and u.deleted = false AND u.name LIKE %:searchText% ORDER BY u.id")
    Page<UserBean> getAllUsers(String searchText, Pageable pageable);

    @Query("SELECT u FROM UserBean u WHERE u.type = 'farmer' and u.registrationApproved = false " +
            "and u.deleted = false ORDER BY u.id")
    Page<UserBean> getRequestedFarmers(Pageable pageable);

    @Query("SELECT u FROM UserBean u WHERE u.type='farmer' and u.registrationApproved = false " +
            "and u.deleted = false AND u.name LIKE %:searchText% ORDER BY u.id")
    Page<UserBean> getRequestedFarmers(String searchText, Pageable pageable);
}
