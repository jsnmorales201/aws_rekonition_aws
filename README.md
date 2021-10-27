# aws_rekognition_car_images
Java application that will recognize any car images using AWS Rekognition service and any text associated with the images

The application is implemented on 2 AWS EC2 instances. 

1 EC2 instance will forward the images recognized as a car to an AWS SQS Queue. 

The other instance will pull the messages from AWS SQS and recognize any text in the images identified as cars.

