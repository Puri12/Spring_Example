//package com.example.advanced.configuration;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//
//@Configuration
//public class AwsConfiguration {
//
//  @Value("${aws.s3.access.key}")
//  String AWS_S3_ACCESS_KEY;
//
//  @Value("${aws.s3.secret.key}")
//  String AWS_S3_SECRET_KEY;
//
//  @Bean
//  @Primary
//  public AwsBasicCredentials awsS3CredentialProvider() {
//    return AwsBasicCredentials.create(AWS_S3_ACCESS_KEY, AWS_S3_SECRET_KEY);
//  }
//
//  @Bean
//  public S3Client s3Client() {
//    return S3Client.builder()
//        .credentialsProvider(
//            this::awsS3CredentialProvider
//        )
//        .region(Region.AP_NORTHEAST_2)
//        .build();
//  }
//
//}
