package com.example.shoppingmall02.comment.repository;

import com.example.shoppingmall02.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
