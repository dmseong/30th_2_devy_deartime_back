// LetterFavoriteId.java (복합 키)
package com.project.deartime.app.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LetterFavoriteId implements Serializable {
    private Long user;
    private Long letter;
}
