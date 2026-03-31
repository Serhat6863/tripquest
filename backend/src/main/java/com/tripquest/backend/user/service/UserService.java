package com.tripquest.backend.user.service;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.auth.repository.UserRepository;
import com.tripquest.backend.user.dto.BucketlistRequest;
import com.tripquest.backend.user.dto.BucketlistResponse;
import com.tripquest.backend.user.dto.UpdateProfileRequest;
import com.tripquest.backend.user.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    @Lazy
    private final BadgeService badgeService;

    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return toResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserProfileResponse addVisitedCountry(Long userId, String countryCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (!user.getVisitedCountries().contains(countryCode)) {
            user.getVisitedCountries().add(countryCode);
            user.setTravelScore(user.getTravelScore() + 10);
            user.setLevel(user.getVisitedCountries().size() / 5 + 1);
        }

        user.getBucketlistCountries().remove(countryCode);
        userRepository.save(user);

        badgeService.checkAndAwardBadges(userId);
        return toResponse(user);
    }

    @Transactional
    public BucketlistResponse addToBucketlist(Long userId, BucketlistRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        String countryCode = request.getCountryCode();

        if (user.getVisitedCountries().contains(countryCode)) {
            throw new RuntimeException("Country already visited: " + countryCode);
        }
        if (user.getBucketlistCountries().contains(countryCode)) {
            throw new RuntimeException("Country already in bucketlist: " + countryCode);
        }

        user.getBucketlistCountries().add(countryCode);
        userRepository.save(user);

        return toBucketlistResponse(user);
    }

    @Transactional
    public BucketlistResponse removeFromBucketlist(Long userId, String countryCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.getBucketlistCountries().remove(countryCode);
        userRepository.save(user);

        return toBucketlistResponse(user);
    }

    public BucketlistResponse getBucketlist(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return toBucketlistResponse(user);
    }

    private BucketlistResponse toBucketlistResponse(User user) {
        return BucketlistResponse.builder()
                .countryCodes(user.getBucketlistCountries())
                .totalCount(user.getBucketlistCountries().size())
                .build();
    }

    private UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getDisplayUsername())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .travelScore(user.getTravelScore())
                .level(user.getLevel())
                .createdAt(user.getCreatedAt())
                .visitedCountries(user.getVisitedCountries())
                .build();
    }

    // TODO: Add getUserRanking, searchUsers methods
}
