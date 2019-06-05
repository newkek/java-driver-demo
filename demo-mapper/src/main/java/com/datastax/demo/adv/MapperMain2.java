package com.datastax.demo.adv;

import com.datastax.demo.simple.User;
import com.datastax.oss.driver.api.core.CqlSession;

public class MapperMain2 {

    public static void main(String[] args) {
        MapperMain2 mapperMain = new MapperMain2();
        mapperMain.start();
    }

    private MapperMain2() {
    }

    public void start() {

        // CREATE INDEX byname ON meetup_demo.user(name);
        // CREATE MATERIALIZED VIEW meetup_demo.user_by_email
        //   AS select email, id, name FROM meetup_demo.user
        //   WHERE email is not null AND id is not null
        //   PRIMARY KEY (email, id);

        try (CqlSession session = CqlSession.builder().withKeyspace("meetup_demo").build()) {

            DemoMapper2 demoMapper = new DemoMapper2Builder(session).build();

            UserDaoWithIndexes daoOnTable = demoMapper.userDaoWithIndexes("user");

            daoOnTable.addUser(new User(1, "marko", "the-email2@email.email"));

            System.out.println("dao = " + daoOnTable.getUser(1));
            System.out.println("dao = " + daoOnTable.getUserByNameWithSI("marko"));

            UserDaoWithIndexes daoOnMV = demoMapper.userDaoWithIndexes("user_by_email");
            System.out.println("dao = " + daoOnMV.getUserByEmailWithMV("the-email@email.email"));
        }
    }
}
