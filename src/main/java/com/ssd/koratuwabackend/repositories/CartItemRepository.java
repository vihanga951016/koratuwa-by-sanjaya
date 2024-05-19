package com.ssd.koratuwabackend.repositories;

import com.ssd.koratuwabackend.beans.CartItemBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemBean, Integer> {

    List<CartItemBean> getAllByCartId(Integer cartId);

    CartItemBean getCartItemBeanById(Integer id);
}
