package com.example.shoppingmall02.board.repository;

import com.example.shoppingmall02.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Long>, BoardRepositoryCustom {
    // 게시글 조회
    @Query("select b from board b" +
            " join fetch b.member " +
            " where b.boardId = :boardId")
    Optional<BoardEntity> findByBoardId(@Param("boardId") Long boardId);

    // 해당 유저의 모든 게시글 삭제
    void deleteAllByMemberMemberId(Long memberId);
}
