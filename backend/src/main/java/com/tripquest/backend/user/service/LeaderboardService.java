package com.tripquest.backend.user.service;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.auth.repository.UserRepository;
import com.tripquest.backend.friends.repository.FriendshipRepository;
import com.tripquest.backend.user.dto.LeaderboardEntry;
import com.tripquest.backend.user.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;

    public List<LeaderboardEntry> getFriendsLeaderboard(Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUserId));

        List<User> participants = new ArrayList<>();
        participants.add(currentUser);

        friendshipRepository.findAcceptedFriendships(currentUserId).forEach(f -> {
            User friend = f.getSender().getId().equals(currentUserId)
                    ? f.getReceiver()
                    : f.getSender();
            participants.add(friend);
        });

        return rankUsers(participants);
    }

    public List<LeaderboardEntry> getGlobalLeaderboard() {
        return rankUsers(userRepository.findTop50ByOrderByTravelScoreDesc());
    }

    private List<LeaderboardEntry> rankUsers(List<User> users) {
        AtomicInteger rank = new AtomicInteger(1);

        return users.stream()
                .sorted(Comparator.comparingInt(User::getTravelScore).reversed())
                .map(user -> LeaderboardEntry.builder()
                        .rank(rank.getAndIncrement())
                        .userId(user.getId())
                        .username(user.getDisplayUsername())
                        .avatarUrl(user.getAvatarUrl())
                        .travelScore(user.getTravelScore())
                        .level(user.getLevel())
                        .visitedCountriesCount(user.getVisitedCountries().size())
                        .badgesCount(badgeRepository.findByUserId(user.getId()).size())
                        .build())
                .toList();
    }
}
