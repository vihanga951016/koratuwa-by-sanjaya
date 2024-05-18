package com.ssd.koratuwabackend.beans.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateRequest {
    private int page;
    private int size;

    private Sorting sort;
    private String  searchText;

    @Override
    public String toString() {
        return "PaginationRequest{" +
                "page=" + page +
                ", size=" + size +
                ", sort=" + sort +
                ", searchText=" + searchText +
                '}';
    }
}
