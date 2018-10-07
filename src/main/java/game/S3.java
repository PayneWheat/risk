package main.java.game;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class S3{
    private static AWSCredentials awsCredentials = new BasicAWSCredentials("email","password");  
    private static AmazonS3 s3 = new AmazonS3Client(awsCredentials); 
}
