package com.ssd.koratuwabackend.repositories;

import com.ssd.koratuwabackend.beans.UserLoginBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLoginBean, Integer> {

    @Query("SELECT l FROM UserLoginBean l WHERE l.user.id=:uid AND l.logoutTime is null")
    UserLoginBean getAlreadyLoginBean(@Param("uid") Integer uid);
}
