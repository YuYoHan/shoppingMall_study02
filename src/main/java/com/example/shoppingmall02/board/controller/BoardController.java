package com.example.shoppingmall02.board.controller;

import com.example.shoppingmall02.board.domain.CreateBoardDTO;
import com.example.shoppingmall02.board.domain.ResponseBoardDTO;
import com.example.shoppingmall02.board.service.BoardService;
import com.example.shoppingmall02.exception.validation.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {
    private final BoardService boardService;

    // 문의 등록
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createBoard(@RequestBody @Validated CreateBoardDTO board,
                                         BindingResult result,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        if(result.hasErrors()) {
            throw new DataValidationException("검증에 실패했습니다.");
        }
        String email = userDetails.getUsername();
        ResponseEntity<?> response = boardService.createBoard(board, email);
        return ResponseEntity.ok().body(response);
    }

    // 문의 삭제
    @DeleteMapping("/{boardId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public String removeBoard(@PathVariable Long boardId,
                              @AuthenticationPrincipal UserDetails userDetails) {
        return boardService.removeBoard(boardId, userDetails);
    }

    // 문의 수정
    @PutMapping("/{boardId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateBoard(@PathVariable Long boardId,
                                         @RequestBody CreateBoardDTO board,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ResponseEntity<?> responseEntity = boardService.updateBoard(boardId, board, email);
        return responseEntity;
    }

    // 문의 상세보기
    @GetMapping("/{boardId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getBoard(@PathVariable Long boardId,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ResponseEntity<?> response = boardService.getBoard(boardId, email);
        return ResponseEntity.ok().body(response);
    }

    // 모든 문의글
    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getBoards(@AuthenticationPrincipal UserDetails userDetails,
                                       Pageable pageable,
                                       String search) {
        String email = userDetails.getUsername();
        Page<ResponseBoardDTO> boards = boardService.getBoards(email, pageable, search);
        Map<String, Object> response = pageInfo(boards);
        return ResponseEntity.ok().body(response);
    }

    private static Map<String, Object> pageInfo(Page<ResponseBoardDTO> boards) {
        Map<String, Object> response = new HashMap<>();
        // 현재 페이지의 아이템 목록
        response.put("items", boards.getContent());
        // 현재 페이지 번호
        response.put("nowPageNumber", boards.getNumber()+1);
        // 전체 페이지 수
        response.put("totalPage", boards.getTotalPages());
        // 한 페이지에 출력되는 데이터 개수
        response.put("pageSize", boards.getSize());
        // 다음 페이지 존재 여부
        response.put("hasNextPage", boards.hasNext());
        // 이전 페이지 존재 여부
        response.put("hasPreviousPage", boards.hasPrevious());
        // 첫 번째 페이지 여부
        response.put("isFirstPage", boards.isFirst());
        // 마지막 페이지 여부
        response.put("isLastPage", boards.isLast());
        return response;
    }

    // 나의 문의글
    @GetMapping("/myBoards")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getMyBoards(@AuthenticationPrincipal UserDetails userDetails,
                                         Pageable pageable,
                                         String search) {
        String email = userDetails.getUsername();
        Page<ResponseBoardDTO> boards = boardService.getMyBoards(email, pageable, search);
        Map<String, Object> response = pageInfo(boards);
        return ResponseEntity.ok().body(response);
    }
}
