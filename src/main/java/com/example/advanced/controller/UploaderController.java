//package com.example.advanced.controller;
//
//import com.example.advanced.controller.response.ResponseDto;
//import com.example.advanced.shared.uploader.ImageUploader;
//import java.io.IOException;
//import javax.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RequiredArgsConstructor
//@RestController
//public class UploaderController {
//
//  private final ImageUploader imageUploader;
//
//  @RequestMapping(value = "/api/upload/image", method = RequestMethod.POST)
//  public ResponseDto<?> uploadImage(
//      HttpServletRequest request,
//      MultipartFile multipartFile
//  ) throws IOException {
//    return imageUploader.execute(request, multipartFile);
//  }
//}
