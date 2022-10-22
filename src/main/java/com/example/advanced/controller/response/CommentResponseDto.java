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
public class CommentResponseDto {
  private Long id;
  private String author;
  private String content;
  private int likes;
  private List<SubCommentResponseDto> subComments;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
