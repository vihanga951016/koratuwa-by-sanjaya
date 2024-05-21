package com.ssd.koratuwabackend.services;

import com.ssd.koratuwabackend.beans.CategoryBean;
import com.ssd.koratuwabackend.beans.ItemBean;
import com.ssd.koratuwabackend.beans.ItemImagesBean;
import com.ssd.koratuwabackend.beans.UserBean;
import com.ssd.koratuwabackend.beans.requests.GetItemsRequest;
import com.ssd.koratuwabackend.beans.responses.GetItemsResponse;
import com.ssd.koratuwabackend.common.constants.ApplicationConstant;
import com.ssd.koratuwabackend.common.enums.JwtTypes;
import com.ssd.koratuwabackend.common.exceptions.KoratuwaAppExceptions;
import com.ssd.koratuwabackend.common.http.HttpResponse;
import com.ssd.koratuwabackend.common.security.impls.JwtUserDetailsService;
import com.ssd.koratuwabackend.repositories.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class ItemsService {

    private static Logger logger = LogManager.getLogger(ItemsService.class);

    @Value("${user.profile.storage.path}")
    private String profilePath;

    private final JwtUserDetailsService jwtUserDetailsService;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemImagesRepository itemImagesRepository;


    public ResponseEntity addItem(String title, Integer categoryId, String description, Integer unitPrice,
                                  Integer totalUnits, boolean bulk, Integer minimumPurchasableUnits,
                                  boolean stockAvailable, Date orderingDate,
                                  MultipartFile[] images, HttpServletRequest request) throws IOException {
        try {
            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user);

            Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);

            UserBean farmer = userRepository.getFarmerData(claimedUserId);

            if (farmer == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You are not a farmer"));
            }

            CategoryBean categoryBean = categoryRepository.getCategoryBeanByIdAndDeletedIsFalse(categoryId);

            if (categoryBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Category not found"));
            }

            ItemBean itemBean = ItemBean.builder()
                    .title(title)
                    .category(categoryBean)
                    .description(description)
                    .unitPrice(unitPrice)
                    .totalUnits(totalUnits)
                    .bulk(bulk)
                    .minimumPurchasableUnits(bulk ? null : minimumPurchasableUnits)
                    .stockAvailable(stockAvailable)
                    .orderingDate(stockAvailable ? null : orderingDate)
                    .farmer(farmer)
                    .disabled(false)
                    .deleted(false).build();

            itemRepository.save(itemBean);

            saveImages(itemBean, images);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Item added"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity updateItem(Integer id, String title, Integer categoryId, String description, Integer unitPrice,
                                  Integer totalUnits, boolean bulk, Integer minimumPurchasableUnits,
                                  boolean stockAvailable, Date orderingDate,
                                  MultipartFile[] images, HttpServletRequest request) throws IOException {
        try {
            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user);

            Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);

            logger.info(orderingDate.toString());

            UserBean farmer = userRepository.getFarmerData(claimedUserId);

            if (farmer == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You are not a farmer"));
            }

            CategoryBean categoryBean = categoryRepository.getCategoryBeanByIdAndDeletedIsFalse(categoryId);

            if (categoryBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Category not found"));
            }

            ItemBean dbItem = itemRepository.getItemBeanById(id);

            if (dbItem == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Item not found"));
            }

            if (title != null && !title.equals("") && !title.equals(dbItem.getTitle())) {
                dbItem.setTitle(title);
            }

            if (!categoryId.equals(dbItem.getCategory().getId())) {
                dbItem.setCategory(new CategoryBean(categoryId));
            }

            if (description != null && !description.equals("") && !description.equals(dbItem.getDescription())) {
                dbItem.setDescription(description);
            }

            if (unitPrice != null && !unitPrice.equals(dbItem.getUnitPrice())) {
                dbItem.setUnitPrice(unitPrice);
            }

            if (totalUnits != null && !totalUnits.equals(dbItem.getTotalUnits())) {
                dbItem.setTotalUnits(totalUnits);
            }

            if (title != null && title.equals("") && !title.equals(dbItem.getTitle())) {
                dbItem.setTitle(title);
            }

            if (dbItem.isBulk() != bulk) {
                dbItem.setBulk(bulk);
            }

            if (minimumPurchasableUnits != null &&
                    !minimumPurchasableUnits.equals(dbItem.getMinimumPurchasableUnits())) {
                dbItem.setMinimumPurchasableUnits(minimumPurchasableUnits);
            }

            if (dbItem.isStockAvailable() != stockAvailable) {
                dbItem.setStockAvailable(stockAvailable);
                if (stockAvailable) {
                    dbItem.setOrderingDate(null);
                } else {
                    dbItem.setOrderingDate(orderingDate);
                }
            }

            itemRepository.save(dbItem);

            saveImages(dbItem, images);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Item added"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity removeItmeImage (Integer imageId, HttpServletRequest request) throws IOException {
        try {
            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user);

            Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);

            UserBean claimedFarmer = userRepository.getFarmerData(claimedUserId);

            if (claimedFarmer == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You are not registered user"));
            }

            ItemImagesBean itemImagesBean = itemImagesRepository.getItemImagesBeanById(imageId);

            if (itemImagesBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Image not found"));
            }

            if (!itemImagesBean.getItemBean().getFarmer().getId().equals(claimedUserId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You have no claims to do this task"));
            }

            deleteImage(itemImagesBean);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Image removed"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity getAllItems(Integer id, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.visitor, JwtTypes.user,
                    JwtTypes.admin);

            List<GetItemsResponse> responseList = new ArrayList<>();

            List<ItemBean> list = itemRepository.getAllItemsByFarmer(id);

            for (ItemBean item: list) {
                List<ItemImagesBean> imagesList = itemImagesRepository.getImagesByItemId(item.getId());

                GetItemsResponse getItemsResponse = GetItemsResponse.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .categoryId(item.getCategory().getId())
                        .categoryName(item.getCategory().getName())
                        .description(item.getDescription())
                        .unitPrice(item.getUnitPrice())
                        .totalUnits(item.getTotalUnits())
                        .bulk(item.isBulk())
                        .minimumPurchasableUnits(item.getMinimumPurchasableUnits())
                        .stockAvailable(item.isStockAvailable())
                        .orderingDate(item.getOrderingDate() != null ? item.getOrderingDate().toString() : null)
                        .farmer(item.getFarmer())
                        .disabled(item.isDisabled())
                        .deleted(item.isDeleted())
                        .imagesList(imagesList).build();

                responseList.add(getItemsResponse);
            }

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(responseList));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity getAllItems(HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.visitor, JwtTypes.user,
                    JwtTypes.admin);

            List<GetItemsResponse> responseList = new ArrayList<>();

            List<ItemBean> list = itemRepository.getAllItems();

            for (ItemBean item: list) {
                List<ItemImagesBean> imagesList = itemImagesRepository.getImagesByItemId(item.getId());

                GetItemsResponse getItemsResponse = GetItemsResponse.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .categoryId(item.getCategory().getId())
                        .categoryName(item.getCategory().getName())
                        .description(item.getDescription())
                        .unitPrice(item.getUnitPrice())
                        .totalUnits(item.getTotalUnits())
                        .bulk(item.isBulk())
                        .minimumPurchasableUnits(item.getMinimumPurchasableUnits())
                        .stockAvailable(item.isStockAvailable())
                        .orderingDate(item.getOrderingDate() != null ? item.getOrderingDate().toString() : null)
                        .farmer(item.getFarmer())
                        .disabled(item.isDisabled())
                        .deleted(item.isDeleted())
                        .imagesList(imagesList).build();

                responseList.add(getItemsResponse);
            }

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(responseList));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity getItemById(Integer id, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.visitor, JwtTypes.user,
                    JwtTypes.admin);

            ItemBean item = itemRepository.getItemBeanById(id);

            if (item == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Image not found"));
            }

            List<ItemImagesBean> imagesList = itemImagesRepository.getImagesByItemId(item.getId());

            GetItemsResponse getItemsResponse = GetItemsResponse.builder()
                    .id(item.getId())
                    .title(item.getTitle())
                    .categoryId(item.getCategory().getId())
                    .categoryName(item.getCategory().getName())
                    .description(item.getDescription())
                    .unitPrice(item.getUnitPrice())
                    .totalUnits(item.getTotalUnits())
                    .bulk(item.isBulk())
                    .minimumPurchasableUnits(item.getMinimumPurchasableUnits())
                    .stockAvailable(item.isStockAvailable())
                    .orderingDate(item.getOrderingDate() != null ? item.getOrderingDate().toString() : null)
                    .farmer(item.getFarmer())
                    .disabled(item.isDisabled())
                    .deleted(item.isDeleted())
                    .imagesList(imagesList).build();


            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(getItemsResponse));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    private void saveImages(ItemBean itemBean, MultipartFile[] images) throws IOException {
        if (images != null) {
            for (MultipartFile image: images) {
                String originalFilename  = StringUtils.cleanPath(image.getOriginalFilename());
                String fileExtension = FilenameUtils.getExtension(originalFilename);
                String fileName = originalFilename + "." + fileExtension;

                byte[] bytes = image.getBytes();

                Path folder = Paths.get(profilePath + "items\\", fileName)
                        .toAbsolutePath().normalize();

                Files.write(folder, bytes);

                ItemImagesBean itemImage = itemImagesRepository.getItemImagesBeanByName(fileName);

                if (itemImage != null) {
                    Path previousFilePath = Paths.get(profilePath + "items\\", itemImage.getName());
                    Files.deleteIfExists(previousFilePath);

                    itemImagesRepository.deleteById(itemImage.getId());
                }

                ItemImagesBean newItemImage = ItemImagesBean.builder()
                        .name(fileName)
                        .imageUrl("http://localhost/img/koratuwa/items/" + fileName)
                        .itemBean(itemBean).build();

                itemImagesRepository.save(newItemImage);
            }
        }
    }

    private void deleteImage(ItemImagesBean itemImagesBean) throws IOException {

        if (itemImagesBean != null) {
            Path previousFilePath = Paths.get(profilePath + "items\\", itemImagesBean.getName());
            Files.delete(previousFilePath);
            itemImagesRepository.deleteById(itemImagesBean.getId());
        }
    }

    public ResponseEntity removeItem(Integer id, HttpServletRequest request) throws IOException {
        try {
            Claims claims = jwtUserDetailsService.authenticate(request, JwtTypes.user);

            Integer claimedUserId = claims.get(ApplicationConstant.JWT_USER_ID, Integer.class);

            UserBean farmer = userRepository.getFarmerData(claimedUserId);

            if (farmer == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You are not a farmer"));
            }

            ItemBean itemBean = itemRepository.getItemBeanById(id);

            if (itemBean == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("Item not found"));
            }

            if (!itemBean.getFarmer().getId().equals(claimedUserId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HttpResponse<>()
                        .responseFail("You have no claims to do this task"));
            }

            List<ItemImagesBean> imagesList = itemImagesRepository.getImagesByItemId(id);

            for (ItemImagesBean image: imagesList) {
                deleteImage(image);

                itemImagesRepository.deleteById(image.getId());
            }

            itemRepository.deleteById(id);

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk("Item removed"));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }

    public ResponseEntity getAllItems(GetItemsRequest getItemsRequest, HttpServletRequest request) {
        try {
            jwtUserDetailsService.authenticate(request, JwtTypes.visitor, JwtTypes.admin, JwtTypes.user);

            List<ItemBean> list;

            if (getItemsRequest.getCategoryId() != null) {
                if (!getItemsRequest.getFarmerName().equals("") && getItemsRequest.getFarmerName() != null) {
                    list = itemRepository.getAllItemsFilterFarmerAndCategory(getItemsRequest.isBulk(),
                            getItemsRequest.getFarmerName(), getItemsRequest.getCategoryId());
                } else {
                    list = itemRepository.getAllItemsFilterCategory(getItemsRequest.isBulk(),
                            getItemsRequest.getCategoryId());
                }
            } else {
                if (!getItemsRequest.getFarmerName().equals("") && getItemsRequest.getFarmerName() != null) {
                    list = itemRepository.getAllItemsFilterFarmer(getItemsRequest.isBulk(),
                            getItemsRequest.getFarmerName());
                } else {
                    list = itemRepository.getAllItemsFilterNormal(getItemsRequest.isBulk());
                }
            }

            List<GetItemsResponse> responseList = new ArrayList<>();

            for (ItemBean item: list) {
                List<ItemImagesBean> imagesList = itemImagesRepository.getImagesByItemId(item.getId());

                GetItemsResponse getItemsResponse = GetItemsResponse.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .categoryId(item.getCategory().getId())
                        .categoryName(item.getCategory().getName())
                        .description(item.getDescription())
                        .unitPrice(item.getUnitPrice())
                        .totalUnits(item.getTotalUnits())
                        .bulk(item.isBulk())
                        .minimumPurchasableUnits(item.getMinimumPurchasableUnits())
                        .stockAvailable(item.isStockAvailable())
                        .orderingDate(item.getOrderingDate() != null ? item.getOrderingDate().toString() : null)
                        .farmer(item.getFarmer())
                        .disabled(item.isDisabled())
                        .deleted(item.isDeleted())
                        .imagesList(imagesList).build();

                responseList.add(getItemsResponse);
            }

            return ResponseEntity.ok()
                    .body(new HttpResponse<>().responseOk(responseList));

        } catch (KoratuwaAppExceptions e) {
            return ResponseEntity.internalServerError()
                    .body(new HttpResponse<>().responseFail(e.getMessage()));
        }
    }
}
