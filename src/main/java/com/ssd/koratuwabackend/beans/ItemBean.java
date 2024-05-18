package com.ssd.koratuwabackend.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class ItemBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String title;
    @OneToOne
    @JoinColumn(name = "categoryId")
    private CategoryBean category;
    @Column(length = 1000)
    private String description;
    private Integer unitPrice;
    private Integer totalUnits;
    private boolean bulk;
    //only if bulk false
    private Integer minimumPurchasableUnits;
    private boolean stockAvailable;
    //only if stockAvailable false
    @Temporal(TemporalType.DATE)
    @Column(name = "orderingDate")
    private Date orderingDate;
    @OneToOne
    @JoinColumn(name = "farmerId")
    private UserBean farmer;
    private boolean disabled;
    private boolean deleted;
}
