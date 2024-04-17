package com.example.shoppingmall02.item.entity;

import com.example.shoppingmall02.comment.entity.CommentEntity;
import com.example.shoppingmall02.config.auditing.entity.BaseEntity;
import com.example.shoppingmall02.item.domain.ItemStatus;
import com.example.shoppingmall02.member.entity.MemberEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ItemEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;
    @Column(nullable = false)
    private String itemName;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private String itemDetails;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("commentId desc ")
    @Builder.Default
    private List<CommentEntity> comments = new ArrayList<>();


}
