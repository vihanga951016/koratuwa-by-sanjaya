package com.ssd.koratuwabackend.repositories;

import com.ssd.koratuwabackend.beans.ItemImagesBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImagesRepository extends JpaRepository<ItemImagesBean, Integer> {

    ItemImagesBean getItemImagesBeanByName(String name);

    ItemImagesBean getItemImagesBeanById(Integer id);

    @Query("SELECT new ItemImagesBean(i.id, i.name, i.imageUrl) FROM ItemImagesBean i " +
            "WHERE i.itemBean.id=:id")
    List<ItemImagesBean> getImagesByItemId(@Param("id") Integer id);
}
