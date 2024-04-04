package com.example.shoppingmall02.item.domain;

import com.example.shoppingmall02.comment.domain.ResponseCommentDTO;
import com.example.shoppingmall02.comment.entity.CommentEntity;
import com.example.shoppingmall02.item.entity.ItemEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseItemDTO {
    private Long itemId;
    private String itemName;
    private int price;
    private String itemDetail;
    private ItemStatus itemStatus;
    private LocalDateTime regTime;
    private String nickName;
    private List<ResponseCommentDTO> comments = new ArrayList<>();


    public static ResponseItemDTO changeDTO(ItemEntity item) {
        // 댓글 처리
        List<CommentEntity> comments = item.getComments() != null ?
                item.getComments() : Collections.emptyList();
        List<ResponseCommentDTO> commentDTOS = comments.stream()
                .map(ResponseCommentDTO::changeDTO)
                .collect(Collectors.toList());

        return ResponseItemDTO.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .price(item.getPrice())
                .itemDetail(item.getItemDetails())
                .itemStatus(item.getStatus())
                .regTime(item.getRegTime())
                .nickName(item.getMember().getMemberNickName())
                .comments(commentDTOS)
                .build();


    }
}
