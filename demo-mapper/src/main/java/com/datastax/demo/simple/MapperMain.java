package com.datastax.demo.simple;

import com.datastax.oss.driver.api.core.CqlSession;

public class MapperMain {

    public static void main(String[] args) {
        MapperMain mapperMain = new MapperMain();
        mapperMain.start();
    }

    private MapperMain() {}

    public void init() {
        try (CqlSession session = CqlSession.builder().build()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS meetup_demo WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
            session.execute("DROP TABLE IF EXISTS meetup_demo.user");
            session.execute("CREATE TABLE meetup_demo.user (id int primary key, name text, email text)");
        }
    }

    public void start() {

        // 1. check out User.java
        // 2. check out UserDao.java
        // 3. check out DemoMapper.java
        // 4. this

        init();
        try (CqlSession session = CqlSession.builder().withKeyspace("meetup_demo").build()) {

            DemoMapper demoMapper = new DemoMapperBuilder(session).build();

            UserDao dao = demoMapper.userDao();

            dao.addUser(new User(1, "andy", "the-email@email.email"));
            dao.addUser(new User(2, "bonnie", "the-email2@email.email"));

            System.out.println("user = " + dao.getUser(1));
        }
    }
}
