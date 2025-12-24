package com.project.deartime.app.friend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {

    @NotNull(message = "친구 ID는 필수입니다.")
    private Long friendId;
}