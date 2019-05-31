package com.datastax.demo.simple;

import com.datastax.demo.simple.User;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;

@Dao
public interface UserDao {

    @Select
    User getUser(int id);

    @Insert
    void addUser(User user);
}
