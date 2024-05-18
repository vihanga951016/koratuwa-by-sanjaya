package com.ssd.koratuwabackend.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "user_logins")
public class UserLoginBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Colombo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Colombo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logoutTime;
    @Column(length = 1000)
    private String token;
    @OneToOne
    @JoinColumn(name = "userId")
    private UserBean user;
}
