package com.ssd.koratuwabackend.beans.requests;

import com.ssd.koratuwabackend.common.utils.SSDStringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sorting {
    private String name;
    private String direction;

    @Override
    public String toString() {
        return "Sort{" +
                "name='" + name + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }

    public static Sort.Order getSort(Sorting sorting) {
        if (sorting != null && !SSDStringUtils.isNullOrEmpty(sorting.getName()))
            if (!SSDStringUtils.isNullOrEmpty(sorting.getDirection())
                    && sorting.getDirection().toLowerCase().contains("desc")) {
                return new Sort.Order(Sort.Direction.DESC, sorting.getName().trim());
            } else
                return new Sort.Order(Sort.Direction.ASC, sorting.getName().trim());

        return new Sort.Order(Sort.Direction.DESC, "id");
    }
}
