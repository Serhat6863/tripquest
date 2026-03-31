package com.tripquest.backend.feed.service;

import com.tripquest.backend.friends.entity.Friendship;
import com.tripquest.backend.friends.repository.FriendshipRepository;
import com.tripquest.backend.post.dto.PostResponse;
import com.tripquest.backend.post.repository.PostRepository;
import com.tripquest.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FriendshipRepository friendshipRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    public Page<PostResponse> getFeed(Long currentUserId, int page, int size) {
        List<Friendship> acceptedFriendships = friendshipRepository.findAcceptedFriendships(currentUserId);

        List<Long> authorIds = new ArrayList<>();
        authorIds.add(currentUserId);

        for (Friendship friendship : acceptedFriendships) {
            Long friendId = friendship.getSender().getId().equals(currentUserId)
                    ? friendship.getReceiver().getId()
                    : friendship.getSender().getId();
            authorIds.add(friendId);
        }

        return postRepository
                .findByAuthorIdInOrderByCreatedAtDesc(authorIds, PageRequest.of(page, size))
                .map(post -> postService.toResponse(post, null));
    }
}
