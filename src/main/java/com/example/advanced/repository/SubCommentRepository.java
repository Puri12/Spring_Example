package com.example.advanced.repository;

import com.example.advanced.domain.Member;
import com.example.advanced.domain.SubComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
  List<SubComment> findAllByCommentId(Long commentId);
  List<SubComment> findAllByMember(Member member);
  Optional<SubComment> findById(Long id);
  int countAllByCommentId(Long id);
}
