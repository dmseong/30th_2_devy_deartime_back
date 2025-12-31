package com.project.deartime.app.capsule.repository;

import com.project.deartime.app.domain.TimeCapsule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TimeCapsuleRepository extends JpaRepository<TimeCapsule, Long> {

    Page<TimeCapsule> findBySenderId(Long senderId, Pageable pageable);

    Page<TimeCapsule> findByReceiverId(Long receiverId, Pageable pageable);

    /**
     * 모든 캡슐 (보낸 것 + 받은 것)
     */
    @Query("SELECT tc FROM TimeCapsule tc WHERE tc.sender.id = :userId OR tc.receiver.id = :userId")
    Page<TimeCapsule> findAllCapsules(@Param("userId") Long userId, Pageable pageable);

    /**
     * 개봉된 캡슐만
     */
    @Query("SELECT tc FROM TimeCapsule tc WHERE tc.openAt <= CURRENT_TIMESTAMP AND (tc.sender.id = :userId OR tc.receiver.id = :userId)")
    Page<TimeCapsule> findOpenedCapsules(@Param("userId") Long userId, Pageable pageable);
}


