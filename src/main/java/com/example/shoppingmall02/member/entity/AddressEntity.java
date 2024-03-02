package com.example.shoppingmall02.member.entity;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity {
    private String memberAddr;
    private String memberAddrDetail;
    private String memberZipCode;
}
