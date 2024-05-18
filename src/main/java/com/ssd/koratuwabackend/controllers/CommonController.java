package com.ssd.koratuwabackend.controllers;

import com.ssd.koratuwabackend.beans.CategoryBean;
import com.ssd.koratuwabackend.common.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {

    private final CategoryService categoryService;

    @PostMapping("/category/add")
    public ResponseEntity addCategory(@RequestBody CategoryBean categoryBean, HttpServletRequest request) {
        return categoryService.add(categoryBean, request);
    }

    @PostMapping("/category/update")
    public ResponseEntity updateCategory(@RequestBody CategoryBean categoryBean, HttpServletRequest request) {
        return categoryService.update(categoryBean, request);
    }

    @PostMapping("/category/delete/{id}")
    public ResponseEntity deleteCategory(@PathVariable Integer id, HttpServletRequest request) {
        return categoryService.remove(id, request);
    }

    @GetMapping("/category/get-all")
    public ResponseEntity getAllCategories(HttpServletRequest request) {
        return categoryService.getAll(request);
    }
}
