package com.tripquest.backend.friends.service;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.auth.repository.UserRepository;
import com.tripquest.backend.friends.dto.FriendResponse;
import com.tripquest.backend.friends.dto.FriendshipResponse;
import com.tripquest.backend.friends.entity.Friendship;
import com.tripquest.backend.friends.entity.FriendshipStatus;
import com.tripquest.backend.friends.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Transactional
    public FriendshipResponse sendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }

        friendshipRepository.findBySenderIdAndReceiverId(senderId, receiverId).ifPresent(f -> {
            throw new RuntimeException("Friend request already sent");
        });
        friendshipRepository.findBySenderIdAndReceiverId(receiverId, senderId).ifPresent(f -> {
            throw new RuntimeException("Already friends or request pending");
        });

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found: " + receiverId));

        Friendship friendship = Friendship.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .build();

        return toFriendshipResponse(friendshipRepository.save(friendship));
    }

    @Transactional
    public FriendshipResponse acceptRequest(Long friendshipId, Long currentUserId) {
        Friendship friendship = findFriendshipOrThrow(friendshipId);

        if (!friendship.getReceiver().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized: only the receiver can accept this request");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return toFriendshipResponse(friendshipRepository.save(friendship));
    }

    @Transactional
    public FriendshipResponse declineRequest(Long friendshipId, Long currentUserId) {
        Friendship friendship = findFriendshipOrThrow(friendshipId);

        if (!friendship.getReceiver().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized: only the receiver can decline this request");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);
        return toFriendshipResponse(friendshipRepository.save(friendship));
    }

    public List<FriendResponse> getFriends(Long userId) {
        return friendshipRepository.findAcceptedFriendships(userId).stream()
                .map(f -> {
                    User friend = f.getSender().getId().equals(userId) ? f.getReceiver() : f.getSender();
                    return toFriendResponse(friend);
                })
                .toList();
    }

    public List<FriendshipResponse> getPendingRequests(Long userId) {
        return friendshipRepository.findByReceiverIdAndStatus(userId, FriendshipStatus.PENDING).stream()
                .map(this::toFriendshipResponse)
                .toList();
    }

    private Friendship findFriendshipOrThrow(Long friendshipId) {
        return friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship not found: " + friendshipId));
    }

    private FriendResponse toFriendResponse(User user) {
        return FriendResponse.builder()
                .id(user.getId())
                .username(user.getDisplayUsername())
                .avatarUrl(user.getAvatarUrl())
                .travelScore(user.getTravelScore())
                .level(user.getLevel())
                .build();
    }

    private FriendshipResponse toFriendshipResponse(Friendship friendship) {
        return FriendshipResponse.builder()
                .id(friendship.getId())
                .sender(toFriendResponse(friendship.getSender()))
                .receiver(toFriendResponse(friendship.getReceiver()))
                .status(friendship.getStatus())
                .createdAt(friendship.getCreatedAt())
                .build();
    }
}
