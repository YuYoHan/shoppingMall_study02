package com.example.shoppingmall02.comment.domain;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestCommentDTO {
    private String comment;
    private CommentSecret commentSecret;
}
