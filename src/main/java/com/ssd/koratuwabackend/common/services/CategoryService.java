package com.ssd.koratuwabackend.common.services;

import com.ssd.koratuwabackend.beans.CategoryBean;
import com.ssd.koratuwabackend.common.enums.JwtTypes;
import com.ssd.koratuwabackend.common.exceptions.KoratuwaAppExceptions;
import com.ssd.koratuwabackend.common.http.HttpResponse;
import com.ssd.koratuwabackend.common.security.impls.JwtUserDetailsService;
import com.ssd.koratuwabackend.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class CategoryService {

    private final JwtUserDetailsService jwtUserDetailsService;

    private final CategoryRepository categoryRepository;

    public ResponseEntity add(CategoryBean categoryBean, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.admin);

            CategoryBean existingCategory = categoryRepository
                    .getCategoryBeanByNameAndDeletedIsFalse(categoryBean.getName());

            if (existingCategory != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("This category is already existing"));
            }

            categoryRepository.save(categoryBean);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Category created"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity update(CategoryBean categoryBean, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.admin);

            CategoryBean existingCategory = categoryRepository
                    .getCategoryBeanByNameAndDeletedIsFalse(categoryBean.getName());

            if (existingCategory == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Category not found"));
            }

            if (!categoryBean.getName().equals("") && categoryBean.getName() != null &&
                    !categoryBean.getName().equals(existingCategory.getName())) {
                existingCategory.setName(categoryBean.getName());
            }

            categoryRepository.save(categoryBean);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Category updated"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity remove(Integer id, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.admin);

            CategoryBean existingCategory = categoryRepository
                    .getCategoryBeanByIdAndDeletedIsFalse(id);

            if (existingCategory == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Category not found"));
            }

            existingCategory.setDeleted(true);

            categoryRepository.save(existingCategory);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Category deleted"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity getAll(HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.admin);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(categoryRepository.findAll()));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

}
