package com.example.advanced.service;

import com.example.advanced.controller.response.CommentResponseDto;
import com.example.advanced.controller.response.PostResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.controller.response.SubCommentResponseDto;
import com.example.advanced.domain.Comment;
import com.example.advanced.domain.CommentLike;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import com.example.advanced.domain.PostLike;
import com.example.advanced.domain.SubComment;
import com.example.advanced.domain.SubCommentLike;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.CommentLikeRepository;
import com.example.advanced.repository.CommentRepository;
import com.example.advanced.repository.PostLikeRepository;
import com.example.advanced.repository.PostRepository;
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
public class LikeService {

  private final PostLikeRepository postLikeRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final SubCommentLikeRepository subCommentLikeRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final SubCommentRepository subCommentRepository;

  private final TokenProvider tokenProvider;
  private final PostService postService;
  private final CommentService commentService;
  private final SubCommentService subCommentService;

  @Transactional
  public ResponseDto<?> likePost(Long id, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Post post = postService.isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "post id is not exist");
    }

    PostLike postLike = isPresentPostLike(member, post);
    if (null == postLike) {
      postLikeRepository.save(
          PostLike.builder()
              .member(member)
              .post(post)
              .build()
      );
      return ResponseDto.success("like success");
    } else {
      postLikeRepository.delete(postLike);
      return ResponseDto.success("cancel like success");
    }
  }

  @Transactional
  public ResponseDto<?> likeComment(Long id, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Comment commment = commentService.isPresentComment(id);
    if (null == commment) {
      return ResponseDto.fail("NOT_FOUND", "comment id is not exist");
    }

    CommentLike commentLike = isPresentCommentLike(member, commment);
    if (null == commentLike) {
      commentLikeRepository.save(
          CommentLike.builder()
              .member(member)
              .comment(commment)
              .build()
      );
      return ResponseDto.success("like success");
    } else {
      commentLikeRepository.delete(commentLike);
      return ResponseDto.success("cancel like success");
    }
  }

  @Transactional
  public ResponseDto<?> likeSubComment(Long id, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    SubComment subComment = subCommentService.isPresentSubComment(id);
    if (null == subComment) {
      return ResponseDto.fail("NOT_FOUND", "sub comment id is not exist");
    }

    SubCommentLike subCommentLike = isPresentSubCommentLike(member, subComment);
    if (null == subCommentLike) {
      subCommentLikeRepository.save(
          SubCommentLike.builder()
          .member(member)
          .subComment(subComment)
          .build()
      );
      return ResponseDto.success("like success");
    } else {
      subCommentLikeRepository.delete(subCommentLike);
      return ResponseDto.success("cancel like success");
    }
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllLikedPost(HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    List<PostLike> postLikeList = postLikeRepository.findAllByMember(member);
    List<PostResponseDto> postResponseDtoList = new ArrayList<>();
    for (PostLike postLike : postLikeList) {
      Post post = postLike.getPost();

      List<Comment> commentList = commentRepository.findAllByPost(post);
      List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
      for (Comment comment : commentList) {
        List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(comment.getId());
        List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();
        for (SubComment subComment : subCommentList) {
          subCommentResponseDtoList.add(
              SubCommentResponseDto.builder()
                  .id(subComment.getId())
                  .content(subComment.getContent())
                  .author(subComment.getMember().getNickname())
                  .likes(countLikesSubCommentLike(subComment))
                  .createdAt(subComment.getCreatedAt())
                  .modifiedAt(subComment.getModifiedAt())
                  .build()
          );
        }
        commentResponseDtoList.add(
            CommentResponseDto.builder()
                .id(comment.getId())
                .author(comment.getMember().getNickname())
                .content(comment.getContent())
                .likes(countLikesComment(comment))
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build()
        );

        postResponseDtoList.add(
            PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imgUrl(post.getImgUrl())
                .author(post.getMember().getNickname())
                .likes(countLikesPost(post))
                .comments(commentResponseDtoList)
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build()
        );
      }
    }

    return ResponseDto.success(postResponseDtoList);
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllLikedComment(HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    List<CommentLike> commentLikeList = commentLikeRepository.findAllByMember(member);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
    for (CommentLike commentLike : commentLikeList) {
      Comment comment = commentLike.getComment();
      commentResponseDtoList.add(
          CommentResponseDto.builder()
              .id(comment.getId())
              .author(comment.getMember().getNickname())
              .content(comment.getContent())
              .likes(countLikesComment(comment))
              .createdAt(comment.getCreatedAt())
              .modifiedAt(comment.getModifiedAt())
              .build()
      );
    }

    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllLikedSubComment(HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    List<SubCommentLike> subCommentLikeList = subCommentLikeRepository.findAllByMember(member);
    List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();
    for (SubCommentLike subCommentLike : subCommentLikeList) {
      SubComment subComment = subCommentLike.getSubComment();
      subCommentResponseDtoList.add(
          SubCommentResponseDto.builder()
              .id(subComment.getId())
              .content(subComment.getContent())
              .author(subComment.getMember().getNickname())
              .likes(countLikesSubCommentLike(subComment))
              .createdAt(subComment.getCreatedAt())
              .modifiedAt(subComment.getModifiedAt())
              .build()
      );
    }

    return ResponseDto.success(subCommentResponseDtoList);
  }

  @Transactional(readOnly = true)
  public PostLike isPresentPostLike(Member member, Post post) {
    Optional<PostLike> optionalPostLike = postLikeRepository.findByMemberAndPost(member, post);
    return optionalPostLike.orElse(null);
  }

  @Transactional(readOnly = true)
  public CommentLike isPresentCommentLike(Member member, Comment comment) {
    Optional<CommentLike> optionalCommentLike = commentLikeRepository.findByMemberAndComment(member, comment);
    return optionalCommentLike.orElse(null);
  }

  @Transactional(readOnly = true)
  public SubCommentLike isPresentSubCommentLike(Member member, SubComment subComment) {
    Optional<SubCommentLike> optionalSubCommentLike =
        subCommentLikeRepository.findByMemberAndSubComment(member, subComment);
    return optionalSubCommentLike.orElse(null);
  }

  @Transactional(readOnly = true)
  public int countLikesPost(Post post) {
    List<PostLike> postLikeList = postLikeRepository.findAllByPost(post);
    return postLikeList.size();
  }

  @Transactional(readOnly = true)
  public int countLikesComment(Comment comment) {
    List<CommentLike> commentLikeList = commentLikeRepository.findAllByComment(comment);
    return commentLikeList.size();
  }

  @Transactional(readOnly = true)
  public int countLikesSubCommentLike(SubComment subComment) {
    List<SubCommentLike> subCommentLikeList = subCommentLikeRepository.findAllBySubComment(subComment);
    return subCommentLikeList.size();
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
}
