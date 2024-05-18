package com.ssd.koratuwabackend.repositories;

import com.ssd.koratuwabackend.beans.CategoryBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryBean, Integer> {

    CategoryBean getCategoryBeanByNameAndDeletedIsFalse(String name);

    CategoryBean getCategoryBeanByIdAndDeletedIsFalse(Integer id);
}
