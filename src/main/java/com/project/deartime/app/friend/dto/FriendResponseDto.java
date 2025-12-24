package com.project.deartime.app.friend.dto;

import com.project.deartime.app.domain.Friend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendResponseDto {

    private Long userId;
    private Long friendId;
    private String friendNickname;
    private String friendProfileImageUrl;
    private String status;
    private LocalDateTime requestedAt;

    public static FriendResponseDto from(Friend friend) {
        return FriendResponseDto.builder()
                .userId(friend.getUser().getId())
                .friendId(friend.getFriend().getId())
                .friendNickname(friend.getFriend().getNickname())
                .friendProfileImageUrl(friend.getFriend().getProfileImageUrl())
                .status(friend.getStatus())
                .requestedAt(friend.getRequestedAt())
                .build();
    }
}