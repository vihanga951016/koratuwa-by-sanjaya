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
@Table(name = "cartItem")
public class CartItemBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "farmerId")
    private UserBean farmer;
    @OneToOne
    @JoinColumn(name = "itemId")
    private ItemBean item;
    @OneToOne
    @JoinColumn(name = "cartId")
    private CartBean cart;
    private boolean bulk;
    private Integer units;
    private Integer totalPrice;
    @Temporal(TemporalType.DATE)
    @Column(name = "orderedDate")
    private Date orderedDate;
    private String paymentMethod;
}
