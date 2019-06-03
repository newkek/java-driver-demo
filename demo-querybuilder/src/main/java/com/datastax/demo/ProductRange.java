package com.datastax.demo;

import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.time.LocalDate;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static com.datastax.oss.driver.api.querybuilder.relation.Relation.column;

public class ProductRange implements Relation {

    private final int id;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private ProductRange(int id, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static ProductRange productsCreatedBetween(int id, LocalDate startDate, LocalDate endDate) {
        return new ProductRange(id, startDate, endDate);
    }

    @Override
    public boolean isIdempotent() {
        return false;
    }

    @Override
    public void appendTo(@NonNull StringBuilder builder) {
        column("id").isEqualTo(literal(id)).appendTo(builder);

        builder.append(" AND ");

        column("produced").isGreaterThanOrEqualTo(literal(startDate)).appendTo(builder);

        builder.append(" AND ");

        column("produced").isLessThanOrEqualTo(literal(endDate)).appendTo(builder);
    }
}
