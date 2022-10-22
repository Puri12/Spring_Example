package com.example.advanced.repository;


import com.example.advanced.domain.Comment;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByPost(Post post);
  List<Comment> findAllByMember(Member member);
  int countAllByPost(Post post);
}
