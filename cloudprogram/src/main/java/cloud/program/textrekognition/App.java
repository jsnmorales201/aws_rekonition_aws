package cloud.program.textrekognition;

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

import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;


import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.TextDetection;

import java.util.*;
import java.io.*;


public class App 
{
    public static void main( String[] args ) throws FileNotFoundException
    {
    
                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
              

               List<String> messagelist = new ArrayList<>();
		
		String bucket_name = "njit-cs-643";
              
 
		boolean flag = true;

		PrintStream o = new PrintStream(new File("/home/ec2-user/cloudprogram/output.txt"));
		PrintStream console = System.out;
		System.setOut(o);
		
     outerloop:
     while(true){				
     		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest("https://sqs.us-east-1.amazonaws.com/031967107468/MoralesJasonCloudImages.fifo")
  						.withWaitTimeSeconds(20)
  						.withMaxNumberOfMessages(10);

     		List<Message> sqsMessages = sqs.receiveMessage(receiveMessageRequest).getMessages();

     		for(int i = 0; i < sqsMessages.size(); i++){
			if(sqsMessages.get(i).getBody().equalsIgnoreCase("-1")){
                              sqs.deleteMessage(new DeleteMessageRequest()
                            .withQueueUrl("https://sqs.us-east-1.amazonaws.com/031967107468/MoralesJasonCloudImages.fifo")
                            .withReceiptHandle(sqsMessages.get(i).getReceiptHandle()));
                              break outerloop;
                         }

     			messagelist.add(sqsMessages.get(i).getBody());
                        sqs.deleteMessage(new DeleteMessageRequest()
                            .withQueueUrl("https://sqs.us-east-1.amazonaws.com/031967107468/MoralesJasonCloudImages.fifo")
                            .withReceiptHandle(sqsMessages.get(i).getReceiptHandle()));      

     		}
      }                            
                List<String> textArray = new ArrayList<>();

		for(int i = 0; i < messagelist.size(); i++){
			
			textArray = generateArrayList(bucket_name, messagelist.get(i));
			
                       if(textArray != null && !textArray.isEmpty()){

           		 System.out.println(messagelist.get(i) + " is a Car " + " and the detected text is " + Arrays.toString(textArray.toArray()));
            
           		 }
		}
	 }
	
	public static List<String> generateArrayList(String bucket, String photo){
    
     		 List<String> stringArray = new ArrayList<>();
     		 AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

                DetectTextRequest request = new DetectTextRequest()
             		 .withImage(new Image()
             		 .withS3Object(new S3Object()
             		 .withName(photo)
             		 .withBucket(bucket)));
     		 try {
     		    DetectTextResult result = rekognitionClient.detectText(request);
     		    List<TextDetection> textDetections = result.getTextDetections();

        	 for (TextDetection text: textDetections) {
      
                	  stringArray.add(text.getDetectedText());
                 
       		  }
     		 } catch(AmazonRekognitionException e) {
        		 e.printStackTrace();
     			 }
        return stringArray;
	  }

}
