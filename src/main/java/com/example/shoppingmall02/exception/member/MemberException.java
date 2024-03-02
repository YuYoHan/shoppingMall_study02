package com.example.shoppingmall02.exception.member;

// 유저 관련 에러 메시지
public class MemberException extends RuntimeException{
    public MemberException(String msg) {
        super(msg);
    }
}
