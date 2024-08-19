package com.kelp_6.banking_apps.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    @UpdateTimestamp
    private Date updatedDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
            this.createdDate = calendar.getTime();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        this.updatedDate = calendar.getTime();
    }
}
