package com.example.advanced.repository;

import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import com.example.advanced.domain.PostLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
  Optional<PostLike> findByMemberAndPost(Member member, Post post);
  List<PostLike> findAllByPost(Post post);
  List<PostLike> findAllByMember(Member member);
}
