package com.ssd.koratuwabackend.controllers;

import com.ssd.koratuwabackend.services.ItemsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Controller
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemsService itemsService;

    @PostMapping("/add")
    public ResponseEntity addItem(String title, Integer categoryId, String description, Integer unitPrice,
                                  Integer totalUnits, boolean bulk, Integer minimumPurchasableUnits, boolean stockAvailable,
                                  @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderingDate,
                                  MultipartFile[] images, HttpServletRequest request) throws IOException {
        return itemsService.addItem(title, categoryId, description, unitPrice, totalUnits, bulk,
                minimumPurchasableUnits, stockAvailable, orderingDate, images, request);
    }

    @PostMapping("/update")
    public ResponseEntity updateItem(Integer id, String title, Integer categoryId, String description, Integer unitPrice,
                                  Integer totalUnits, boolean bulk, Integer minimumPurchasableUnits, boolean stockAvailable,
                                  @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderingDate,
                                  MultipartFile[] images, HttpServletRequest request) throws IOException {
        return itemsService.updateItem(id, title, categoryId, description, unitPrice, totalUnits, bulk,
                minimumPurchasableUnits, stockAvailable, orderingDate, images, request);
    }


    @GetMapping("/{id}/get-all")
    public ResponseEntity getItems(@PathVariable Integer id, HttpServletRequest request) {
        return itemsService.getAllItems(id, request);
    }

    @PostMapping("/remove/image/{id}")
    public ResponseEntity removeItemImage(@PathVariable Integer id, HttpServletRequest request) throws IOException {
        return itemsService.removeItmeImage(id, request);
    }
}
