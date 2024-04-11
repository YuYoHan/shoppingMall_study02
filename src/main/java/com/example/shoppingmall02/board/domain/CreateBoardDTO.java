package com.example.shoppingmall02.board.domain;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class CreateBoardDTO {
    @NotNull(message = "게시글 제목은 필수 입력입니다.")
    private String title;
    private String content;
}
