package com.dajia.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.PreUpdate;

public class BaseModel {

    @Column(name="created_date")
    public Date createdDate;
    
    @Column(name="modified_date")
    public Date modifiedDate;

    @Column(name="is_active")
    public String isActive;

    @PreUpdate
    public void modifiedDate() {
        this.modifiedDate = new Date();
    }
}