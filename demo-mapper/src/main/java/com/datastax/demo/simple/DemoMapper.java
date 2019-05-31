package com.datastax.demo.simple;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface DemoMapper
{
    @DaoFactory
    UserDao userDao();
}
