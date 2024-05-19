package com.ssd.koratuwabackend.beans.requests.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequest {

    private Integer farmerId;
    private Integer itemId;
    private boolean bulk;
    private Integer units;
    private Integer pricePerUnit;
    @Temporal(TemporalType.DATE)
    @Column(name = "orderedDate")
    private Date orderedDate;
}
