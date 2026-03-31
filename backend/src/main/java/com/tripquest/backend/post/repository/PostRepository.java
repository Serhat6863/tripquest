package com.tripquest.backend.post.repository;

import com.tripquest.backend.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    List<Post> findByCountryCodeOrderByCreatedAtDesc(String countryCode);

    List<Post> findByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds, Pageable pageable);
}
