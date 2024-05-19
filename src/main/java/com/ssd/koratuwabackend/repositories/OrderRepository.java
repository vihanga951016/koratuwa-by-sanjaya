package com.ssd.koratuwabackend.repositories;

import com.ssd.koratuwabackend.beans.OrderBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderBean, Integer> {

    @Query("SELECT o FROM OrderBean o WHERE o.farmer.id=:farmerId")
    Page<OrderBean> getOrdersListByFarmer(@Param("farmerId") Integer farmerId, Pageable pageable);

    @Query("SELECT o FROM OrderBean o WHERE o.farmer.id=:farmerId AND o.status = 'pending'")
    Page<OrderBean> getOrdersListByFarmerPending(@Param("farmerId") Integer farmerId, Pageable pageable);

    @Query("SELECT o FROM OrderBean o WHERE o.farmer.id=:farmerId AND o.status = 'completed'")
    Page<OrderBean> getOrdersListByFarmerCompleted(@Param("farmerId") Integer farmerId, Pageable pageable);

    @Query("SELECT o FROM OrderBean o WHERE o.farmer.id=:farmerId AND o.status = 'cancelled'")
    Page<OrderBean> getOrdersListByFarmerCancelled(@Param("farmerId") Integer farmerId, Pageable pageable);
}
