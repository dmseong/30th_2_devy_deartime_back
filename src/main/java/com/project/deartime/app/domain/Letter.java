package com.project.deartime.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "letters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Letter extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 관계: sender_id (보낸 사람)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // N:1 관계: receiver_id (받는 사람)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // N:1 관계: theme_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private LetterTheme theme;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    protected Boolean isRead = false;

    @Column(name = "is_deleted_by_sender", nullable = false)
    @Builder.Default
    private Boolean isDeletedBySender = false;

    @Column(name = "is_deleted_by_receiver", nullable = false)
    @Builder.Default
    private Boolean isDeletedByReceiver = false;

    // N:M 관계: LetterFavorite (좋아요를 누른 사용자 목록)
    @OneToMany(mappedBy = "letter", cascade = CascadeType.ALL)
    private Set<LetterFavorite> favorites = new HashSet<>();

    public void softDeleteBySender() {
        this.isDeletedBySender = true;
    }

    public void softDeleteByReceiver() {
        this.isDeletedByReceiver = true;
    }

    public boolean isPermanentlyDeletable() {
        return this.isDeletedByReceiver && this.isDeletedBySender;
    }

    public void markAsRead() {
        if (!this.getIsRead()) {
            this.isRead = true;
        }
    }

    public Letter(User sender, User receiver, LetterTheme theme, String title, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.theme = theme;
        this.title = title;
        this.content = content;
    }
}
