package com.example.shoppingmall02.comment.controller;

import com.example.shoppingmall02.comment.domain.RequestCommentDTO;
import com.example.shoppingmall02.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/{boardId}/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createComment(@PathVariable Long boardId,
                                           @RequestBody RequestCommentDTO comment,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ResponseEntity<?> responseComment = commentService.createComment(boardId, comment, email);
        return ResponseEntity.ok().body(responseComment);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> removeComment(@PathVariable Long commentId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        String responseComment = commentService.removeComment(commentId, userDetails);
        return ResponseEntity.ok().body(responseComment);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateComment(@PathVariable Long boardId,
                                           @PathVariable Long commentId,
                                           @RequestBody RequestCommentDTO comment,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return commentService.updateComment(boardId, commentId, comment, email);
    }
}
