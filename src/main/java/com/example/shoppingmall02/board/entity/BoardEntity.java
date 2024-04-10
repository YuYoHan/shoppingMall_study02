package com.example.shoppingmall02.board.entity;

import com.example.shoppingmall02.board.domain.BoardSecret;
import com.example.shoppingmall02.board.domain.CreateBoardDTO;
import com.example.shoppingmall02.board.domain.ReplyStatus;
import com.example.shoppingmall02.board.domain.ResponseBoardDTO;
import com.example.shoppingmall02.comment.domain.ResponseCommentDTO;
import com.example.shoppingmall02.comment.entity.CommentEntity;
import com.example.shoppingmall02.config.auditing.entity.BaseEntity;
import com.example.shoppingmall02.member.entity.MemberEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "board")
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"member", "comment"})
public class BoardEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;
    @Column(length = 300, nullable = false)
    private String title;
    @Column(length = 3000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("commentId desc ")
    @Builder.Default
    private List<CommentEntity> comments = new ArrayList<>();

    // DTO를 엔티티로 변환
    public static BoardEntity changeEntity(ResponseBoardDTO board,
                                           MemberEntity member) {
        BoardEntity boardEntity = BoardEntity.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .member(member)
                .build();

        // 댓글 처리
        List<ResponseCommentDTO> comments =
                board.getComments() != null ? board.getComments() : Collections.emptyList();
        for (ResponseCommentDTO comment : comments) {
            CommentEntity commentEntity = CommentEntity.changeEntity(comment, member, boardEntity);
            boardEntity.comments.add(commentEntity);
        }

        return boardEntity;
    }

    // 게시글 작성
    public static BoardEntity createBoard(CreateBoardDTO board,
                                          MemberEntity member) {
        return BoardEntity.builder()
                .title(board.getTitle())
                .content(board.getContent())
                // 본인이 작성한 글은 읽을 수 있어야하기 때문에 UN_ROCK
                .member(member)
                .build();
    }
    // 게시글 수정
    public void updateBoard(CreateBoardDTO board) {
        this.title = board.getTitle();
        this.content = board.getContent();
    }
}
