package com.example.advanced.controller;

import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.service.LikeService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;

  @RequestMapping(value = "/api/auth/like/post/{id}", method = RequestMethod.POST)
  public ResponseDto<?> likePost(
      @PathVariable Long id,
      HttpServletRequest request
  ) {
    return likeService.likePost(id, request);
  }

  @RequestMapping(value = "/api/auth/like/comment/{id}", method = RequestMethod.POST)
  public ResponseDto<?> likeComment(
      @PathVariable Long id,
      HttpServletRequest request
  ) {
    return likeService.likeComment(id, request);
  }

  @RequestMapping(value = "/api/auth/like/sub-comment/{id}", method = RequestMethod.POST)
  public ResponseDto<?> likeSubComment(
      @PathVariable Long id,
      HttpServletRequest request
  ) {
    return likeService.likeSubComment(id, request);
  }

  @RequestMapping(value = "/api/auth/like/post", method = RequestMethod.POST)
  public ResponseDto<?> getAllLikedPost(
      HttpServletRequest request
  ) {
    return likeService.getAllLikedPost(request);
  }

  @RequestMapping(value = "/api/auth/like/comment", method = RequestMethod.POST)
  public ResponseDto<?> getAllLikedComment(
      HttpServletRequest request
  ) {
    return likeService.getAllLikedComment(request);
  }

  @RequestMapping(value = "/api/auth/like/sub-comment", method = RequestMethod.POST)
  public ResponseDto<?> getAllLikedSubComment(
      HttpServletRequest request
  ) {
    return likeService.getAllLikedSubComment(request);
  }
}
