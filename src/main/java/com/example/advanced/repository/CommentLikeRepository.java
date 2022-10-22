package com.example.advanced.repository;

import com.example.advanced.domain.Comment;
import com.example.advanced.domain.CommentLike;
import com.example.advanced.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
  Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);
  List<CommentLike> findAllByComment(Comment comment);
  List<CommentLike> findAllByMember(Member member);
}
