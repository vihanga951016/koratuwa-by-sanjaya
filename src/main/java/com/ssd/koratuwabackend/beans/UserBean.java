package com.ssd.koratuwabackend.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data //lombok annotation -> creating getters and setters
@Entity// making this class as an entity
@Builder//lombok annotation -> can use builder pattern for this object
@AllArgsConstructor//lombok annotation -> creating parametarized constructor
@NoArgsConstructor//lombok annotation -> creating default constructor
@Table(name = "users")
public class UserBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String password;
    private String role;
    private String type;
    private String profilePicture;
    private boolean registrationApproved;
    private double wallet;
    private boolean deleted;
}
