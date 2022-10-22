package com.example.advanced.controller.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {
  private Long id;
  private String title;
  private String content;
  private String imgUrl;
  private String author;
  private int likes;
  private int commentsNum;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
