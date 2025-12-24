package com.project.deartime.app.friend.controller;

import com.project.deartime.app.friend.dto.FriendRequestDto;
import com.project.deartime.app.friend.dto.FriendResponseDto;
import com.project.deartime.app.friend.dto.FriendSearchResponse;
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
}