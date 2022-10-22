package com.example.advanced.controller.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
  private Long id;
  private String title;
  private String content;
  private String imgUrl;
  private String author;
  private int likes;
  private List<CommentResponseDto> comments;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
