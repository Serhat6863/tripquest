package com.tripquest.backend.user.repository;

import com.tripquest.backend.user.entity.Badge;
import com.tripquest.backend.user.entity.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    List<Badge> findByUserId(Long userId);

    boolean existsByUserIdAndType(Long userId, BadgeType type);
}
