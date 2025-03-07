package com.anlb.readcycle.dto.response;

import java.io.Serializable;
import java.time.Instant;

public class MaintenanceDto implements Serializable {

    private boolean isInMaintenance = false;
    private Instant from;

    public MaintenanceDto(boolean isInMaintenance, Instant from) {
        this.isInMaintenance = isInMaintenance;
        this.from = from;
    }

    public MaintenanceDto() {}

    public boolean isInMaintenance() {
        return isInMaintenance;
    }

    public void setInMaintenance(boolean inMaintenance) {
        isInMaintenance = inMaintenance;
    }

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    
}
