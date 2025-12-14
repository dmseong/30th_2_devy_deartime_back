package com.project.deartime.app.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "letter_favorite")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(LetterFavoriteId.class)
@Builder
@AllArgsConstructor
public class LetterFavorite extends BaseTimeEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id", nullable = false)
    private Letter letter;

}
