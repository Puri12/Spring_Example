package com.example.advanced.scheduler;

import com.example.advanced.domain.Comment;
import com.example.advanced.domain.Post;
import com.example.advanced.repository.CommentRepository;
import com.example.advanced.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostManagingScheduler {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  @Scheduled(cron = "0 50 23 * * *")
  public void deleteImage() {
    List<Post> postList = postRepository.findAll();
    for (Post post : postList) {
      List<Comment> commentList = commentRepository.findAllByPost(post);
      if (commentList.size() == 0) {
        postRepository.delete(post);
        log.info("게시물 <" + post.getTitle() + ">이 삭제되었습니다.");
      }
    }
  }
}
