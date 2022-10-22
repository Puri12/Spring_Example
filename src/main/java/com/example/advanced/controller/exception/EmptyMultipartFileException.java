package com.example.advanced.controller.exception;

public class EmptyMultipartFileException extends RuntimeException {
  public EmptyMultipartFileException() {
    super("multipart file is empty");
  }
}
