package com.example.advanced.controller;

import com.example.advanced.controller.request.SubCommentRequestDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.service.SubCommentService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
public class SubCommentController {

  private final SubCommentService subCommentService;

  @RequestMapping(value = "/api/auth/sub-comment", method = RequestMethod.POST)
  public ResponseDto<?> createComment(@RequestBody SubCommentRequestDto requestDto,
      HttpServletRequest request) {
    return subCommentService.createSubComment(requestDto, request);
  }

  @RequestMapping(value = "/api/auth/sub-comment/{id}", method = RequestMethod.POST)
  public ResponseDto<?> updateSubComment(
      @PathVariable Long id,
      @RequestBody SubCommentRequestDto requestDto,
      HttpServletRequest request) {
    return subCommentService.updateSubComment(id, requestDto, request);
  }

  @RequestMapping(value = "/api/auth/sub-comment/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> createComment(@PathVariable Long id,
      HttpServletRequest request) {
    return subCommentService.deleteSubComment(id, request);
  }

  @RequestMapping(value = "/api/auth/sub-comment", method = RequestMethod.GET)
  public ResponseDto<?> getAllPostByMember(HttpServletRequest request) {
    return subCommentService.getAllSubCommentByMember(request);
  }
}
