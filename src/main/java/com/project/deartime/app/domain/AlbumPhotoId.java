// AlbumPhotoId.java (복합 키)
package com.project.deartime.app.domain;

import lombok.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AlbumPhotoId implements Serializable {
    private Long photo; // AlbumPhoto 엔티티의 필드명(photo)의 ID 타입
    private Long album; // AlbumPhoto 엔티티의 필드명(album)의 ID 타입
}