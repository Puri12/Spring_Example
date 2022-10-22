package com.example.advanced.repository;


import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();
  List<Post> findAllByMember(Member member);
}
