package com.datastax.demo.adv;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface DemoMapper2 {
    @DaoFactory
    UserDaoWithIndexes userDaoWithIndexes(@DaoTable String tableOrMv);
}
