package com.yonatankarp.drools.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Trades implements Serializable {
    private Long id;
    private Long id2;
    private LocalDateTime processDate;

    public Trades() {
        super();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId2() {
        return id2;
    }

    public void setId2(Long id2) {
        this.id2 = id2;
    }

    public LocalDateTime getProcessDate() {
        return processDate;
    }

    public void setProcessDate(LocalDateTime processDate) {
        this.processDate = processDate;
    }

    public Trade toTrade()  {
        Trade tr = new Trade();
        tr.setId(this.id);
        tr.setProcessDate(this.processDate);
        return tr;
    }

}
