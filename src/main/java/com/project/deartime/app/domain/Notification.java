package com.project.deartime.app.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Notification")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // N:1 관계: user_id (알림을 받는 사용자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false, length = 30)
    private String type; // 알림 유형 (예: 'COMMENT', 'LIKE', 'FRIEND_REQUEST')

    @Column(name = "message", nullable = false, length = 255)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "related_resource_id")
    private Long relatedResourceID; // 관련된 리소스 ID (예: Letter ID)

    @Column(name = "related_resource_owner")
    private Long relatedResourceOwner; // 관련된 리소스의 소유자 ID (Notification 테이블의 FK는 아님)
}
