package com.example.shoppingmall02.comment.domain;

import com.example.shoppingmall02.comment.entity.CommentEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResponseCommentDTO {
    private Long commentId;
    private String comment;
    private String nickName;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;
    private CommentSecret commentSecret;

    public static ResponseCommentDTO changeDTO(CommentEntity comment) {
        return ResponseCommentDTO.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .regTime(comment.getRegTime())
                .updateTime(comment.getUpdateTime())
                .nickName(comment.getMember().getMemberNickName())
                .commentSecret(comment.getCommentSecret())
                .build();
    }
}
