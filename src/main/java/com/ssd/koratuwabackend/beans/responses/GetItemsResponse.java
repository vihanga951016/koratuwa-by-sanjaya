package com.ssd.koratuwabackend.beans.responses;

import com.ssd.koratuwabackend.beans.ItemImagesBean;
import com.ssd.koratuwabackend.beans.UserBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetItemsResponse {

    private Integer id;
    private String title;
    private Integer categoryId;
    private String categoryName;
    private String description;
    private Integer unitPrice;
    private Integer totalUnits;
    private boolean bulk;
    private Integer minimumPurchasableUnits;
    private boolean stockAvailable;
    private String orderingDate;
    private UserBean farmer;
    private boolean disabled;
    private boolean deleted;
    private List<ItemImagesBean> imagesList;

}
