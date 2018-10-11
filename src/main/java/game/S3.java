package main.java.game;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;

public class S3 {
    private static AWSCredentials awsCredentials = null;  
    private static AmazonS3 s3 = null; 
	public S3() {
		Properties prop = new Properties();
		InputStream input = null;
		String awsUser = null;
		String awsPassword = null;
		try {
			input = new FileInputStream("secrets_PHP.prop");
			
			prop.load(input);
			awsUser = prop.getProperty("awsuser");
			awsPassword = prop.getProperty("awspassword");
			System.out.println(awsUser);
			System.out.println(awsPassword);
		} catch(IOException e) {
			e.printStackTrace();
		}
		awsCredentials = new BasicAWSCredentials(awsUser, awsPassword);
		s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		//AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
		
		String bucket_name = "riskreplay";
		Bucket b = null;
		
		if(s3.doesBucketExistV2(bucket_name)) {
			System.out.println("Bucket already exists.");
			b = getBucket(bucket_name);
			
		} else {
			try {
				b = s3.createBucket(bucket_name);
				System.out.println("New bucket created");
			} catch(AmazonS3Exception e) {
				System.err.println(e.getErrorMessage());
			}
		}
		
		
	}
	public static Bucket getBucket(String bucket_name) {
		// https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/java/example_code/s3/src/main/java/aws/example/s3/CreateBucket.java
        
		final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        Bucket named_bucket = null;
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            if (b.getName().equals(bucket_name)) {
                named_bucket = b;
            }
        }
        return named_bucket;
        
	}
}
