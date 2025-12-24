package com.project.deartime.app.friend.controller;

import com.project.deartime.app.friend.dto.*;
import com.project.deartime.app.friend.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    /**
     * 친구 닉네임 검색
     * GET /api/friends/search?keyword={닉네임}
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFriends(
            @AuthenticationPrincipal String userId,
            @RequestParam("keyword") String keyword
    ) {
        System.out.println("=== 친구 검색 ===");
        System.out.println("userId: " + userId);
        System.out.println("keyword: " + keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", "검색어를 입력해주세요.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        List<FriendSearchResponse> searchResults =
                friendService.searchFriendsByNickname(Long.parseLong(userId), keyword.trim());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", 200);
        responseBody.put("message", "검색 성공");
        responseBody.put("count", searchResults.size());
        responseBody.put("results", searchResults);

        return ResponseEntity.ok(responseBody);
    }

    /**
     * 친구 추가 요청
     * POST /api/friends
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> sendFriendRequest(
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid FriendRequestDto request
    ) {
        System.out.println("=== 친구 추가 요청 ===");
        System.out.println("userId: " + userId);
        System.out.println("friendId: " + request.getFriendId());

        try {
            FriendResponseDto friendResponse =
                    friendService.sendFriendRequest(Long.parseLong(userId), request.getFriendId());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", 200);
            responseBody.put("message", "친구 요청 성공");
            responseBody.put("data", friendResponse);

            return ResponseEntity.ok(responseBody);

        } catch (IllegalArgumentException e) {
            System.out.println("❌ 친구 요청 실패: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.out.println("❌ 서버 오류: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "서버 오류가 발생했습니다.");

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 친구 관계 상태 변경 (수락/거절/차단)
     * PUT /api/friends/{friendId}
     * Body: { "status": "accepted" | "rejected" | "blocked" }
     */
    @PutMapping("/{friendId}")
    public ResponseEntity<Map<String, Object>> updateFriendStatus(
            @AuthenticationPrincipal String userId,
            @PathVariable Long friendId,
            @RequestBody @Valid FriendStatusUpdateDto request
    ) {
        System.out.println("=== 친구 관계 상태 변경 ===");
        System.out.println("userId: " + userId);
        System.out.println("friendId: " + friendId);
        System.out.println("status: " + request.getStatus());

        try {
            String status = request.getStatus();
            String message;
            Object data = null;

            switch (status) {
                case "accepted":
                    FriendResponseDto acceptedFriend =
                            friendService.acceptFriendRequest(Long.parseLong(userId), friendId);
                    message = "친구 요청을 수락했습니다.";
                    data = acceptedFriend;
                    break;

                case "rejected":
                    friendService.rejectFriendRequest(Long.parseLong(userId), friendId);
                    message = "친구 요청을 거절했습니다.";
                    break;

                case "blocked":
                    FriendResponseDto blockedFriend =
                            friendService.blockFriend(Long.parseLong(userId), friendId);
                    message = "사용자를 차단했습니다.";
                    data = blockedFriend;
                    break;

                default:
                    throw new IllegalArgumentException("유효하지 않은 상태값입니다: " + status);
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", 200);
            responseBody.put("message", message);
            if (data != null) {
                responseBody.put("data", data);
            }

            return ResponseEntity.ok(responseBody);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 친구 삭제
     * DELETE /api/friends/{friendId}
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Map<String, Object>> deleteFriend(
            @AuthenticationPrincipal String userId,
            @PathVariable Long friendId
    ) {
        System.out.println("=== 친구 삭제 ===");
        System.out.println("userId: " + userId);
        System.out.println("friendId: " + friendId);

        try {
            friendService.deleteFriend(Long.parseLong(userId), friendId);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", 200);
            responseBody.put("message", "친구 관계를 삭제했습니다.");

            return ResponseEntity.ok(responseBody);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 대리인 설정
     * PUT /api/friends/{friendId}/proxy
     */
    @PutMapping("/{friendId}/proxy")
    public ResponseEntity<Map<String, Object>> setProxy(
            @AuthenticationPrincipal String userId,
            @PathVariable Long friendId,
            @RequestBody @Valid ProxyRequestDto request
    ) {
        System.out.println("=== 대리인 설정 ===");
        System.out.println("userId: " + userId);
        System.out.println("proxyUserId (friendId): " + friendId);
        System.out.println("expiredAt: " + request.getExpiredAt());

        try {
            ProxyResponseDto proxyResponse =
                    friendService.setProxy(Long.parseLong(userId), friendId, request.getExpiredAt());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", 200);
            responseBody.put("message", "대리인을 설정했습니다.");
            responseBody.put("data", proxyResponse);

            return ResponseEntity.ok(responseBody);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 대리인 해제
     * DELETE /api/friends/{friendId}/proxy
     */
    @DeleteMapping("/{friendId}/proxy")
    public ResponseEntity<Map<String, Object>> removeProxy(
            @AuthenticationPrincipal String userId,
            @PathVariable Long friendId
    ) {
        System.out.println("=== 대리인 해제 ===");
        System.out.println("userId: " + userId);
        System.out.println("proxyUserId (friendId): " + friendId);

        try {
            friendService.removeProxy(Long.parseLong(userId), friendId);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", 200);
            responseBody.put("message", "대리인을 해제했습니다.");

            return ResponseEntity.ok(responseBody);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}