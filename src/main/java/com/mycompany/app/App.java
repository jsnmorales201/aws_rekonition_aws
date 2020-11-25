package com.mycompany.app;

/**
 * Hello world!
 *
 */
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.io.*; 
import java.util.*;



/**
 * List objects within an Amazon S3 bucket.
 * 
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */

public class App {
    public static void main(String[] args) {

        

	Vector<String> photos = new Vector<>();
 
        String bucket_name = "njit-cs-643";
        
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        System.out.format("Objects in S3 bucket %s:\n", bucket_name);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build();
        ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        
	for (int i = 0; i < objects.size() ; i++) {
             photos.add(objects.get(i).getKey());
        }
       
  
        boolean isCar;

        for (int i = 0; i < photos.size(); i++) { 
                 
              isCar = return_images_car(bucket_name, photos.get(i));

              if(isCar){
			
			SendMessageRequest send_msg_request = new SendMessageRequest()
        								.withQueueUrl("https://sqs.us-east-1.amazonaws.com/031967107468/MoralesJasonCloudImages.fifo")
        								.withMessageBody(photos.get(i))
			                                                .withMessageGroupId(bucket_name);
			sqs.sendMessage(send_msg_request);

              }

            
         } 
	
	SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl("https://sqs.us-east-1.amazonaws.com/031967107468/MoralesJasonCloudImages.fifo")
                        .withMessageBody("-1")
                        .withMessageGroupId(bucket_name);
      sqs.sendMessage(send_msg_request);

    }


    public static boolean return_images_car(String bucket, String photo){
                
                  //      boolean Car_flag = false;
                        
                        boolean Car_flag = false;

                        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

         		DetectLabelsRequest request = new DetectLabelsRequest()
           		.withImage(new Image()
           		.withS3Object(new S3Object()
           		.withName(photo).withBucket(bucket)))
           		.withMaxLabels(10)
           		.withMinConfidence(90F);


            		
                       try {
        			 DetectLabelsResult result = rekognitionClient.detectLabels(request);
         			 List <Label> labels = result.getLabels();

         			 for (Label label: labels) {
                                 
                                 	if(label.getName().equalsIgnoreCase("Car")){ 
                                            
                                                Car_flag = true;
                                                 	
        				}

         			 }
      			} catch(AmazonRekognitionException e) {
         					e.printStackTrace();
      	
                        }

         return Car_flag;    
        } 		
                
         
}


