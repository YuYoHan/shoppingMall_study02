package com.example.shoppingmall02.board.service;

import com.example.shoppingmall02.board.domain.BoardSecret;
import com.example.shoppingmall02.board.domain.CreateBoardDTO;
import com.example.shoppingmall02.board.domain.ResponseBoardDTO;
import com.example.shoppingmall02.board.entity.BoardEntity;
import com.example.shoppingmall02.board.repository.BoardRepository;
import com.example.shoppingmall02.board.repository.BoardRepositoryCustom;
import com.example.shoppingmall02.exception.board.BoardException;
import com.example.shoppingmall02.exception.member.MemberException;
import com.example.shoppingmall02.member.entity.MemberEntity;
import com.example.shoppingmall02.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Transactional
@Log4j2
public class BoardServiceImpl implements BoardService{
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    // 게시글 생성
    @Override
    public ResponseEntity<?> createBoard(CreateBoardDTO board, String email) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(email);

            if(findMember != null) {
                BoardEntity boardEntity = BoardEntity.createBoard(board, findMember);
                BoardEntity saveBoard = boardRepository.save(boardEntity);
                ResponseBoardDTO responseBoardDTO = ResponseBoardDTO.changeDTO(saveBoard);
                return ResponseEntity.ok().body(responseBoardDTO);
            } else {
               throw new EntityNotFoundException("회원이 존재하지 않습니다.");
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 게시글 수정
    @Override
    public ResponseEntity<?> updateBoard(Long boardId, CreateBoardDTO board, String email) {
        try {
            BoardEntity findBoard = boardRepository.findByBoardId(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
            log.info("게시글 : " + findBoard);
            MemberEntity findMember = memberRepository.findByMemberEmail(email);

            if(findBoard.getMember().getMemberId().equals(findMember.getMemberId())) {
                findBoard.updateBoard(board);
                BoardEntity updateBoard = boardRepository.save(findBoard);
                ResponseBoardDTO responseBoardDTO = ResponseBoardDTO.changeDTO(updateBoard);
                log.info("수정한 게시글 : " + responseBoardDTO);
                return ResponseEntity.ok().body(responseBoardDTO);
            }
            throw new MemberException("회원정보가 일치 하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 게시글 삭제
    @Override
    public String removeBoard(Long boardId, UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("이메일 : " + email);
            // 권한 가져오기
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            String authority = authorities.iterator().next().getAuthority();
            // 게시글 조회
            BoardEntity findBoard = boardRepository.findByBoardId(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
            // 유저 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);

            if(findBoard.getMember().getMemberId().equals(findMember.getMemberId())) {
                // 게시글 삭제
                boardRepository.findByBoardId(findBoard.getBoardId());
                return "게시글을 삭제했습니다.";
            }

            // 관리자 등급이 맞다면 삭제 가능
            if(authority.equals("ADMIN") || authority.equals("ROLE_ADIN")) {
                boardRepository.findByBoardId(findBoard.getBoardId());
                return "게시글을 관리자 권한으로 삭제하였습니다.";
            }

            return "삭제할 수 없습니다.";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // 게시글 조회
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> getBoard(Long boardId, String email) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            BoardEntity findBoard = boardRepository.findByBoardId(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

            if(findMember.getMemberId().equals(findBoard.getMember().getMemberId())) {
                ResponseBoardDTO responseBoardDTO = ResponseBoardDTO.changeDTO(findBoard);
                return ResponseEntity.ok().body(responseBoardDTO);
            } else {
                throw new BoardException("해당 유저의 문의글이 아닙니다.");
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 모든 게시글 가져오기
    @Override
    @Transactional(readOnly = true)
    public Page<ResponseBoardDTO> getBoards(String email, Pageable pageable, String searchKeyword) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            Page<BoardEntity> findBoards = boardRepository.findAll(pageable, searchKeyword);
            Page<ResponseBoardDTO> findBoardDTOs = findBoards.map(ResponseBoardDTO::changeDTO);
            // 댓글이 존재 여부에 따라 상태를 바꿔준다.
            // X면 없다는 것이고 O면 댓글이 있다는 것이다.
            findBoardDTOs.forEach(ResponseBoardDTO::changeReplyStatus);

            findBoardDTOs.forEach(board -> {
                        if(board.getNickName().equals(findMember.getMemberNickName())) {
                            board.changeBoardSecret(BoardSecret.LOCK);
                        } else {
                            board.changeBoardSecret(BoardSecret.UN_LOCK);
                        }
                    }
            );
            return findBoardDTOs;
        } catch (Exception e) {
            throw new BoardException("게시글을 조회하는데 실패했습니다.");
        }
    }

    // 나의 게시글 가져오기
    @Override
    @Transactional(readOnly = true)
    public Page<ResponseBoardDTO> getMyBoards(String email, Pageable pageable, String searchKeyword) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            Page<BoardEntity> findBoards = boardRepository.findByEmail(email, pageable, searchKeyword);
            Page<ResponseBoardDTO> findBoardDTOs = findBoards.map(ResponseBoardDTO::changeDTO);
            // 댓글이 존재 여부에 따라 상태를 바꿔준다.
            // X면 없다는 것이고 O면 댓글이 있다는 것이다.
            findBoardDTOs.forEach(ResponseBoardDTO::changeReplyStatus);
            // 나의 게시글이니 볼 수 있게 해준다.
            findBoardDTOs.forEach(board -> board.changeBoardSecret(BoardSecret.UN_LOCK));
            return findBoardDTOs;
        } catch (Exception e) {
            throw new BoardException("게시글을 조회하는데 실패했습니다.");
        }
    }
}
