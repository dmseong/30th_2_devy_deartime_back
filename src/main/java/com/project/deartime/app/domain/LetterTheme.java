package com.project.deartime.app.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "letter_themes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LetterTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "code", nullable = false, length = 20, unique = true)
    private String code;

    // 1:N 관계: Theme 1개에 Letter N개
    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    private List<Letter> letters = new ArrayList<>();
}
