package com.ssd.koratuwabackend.repositories;

import com.ssd.koratuwabackend.beans.ItemBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemBean, Integer> {

    @Query("SELECT i FROM ItemBean i WHERE i.farmer.id=:id AND i.deleted = false")
    List<ItemBean> getAllItemsByFarmer(@Param("id") Integer id);

    @Query("SELECT i FROM ItemBean i WHERE i.deleted = false")
    List<ItemBean> getAllItems();

    ItemBean getItemBeanById(Integer id);
}
