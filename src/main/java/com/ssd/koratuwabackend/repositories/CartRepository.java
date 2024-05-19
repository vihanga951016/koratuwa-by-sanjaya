package com.ssd.koratuwabackend.repositories;

import com.ssd.koratuwabackend.beans.CartBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<CartBean, Integer> {

    CartBean getCartBeanByCustomerIdAndCompletedIsFalse(Integer customerId);
}
