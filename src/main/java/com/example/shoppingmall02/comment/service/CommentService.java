package com.example.shoppingmall02.comment.service;

import com.example.shoppingmall02.comment.domain.RequestCommentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CommentService {
    // 댓글 작성
    ResponseEntity<?> createComment(Long boardId,
                                    RequestCommentDTO comment,
                                    String email);

    // 댓글 삭제
    String removeComment(Long commentId,
                         UserDetails userDetails);

    // 댓글 수정
    ResponseEntity<?> updateComment(Long boardId,
                                    Long commentId,
                                    RequestCommentDTO comment,
                                    String email);
}
