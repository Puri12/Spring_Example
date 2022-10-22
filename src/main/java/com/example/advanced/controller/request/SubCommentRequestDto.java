package com.example.advanced.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubCommentRequestDto {
  private Long commentId;
  private String content;
}
