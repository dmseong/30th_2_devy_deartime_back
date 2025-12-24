package com.project.deartime.app.friend.service;

import com.project.deartime.app.auth.repository.UserRepository;
import com.project.deartime.app.domain.Friend;
import com.project.deartime.app.domain.User;
import com.project.deartime.app.friend.dto.FriendResponseDto;
import com.project.deartime.app.friend.dto.FriendSearchResponse;
import com.project.deartime.app.friend.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    /**
     * 닉네임으로 친구 검색
     */
    public List<FriendSearchResponse> searchFriendsByNickname(Long currentUserId, String keyword) {
        List<User> searchedUsers = userRepository.searchByNickname(keyword, currentUserId);

        List<FriendSearchResponse> responses = new ArrayList<>();

        for (User user : searchedUsers) {
            String friendStatus = determineFriendStatus(currentUserId, user.getId());

            FriendSearchResponse response = FriendSearchResponse.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImageUrl())
                    .bio(user.getBio())
                    .friendStatus(friendStatus)
                    .build();

            responses.add(response);
        }

        return responses;
    }

    /**
     * 친구 추가 요청
     */
    @Transactional
    public FriendResponseDto sendFriendRequest(Long userId, Long friendId) {
        // 1. 본인에게 친구 요청하는 경우
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        // 2. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구를 찾을 수 없습니다."));

        // 3. 이미 친구 관계가 있는지 확인 (양방향)
        List<Friend> existingFriendships = friendRepository.findFriendshipBetween(userId, friendId);

        for (Friend existing : existingFriendships) {
            String status = existing.getStatus();

            if ("accepted".equals(status)) {
                throw new IllegalArgumentException("이미 친구 관계입니다.");
            }

            if ("pending".equals(status)) {
                // 상대방이 나에게 이미 요청을 보낸 경우 -> 자동으로 수락
                if (existing.getUser().getId().equals(friendId) &&
                        existing.getFriend().getId().equals(userId)) {
                    existing = Friend.builder()
                            .user(existing.getUser())
                            .friend(existing.getFriend())
                            .status("accepted")
                            .requestedAt(existing.getRequestedAt())
                            .build();
                    Friend savedFriend = friendRepository.save(existing);
                    return FriendResponseDto.from(savedFriend);
                }

                // 내가 이미 요청을 보낸 경우
                if (existing.getUser().getId().equals(userId) &&
                        existing.getFriend().getId().equals(friendId)) {
                    throw new IllegalArgumentException("이미 친구 요청을 보냈습니다.");
                }
            }

            if ("blocked".equals(status)) {
                throw new IllegalArgumentException("차단된 사용자입니다.");
            }
        }

        // 4. 새로운 친구 요청 생성
        Friend newFriendRequest = Friend.builder()
                .user(user)
                .friend(friend)
                .status("pending")
                .requestedAt(LocalDateTime.now())
                .build();

        Friend savedFriend = friendRepository.save(newFriendRequest);

        return FriendResponseDto.from(savedFriend);
    }

    /**
     * 두 사용자 간의 친구 관계 상태 확인
     */
    private String determineFriendStatus(Long userId1, Long userId2) {
        Optional<Friend> sentRequest = friendRepository.findByUserIdAndFriendId(userId1, userId2);

        Optional<Friend> receivedRequest = friendRepository.findByUserIdAndFriendId(userId2, userId1);

        if (sentRequest.isPresent()) {
            return sentRequest.get().getStatus();
        }

        if (receivedRequest.isPresent()) {
            String status = receivedRequest.get().getStatus();
            if ("pending".equals(status)) {
                return "received";
            }
            return status;
        }

        return "none";
    }
}