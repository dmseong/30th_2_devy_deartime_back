package com.project.deartime.app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Friend")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(FriendId.class)
public class Friend extends BaseTimeEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend; // 친구 관계 대상

    @Column(name = "status", nullable = false, length = 10)
    private String status; // 관계 상태 (예: 'pending', 'accepted', 'blocked')

    @Column(name = "requested_at")
    private LocalDateTime requestedAt; // 친구 요청 일시
}
