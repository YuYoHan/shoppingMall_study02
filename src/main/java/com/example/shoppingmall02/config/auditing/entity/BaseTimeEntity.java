package com.example.shoppingmall02.config.auditing.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {
    // 엔티티 등록한 시간
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regTime;

    // 엔티티 수정한 시간
    @LastModifiedDate
    private LocalDateTime updateTime;
}
