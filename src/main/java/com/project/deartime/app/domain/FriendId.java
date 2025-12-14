// FriendId.java (복합 키)
package com.project.deartime.app.domain;

import lombok.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FriendId implements Serializable {
    private Long user;
    private Long friend;
}
