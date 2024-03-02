package com.example.shoppingmall02.member.domain;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private String memberAddr;
    private String memberAddrDetail;
    private String memberZipCode;
}
