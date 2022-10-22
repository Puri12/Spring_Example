package com.example.advanced.repository;

import com.example.advanced.domain.Member;
import com.example.advanced.domain.SubComment;
import com.example.advanced.domain.SubCommentLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCommentLikeRepository extends JpaRepository<SubCommentLike, Long> {
  Optional<SubCommentLike> findByMemberAndSubComment(Member member, SubComment subComment);
  List<SubCommentLike> findAllBySubComment(SubComment subComment);
  List<SubCommentLike> findAllByMember(Member member);
}
