package com.datastax.kgdo;

import com.datastax.dse.driver.api.core.DseSession;
import org.springframework.stereotype.Component;

@Component
public class DriverComponent {

    DseSession session;

    public DriverComponent(DseSession session) {
        this.session = session;
    }

    public DseSession session() {
        return this.session;
    }
}
