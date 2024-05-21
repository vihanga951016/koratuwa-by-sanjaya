package com.ssd.koratuwabackend.beans.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetItemsRequest {
    private Integer categoryId;
    private String farmerName;
    private boolean bulk;

}
