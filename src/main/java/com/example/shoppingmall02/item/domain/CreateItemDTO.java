package com.example.shoppingmall02.item.domain;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreateItemDTO {
    @NotBlank(message = "상품명은 필수입력입니다.")
    private String itemName;
    @NotNull(message = "상품 가격은 필수입력입니다.")
    private int price;
    @NotNull(message = "상품에 대한 설명을 입력하세요")
    private String itemDetail;
    @NotNull(message = "재고수량은 최소 1개 이상이여야 합니다.")
    private int stockNum;


}
