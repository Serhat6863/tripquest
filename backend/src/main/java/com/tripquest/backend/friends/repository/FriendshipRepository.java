package com.tripquest.backend.friends.repository;

import com.tripquest.backend.friends.entity.Friendship;
import com.tripquest.backend.friends.entity.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Friendship> findByReceiverIdAndStatus(Long receiverId, FriendshipStatus status);

    @Query("""
            SELECT f FROM Friendship f
            WHERE f.status = 'ACCEPTED'
            AND (f.sender.id = :userId OR f.receiver.id = :userId)
            """)
    List<Friendship> findAcceptedFriendships(@Param("userId") Long userId);
}
