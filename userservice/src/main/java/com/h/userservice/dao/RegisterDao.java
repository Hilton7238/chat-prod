package com.h.userservice.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RegisterDao {
    int countUserById(@Param("uid") String uid);

    boolean addUser(@Param("uid") String uid, @Param("password") String password, @Param("imgUrl") String imgUrl, @Param("mail") String mail, String name);

    List<String> getAllMails();
}
