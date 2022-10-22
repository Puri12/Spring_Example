package com.example.advanced.controller.exception;

public class RemoveFileException extends RuntimeException {
  public RemoveFileException() {
    super("fail to remove target file");
  }
}
