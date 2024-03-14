package com.example.shoppingmall02.board.domain;

import com.example.shoppingmall02.board.entity.BoardEntity;
import com.example.shoppingmall02.comment.domain.ResponseCommentDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseBoardDTO {
    private Long boardId;
    private String title;
    private String content;
    private String nickName;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;
    private BoardSecret boardSecret;
    private ReplyStatus replyStatus;
    private List<ResponseCommentDTO> comments = new ArrayList<>();

    // 엔티티를 DTO로 변환
    public static ResponseBoardDTO changeDTO(BoardEntity board) {
        return ResponseBoardDTO.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .nickName(board.getMember().getMemberNickName())
                .regTime(board.getRegTime())
                .updateTime(board.getUpdateTime())
                .build();
    }

    // 댓글 여부 확인
    public void changeReplyStatus() {
        this.replyStatus = this.comments.isEmpty() ? ReplyStatus.REPLY_X : ReplyStatus.REPLY_O;
    }
    // 게시글이 본인 글인지 확인
    public void changeBoardSecret(BoardSecret boardSecret) {
        this.boardSecret = boardSecret;
    }

}
