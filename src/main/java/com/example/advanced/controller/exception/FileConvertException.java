package com.example.advanced.controller.exception;

public class FileConvertException extends RuntimeException {

  public FileConvertException() {
    super("fail convert multipartfile to file");
  }
}
