package com.h.userservice.dao;

import com.h.common.bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    String getEmailByUid(@Param("uid") String uid);

    boolean updatePasswordByUid(@Param("uid") String uid, @Param("password") String password);

    List<User> getFriends(@Param("uid") String uid);

    boolean deleteFriend(@Param("uid") String uid, @Param("friendUid") String friendUid);

    User getUser(@Param("uid") String uid);

    List<User> getAllUsers();

    List<User> getUsers(@Param("uid") String uid);

    int addFriend(@Param("uid") String uid, @Param("friendUid") String friendUid);

    boolean updateInf(@Param("uid") String uid, @Param("imgUrl") String imgUrl, @Param("mail") String mail, @Param("name") String name);

    boolean deleteUser(@Param("uid") String uid);

    int countColumns(@Param("tableName") String tableName);

    int updateMailByUid(@Param("uid") String uid, @Param("mail") String mail);
    
}
