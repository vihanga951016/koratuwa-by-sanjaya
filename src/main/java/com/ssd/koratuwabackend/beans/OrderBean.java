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
@Table(name = "orders")
public class OrderBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Date orderedDate;
    private Integer pricePerUnit;
    private Integer quantity;
    private Integer totalPrice;
    private String paymentType;
    private String status;
    @OneToOne
    @JoinColumn(name = "itemId")
    private ItemBean item;
    @OneToOne
    @JoinColumn(name = "customerId")
    private UserBean customer;
    @OneToOne
    @JoinColumn(name = "farmerId")
    private UserBean farmer;
}
