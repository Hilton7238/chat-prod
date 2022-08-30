package com.h.msgservice.dao;

import org.apache.ibatis.annotations.Param;

public interface UserDao {
    public Object getFriend(@Param("uid") String uid, @Param("friendUid") String friendUid);
}
