package com.example.shoppingmall02.board.repository;

import com.example.shoppingmall02.board.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    Page<BoardEntity> findAll(Pageable pageable, String search);
    Page<BoardEntity> findByEmail(String email, Pageable pageable, String search);
    Page<BoardEntity> findByNickName(String nickName, Pageable pageable, String search);
}
