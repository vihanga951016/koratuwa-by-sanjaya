package com.ssd.koratuwabackend.beans.requests.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamersListRequest {
    private int pageIndex;
    private int size;
    private String search;

}
