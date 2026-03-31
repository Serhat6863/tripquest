package com.tripquest.backend.user.service;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.auth.repository.UserRepository;
import com.tripquest.backend.friends.repository.FriendshipRepository;
import com.tripquest.backend.post.repository.PostRepository;
import com.tripquest.backend.user.dto.BadgeResponse;
import com.tripquest.backend.user.entity.Badge;
import com.tripquest.backend.user.entity.BadgeType;
import com.tripquest.backend.user.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public void checkAndAwardBadges(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        checkPostBadges(user);
        checkCountryBadges(user);
        checkFriendBadges(user);
    }

    private void checkPostBadges(User user) {
        long postCount = postRepository.countByAuthorId(user.getId());
        if (postCount >= 1)  awardIfNotExists(user, BadgeType.FIRST_POST);
        if (postCount >= 5)  awardIfNotExists(user, BadgeType.PHOTO_REPORTER);
        if (postCount >= 10) awardIfNotExists(user, BadgeType.CRITIC);
    }

    private void checkCountryBadges(User user) {
        List<String> visited = user.getVisitedCountries();
        int size = visited.size();

        if (size >= 5)  awardIfNotExists(user, BadgeType.EXPLORER_5);
        if (size >= 10) awardIfNotExists(user, BadgeType.EXPLORER_10);
        if (size >= 20) awardIfNotExists(user, BadgeType.EXPLORER_20);

        List<String> europe  = List.of("FR","DE","IT","ES","PT","NL","BE","CH","AT","PL","SE","NO","DK","FI","GR");
        List<String> asia    = List.of("JP","CN","KR","TH","VN","ID","MY","SG","IN","PH","TR","AE","SA","IL");
        List<String> america = List.of("US","CA","MX","BR","AR","CO","PE","CL","CU");
        List<String> africa  = List.of("MA","EG","ZA","NG","KE","ET","GH","TN","SN");

        if (visited.stream().anyMatch(europe::contains))  awardIfNotExists(user, BadgeType.CONTINENT_EUROPE);
        if (visited.stream().anyMatch(asia::contains))    awardIfNotExists(user, BadgeType.CONTINENT_ASIA);
        if (visited.stream().anyMatch(america::contains)) awardIfNotExists(user, BadgeType.CONTINENT_AMERICA);
        if (visited.stream().anyMatch(africa::contains))  awardIfNotExists(user, BadgeType.CONTINENT_AFRICA);
    }

    private void checkFriendBadges(User user) {
        long friendsCount = friendshipRepository.findAcceptedFriendships(user.getId()).size();
        if (friendsCount >= 5)  awardIfNotExists(user, BadgeType.SOCIAL_5);
        if (friendsCount >= 10) awardIfNotExists(user, BadgeType.SOCIAL_10);
    }

    private void awardIfNotExists(User user, BadgeType type) {
        if (!badgeRepository.existsByUserIdAndType(user.getId(), type)) {
            badgeRepository.save(Badge.builder().user(user).type(type).build());
        }
    }

    public List<BadgeResponse> getUserBadges(Long userId) {
        return badgeRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private BadgeResponse toResponse(Badge badge) {
        return BadgeResponse.builder()
                .id(badge.getId())
                .type(badge.getType())
                .label(badge.getType().getLabel())
                .description(badge.getType().getDescription())
                .unlockedAt(badge.getUnlockedAt())
                .build();
    }
}
