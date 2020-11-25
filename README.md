# aws_rekonition_aws
Java application which will recognize any car images using AWS Rekognition service and any text associated with the images

AWS EC2 instance will recognized any car images from an S3 bucket and will forward them to SQS that will be further processed by the text recognition EC2 instance.
