//package com.example.advanced.shared.uploader;
//
//import com.example.advanced.controller.exception.EmptyMultipartFileException;
//import com.example.advanced.controller.exception.FileConvertException;
//import com.example.advanced.controller.exception.RemoveFileException;
//import com.example.advanced.controller.response.ResponseDto;
//import com.example.advanced.domain.Member;
//import com.example.advanced.jwt.TokenProvider;
//import com.example.advanced.shared.MutipartToFileConverter;
//import java.io.File;
//import java.io.IOException;
//import java.time.Instant;
//import java.time.ZoneId;
//import java.util.Objects;
//import java.util.UUID;
//import javax.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//@Service
//@RequiredArgsConstructor
//public class ImageUploader {
//
//  @Value("${aws.s3.bucket.name}")
//  private String BUCKET;
//
//  @Value("${aws.s3.path.url}")
//  private String BUCKET_PATH;
//
//  private final S3Client s3Client;
//  private final MutipartToFileConverter mutipartToFileConverter;
//  private final TokenProvider tokenProvider;
//
//  public ResponseDto<?> execute(
//      HttpServletRequest request,
//      MultipartFile multipartFile
//  ) throws IOException {
//
//    Member member = validateMember(request);
//    if (null == member) {
//      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
//    }
//
//    if (multipartFile.isEmpty()) {
//      throw new EmptyMultipartFileException();
//    }
//
//    File licenseFile = mutipartToFileConverter.convert(multipartFile)
//        .orElseThrow(FileConvertException::new);
//
//    if (multipartFile.isEmpty()) {
//      throw new EmptyMultipartFileException();
//    }
//
//    String now = Instant
//        .now().atZone(ZoneId.of("Asia/Seoul")).toString()
//        .replace("T", "-")
//        .replace("Z", "")
//        .replace("[Asia/Seoul]", "");
//    String fileName = "/images/" + now + UUID.randomUUID() + "."
//        + Objects.requireNonNull(multipartFile.getOriginalFilename()).split("\\.")[1];
//
//    System.out.println("fileName = " + fileName);
//
//    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//        .bucket(BUCKET)
//        .key(fileName)
//        .build();
//
//
//    s3Client.putObject(putObjectRequest, RequestBody.fromFile(licenseFile));
//    removeNewFile(licenseFile);
//
//    return ResponseDto.success(
//        BUCKET_PATH + fileName
//    );
//
//  }
//
//  private void removeNewFile(File targetFile) {
//    if (!targetFile.delete()) {
//      throw new RemoveFileException();
//    }
//  }
//
//  @Transactional
//  public Member validateMember(HttpServletRequest request) {
//    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
//      return null;
//    }
//    return tokenProvider.getMemberFromAuthentication();
//  }
//}
