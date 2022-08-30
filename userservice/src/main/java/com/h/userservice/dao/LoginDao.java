package com.h.userservice.dao;

import org.apache.ibatis.annotations.Param;

public interface LoginDao {
    String getPasswordByUid(@Param("uid") String uid);
}
