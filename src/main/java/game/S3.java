package main.java.game;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3 {
	public PlayerActivities pa = null;
    private static AWSCredentials awsCredentials = null;  
    private static AmazonS3 s3 = null; 
    private static Bucket rrBucket = null;
    private static String gameKey = null;
	public S3() {
		Properties prop = new Properties();
		pa = new PlayerActivities();
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
		
		String bucket_name = "riskreplay";
		//Bucket b = null;
		
		if(s3.doesBucketExistV2(bucket_name)) {
			System.out.println("Bucket already exists.");
			rrBucket = getBucket(bucket_name);
			
		} else {
			try {
				rrBucket = s3.createBucket(bucket_name);
				System.out.println("New bucket created");
			} catch(AmazonS3Exception e) {
				System.err.println(e.getErrorMessage());
			}
		}
		gameKey = createGame();
		
	}
	public static Bucket getBucket(String bucket_name) {
		// https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/java/example_code/s3/src/main/java/aws/example/s3/CreateBucket.java
        Bucket named_bucket = null;
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
        	System.out.println(b.getName());
            if (b.getName().equals(bucket_name)) {
                named_bucket = b;
                System.out.println("Found bucket");
            }
        }
        return named_bucket;
        
	}
	public String createGame() {
		LocalDateTime dt = LocalDateTime.now();
		String objectKey = "G" + dt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		System.out.println("Key created: " + objectKey);
		s3.putObject(rrBucket.getName(), objectKey, "Game Object created.\n");
		return objectKey;
	}
	public String s3ObjectConverter(S3Object obj) throws IOException {
		String fullObj = "";
		InputStream input = obj.getObjectContent();
		// Input text stream into buffer and concat a string.
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
            fullObj += line + "\n";
        }
        //System.out.println();
		return fullObj;
	}
	public void logPlayerActivity() {
		pa.print();
		String str = null;
		S3Object fullObj = s3.getObject(new GetObjectRequest(rrBucket.getName(), gameKey));
		try {
			str = s3ObjectConverter(fullObj);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		if(str != null) {
			str += pa.getActivityOutput() + "\n";
		} else {
			str = pa.getActivityOutput() + "\n";
		}
		// send object to AWS bucket
		s3.putObject(rrBucket.getName(), gameKey, str);
		System.out.println("updated AWS bucket:\n" + str);
		// fetch game object, append activity, overwrite object
	}
}
