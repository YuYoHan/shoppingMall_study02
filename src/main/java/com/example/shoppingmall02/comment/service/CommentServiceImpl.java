package com.example.shoppingmall02.comment.service;

import com.example.shoppingmall02.board.entity.BoardEntity;
import com.example.shoppingmall02.board.repository.BoardRepository;
import com.example.shoppingmall02.comment.domain.RequestCommentDTO;
import com.example.shoppingmall02.comment.domain.ResponseCommentDTO;
import com.example.shoppingmall02.comment.entity.CommentEntity;
import com.example.shoppingmall02.comment.repository.CommentRepository;
import com.example.shoppingmall02.exception.comment.CommentException;
import com.example.shoppingmall02.exception.member.MemberException;
import com.example.shoppingmall02.member.entity.MemberEntity;
import com.example.shoppingmall02.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    // 댓글 생성
    @Override
    public ResponseEntity<?> createComment(Long boardId, RequestCommentDTO comment, String email) {
        try {
            BoardEntity findBoard = boardRepository.findByBoardId(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
            MemberEntity findMember = memberRepository.findByMemberEmail(email);

            if(findMember != null) {
                CommentEntity commentEntity = CommentEntity.createComment(comment, findMember, findBoard);
                CommentEntity responseComment = commentRepository.save(commentEntity);
                return ResponseEntity.ok().body(responseComment);
            } else {
                throw new EntityNotFoundException("유저가 존재하지 않습니다.");
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 생성 실패");
        }
    }

    // 댓글 삭제
    @Override
    public String removeComment(Long commentId, UserDetails userDetails) {
        try {
            // 권한 가져오기
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            String authority = authorities.iterator().next().getAuthority();
            log.info("권한 : " + authority);

            // 댓글 조회
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

            // 유저 정보 조회
            String email = userDetails.getUsername();
            MemberEntity findMember = memberRepository.findByMemberEmail(email);

            if(findMember.getMemberId().equals(findComment.getMember().getMemberId())) {
                commentRepository.deleteById(findComment.getCommentId());
                return "댓글을 삭제했습니다.";
            }
            if(authority.equals("ROLE_USER") || authority.equals("ROLE_ADMIN")) {
                commentRepository.deleteById(findComment.getCommentId());
                return "댓글을 관리자 권한으로 삭제하였습니다.";
            }

            throw new CommentException("댓글을 삭제하는데 실패했습니다.");
        } catch (EntityNotFoundException | CommentException e) {
            return e.getMessage();
        }
    }

    // 댓글 수정
    @Override
    public ResponseEntity<?> updateComment(Long boardId, Long commentId, RequestCommentDTO comment, String email) {
        try {
            // 게시물 조회
            BoardEntity findBoard = boardRepository.findById(boardId)
                    .orElseThrow(EntityNotFoundException::new);
            // 댓글 조회
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(EntityNotFoundException::new);
            // 회원 조회
            MemberEntity findUser = memberRepository.findByMemberEmail(email);

            boolean equalsEmail = findUser.getMemberEmail().equals(findComment.getMember().getMemberEmail());
            boolean equalsId = findComment.getBoard().getBoardId().equals(findBoard.getBoardId());

            // 해당 유저인지 체크하고 댓글을 작성한 게시글 id인지 체크
            if(equalsEmail && equalsId) {
                // 댓글 수정
                findComment.updateComment(comment);
                CommentEntity updateComment = commentRepository.save(findComment);
                log.info("댓글 : " + updateComment);
                ResponseCommentDTO returnComment = ResponseCommentDTO.changeDTO(updateComment);
                return ResponseEntity.ok().body(returnComment);
            } else {
                return ResponseEntity.badRequest().body("일치하지 않습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 수정하는데 실패했습니다.");
        }
    }
}
