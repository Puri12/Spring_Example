package com.example.advanced.service;

import com.example.advanced.controller.request.SubCommentRequestDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.controller.response.SubCommentResponseDto;
import com.example.advanced.domain.Comment;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.SubComment;
import com.example.advanced.domain.SubCommentLike;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.SubCommentLikeRepository;
import com.example.advanced.repository.SubCommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubCommentService {

  private final SubCommentRepository subCommentRepository;
  private final SubCommentLikeRepository subCommentLikeRepository;

  private final TokenProvider tokenProvider;
  private final CommentService commentService;

  @Transactional
  public ResponseDto<?> createSubComment(
      SubCommentRequestDto requestDto,
      HttpServletRequest request
  ) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Comment comment = commentService.isPresentComment(requestDto.getCommentId());
    if (null == comment)
      return ResponseDto.fail("NOT_FOUND", "comment id is not exist");

    SubComment subComment = SubComment.builder()
        .commentId(comment.getId())
        .member(member)
        .content(requestDto.getContent())
        .build();
    subCommentRepository.save(subComment);
    return ResponseDto.success(
        SubCommentResponseDto.builder()
          .id(subComment.getId())
          .author(member.getNickname())
          .content(subComment.getContent())
          .createdAt(subComment.getCreatedAt())
          .modifiedAt(subComment.getModifiedAt())
          .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllSubCommentByMember(HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    List<SubComment> subCommentList = subCommentRepository.findAllByMember(member);
    List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();

    for (SubComment subComment : subCommentList) {
      subCommentResponseDtoList.add(
          SubCommentResponseDto.builder()
              .id(subComment.getId())
              .author(subComment.getMember().getNickname())
              .content(subComment.getContent())
              .likes(countLikesSubCommentLike(subComment))
              .createdAt(subComment.getCreatedAt())
              .modifiedAt(subComment.getModifiedAt())
              .build()
      );
    }
    return ResponseDto.success(subCommentResponseDtoList);
  }

  @Transactional
  public ResponseDto<?> updateSubComment(
      Long id,
      SubCommentRequestDto requestDto,
      HttpServletRequest request
  ) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Comment comment = commentService.isPresentComment(requestDto.getCommentId());
    if (null == comment)
      return ResponseDto.fail("NOT_FOUND", "comment id is not exist");

    SubComment subComment = isPresentSubComment(id);
    if (null == subComment) {
      return ResponseDto.fail("NOT_FOUND", "sub comment id is not exist");
    }

    if (subComment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "only author can update");
    }

    subComment.update(requestDto);
    return ResponseDto.success(
        SubCommentResponseDto.builder()
            .id(subComment.getId())
            .author(member.getNickname())
            .content(subComment.getContent())
            .createdAt(subComment.getCreatedAt())
            .modifiedAt(subComment.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> deleteSubComment(
      Long id,
      HttpServletRequest request
  ) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Comment comment = commentService.isPresentComment(id);
    if (null == comment)
      return ResponseDto.fail("NOT_FOUND", "comment id is not exist");

    SubComment subComment = isPresentSubComment(id);
    if (null == subComment) {
      return ResponseDto.fail("NOT_FOUND", "sub comment id is not exist");
    }

    if (subComment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "only author can update");
    }

    subCommentRepository.delete(subComment);
    return ResponseDto.success("success");
  }

  @Transactional(readOnly = true)
  public int countLikesSubCommentLike(SubComment subComment) {
    List<SubCommentLike> subCommentLikeList = subCommentLikeRepository.findAllBySubComment(subComment);
    return subCommentLikeList.size();
  }

  @Transactional(readOnly = true)
  public SubComment isPresentSubComment(Long id) {
    Optional<SubComment> optionalSubComment = subCommentRepository.findById(id);
    return optionalSubComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

}
