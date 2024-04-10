package com.example.shoppingmall02.board.service;

import com.example.shoppingmall02.board.domain.CreateBoardDTO;
import com.example.shoppingmall02.board.domain.ResponseBoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface BoardService {
    ResponseEntity<?> createBoard(CreateBoardDTO board, String email);
    ResponseEntity<?> updateBoard(Long boardId, CreateBoardDTO board, String email);
    String removeBoard(Long boardId, UserDetails userDetails);
    ResponseEntity<?> getBoard(Long boardId, String email);
    // 모든 게시글 가져오기
    Page<ResponseBoardDTO> getBoards(String email, Pageable pageable, String searchKeyword);
    // 작성자의 모든 게시글 가져오기
    Page<ResponseBoardDTO> getMyBoards(String email, Pageable pageable, String searchKeyword);
}
