package com.datastax.demo.adv;

import com.datastax.demo.simple.User;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;

@Dao
public interface UserDaoWithIndexes {

    @Select
    User getUser(int id);

    @Insert
    void addUser(User user);

    @Select(customWhereClause = "name = :name")
    User getUserByNameWithSI(String name);

//    @Select(customWhereClause = "email = :email")
//    User getUserByEmailWithMV(String email);
}
