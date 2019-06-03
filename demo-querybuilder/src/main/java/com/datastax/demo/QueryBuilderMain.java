package com.datastax.demo;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.select.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.datastax.demo.ProductRange.productsCreatedBetween;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public class QueryBuilderMain {

    private QueryBuilderMain() {
    }

    public static void main(String[] args) {
        QueryBuilderMain demo = new QueryBuilderMain();
        demo.start();
    }

    public void init(CqlSession session) {
        session.execute("CREATE KEYSPACE IF NOT EXISTS meetup_demo WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
        session.execute("DROP TABLE IF EXISTS meetup_demo.product");
        session.execute("CREATE TABLE meetup_demo.product (id int, produced date, name text, description text, primary key (id, produced))");
    }

    public void start() {

        try (CqlSession session = CqlSession.builder().withKeyspace("meetup_demo").build()) {
            init(session);



            addData(session);



            List<Row> rows = session.execute(queryExample1()).all();
            System.out.println("only 1 product = " + rows.stream().map(r -> r.getString("name")).collect(Collectors.toList()));



            rows = session.execute(queryExample2()).all();
            System.out.println("only 1 product and columns = " + rows.stream().map(r -> r.getColumnDefinitions().size()).collect(Collectors.toList()));




            rows = session.execute(queryExample3()).all();
            System.out.println("range products = " + rows.stream().map(r -> r.getString("name")).collect(Collectors.toList()));
        }
    }






    private void addData(CqlSession session) {
        session.execute(
                insertProduct(1, LocalDate.of(1993, 2, 21), "woody", "sheriff cowboy").build());

        session.execute(
                insertProduct(1, LocalDate.of(1993, 2, 22), "jessie", "fiercest cowgirl in the west").build());

        session.execute(
                insertProduct(1, LocalDate.of(1993, 2, 23), "bullseye", "rides like the wind").build());

        session.execute(
                insertProduct(2, LocalDate.of(1993, 2, 21), "buzz lightyear", "space ranger")
                .ifNotExists().build());

        session.execute(
                insertProduct(3, LocalDate.of(1993, 2, 21), "slinky", "springy dog")
                .usingTimestamp(122332L).build());
    }

    public static Insert insertProduct(int id, LocalDate date, String name, String description) {
        return insertInto("product")
                .value("id", literal(id))
                .value("produced", literal(date))
                .value("name", literal(name))
                .value("description", literal(description));
    }











    public Statement queryExample1() {
        return getOnlyProduct(1, LocalDate.of(1993, 2, 21))
                .build();
    }

    public Statement queryExample2() {
        return getOnlyProductColumns(2, LocalDate.of(1993, 2, 21), "name", "description")
                .build();
    }

    public Statement queryExample3() {
        return getAllProducts().where(productsCreatedBetween(1, LocalDate.of(1993, 2, 21), LocalDate.of(1993, 2, 22)))
                .build();
    }



    public static Select getAllProducts() {
        return selectFrom("product")
                .all();
    }

    public static Select getOnlyProduct(int id, LocalDate date) {
        return getAllProducts()
                .whereColumn("id")
                .isEqualTo(literal(id))
                .whereColumn("produced")
                .isEqualTo(literal(date));
    }

    public static Select getOnlyProductColumns(int id, LocalDate date, String... columns) {
        return getOnlyProduct(id, date)
                .columns(columns);
    }
}
