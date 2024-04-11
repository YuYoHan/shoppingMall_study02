package com.example.shoppingmall02.comment.entity;

import com.example.shoppingmall02.board.entity.BoardEntity;
import com.example.shoppingmall02.comment.domain.CommentSecret;
import com.example.shoppingmall02.comment.domain.RequestCommentDTO;
import com.example.shoppingmall02.comment.domain.ResponseCommentDTO;
import com.example.shoppingmall02.config.auditing.entity.BaseEntity;
import com.example.shoppingmall02.member.entity.MemberEntity;
import lombok.*;

import javax.persistence.*;

@Entity(name = "comment")
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommentEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;
    private String comment;

    @Enumerated(EnumType.STRING)
    private CommentSecret commentSecret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    // DTO를 엔티티로 변환
    public static CommentEntity changeEntity(ResponseCommentDTO comment,
                                             MemberEntity member,
                                             BoardEntity board) {
        return CommentEntity.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .commentSecret(comment.getCommentSecret())
                .member(member)
                .board(board)
                .build();
    }

    // 생성
    public static CommentEntity createComment(RequestCommentDTO comment,
                                              MemberEntity member,
                                              BoardEntity board) {
        return CommentEntity.builder()
                .comment(comment.getComment())
                .commentSecret(comment.getCommentSecret())
                .member(member)
                .board(board)
                .build();
    }

    // 수정
    public void updateComment(RequestCommentDTO comment) {
        // 수정할 내용이 들어오면 수정하고 아니면 기존의 코멘트를 사용합니다.
        this.comment = comment.getComment() != null ? comment.getComment() : this.comment;
    }
}
