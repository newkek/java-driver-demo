package com.datastax.demo.simple;

import com.datastax.oss.driver.api.core.CqlSession;

public class MapperMain
{

    public static void main(String[] args)
    {
        MapperMain mapperMain = new MapperMain();
        mapperMain.start();
    }

    private MapperMain() {}

    public void start() {

        // 1. check out User.java
        // 2. check out UserDao.java
        // 3. check out DemoMapper.java
        // 4. this

        // Created schema:
        // CREATE KEYSPACE meetup_demo WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
        // CREATE TABLE meetup_demo.user (id int primary key, name text, email text);

        try (CqlSession session = CqlSession.builder().withKeyspace("meetup_demo").build()) {

            DemoMapper demoMapper = new DemoMapperBuilder(session).build();

            UserDao dao = demoMapper.userDao();

            dao.addUser(new User(1, "marko", "the-email@email.email"));

            System.out.println("getUser() = " + dao.getUser(1));
        }
    }
}
