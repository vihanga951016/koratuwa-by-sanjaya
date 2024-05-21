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

    @Query("SELECT i FROM ItemBean i WHERE i.id=:id AND i.disabled = false AND i.deleted = false")
    ItemBean getItemBeanById(@Param("id") Integer id);

    @Query("SELECT i FROM ItemBean i WHERE i.bulk=:bulk AND i.disabled = false AND i.deleted = false")
    List<ItemBean> getAllItemsFilterNormal(boolean bulk);

    @Query("SELECT i FROM ItemBean i WHERE i.bulk=:bulk AND i.farmer.name LIKE %:farmerName% AND i.disabled = false AND i.deleted = false")
    List<ItemBean> getAllItemsFilterFarmer(@Param("bulk") boolean bulk, @Param("farmerName") String farmerName);

    @Query("SELECT i FROM ItemBean i WHERE i.bulk=:bulk AND i.category.id=:categoryId AND i.disabled = false AND i.deleted = false")
    List<ItemBean> getAllItemsFilterCategory(@Param("bulk") boolean bulk, @Param("categoryId") Integer categoryId);

    @Query("SELECT i FROM ItemBean i WHERE i.bulk=:bulk AND i.farmer.name LIKE %:farmerName% " +
            "AND i.category.id=:categoryId AND i.disabled = false AND i.deleted = false")
    List<ItemBean> getAllItemsFilterFarmerAndCategory(@Param("bulk") boolean bulk, @Param("farmerName") String farmerName,
                                     @Param("categoryId") Integer categoryId);
}
