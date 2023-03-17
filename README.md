---
services: Batch
platforms: java
---
## References
Java Doc Support link: https://learn.microsoft.com/en-us/java/api/com.microsoft.azure.batch.protocol.models.cloudjob?view=azure-java-stable
Microsoft Documentation: https://learn.microsoft.com/en-us/azure/batch/batch-technical-overview
https://learn.microsoft.com/en-us/answers/questions/106667/azure-batch-execute-java-processes
Stakoverflow updates: https://stackoverflow.com/questions/tagged/azure-batch
Get Storage Directory SAS token: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/storage/azure-storage-file-datalake/src/samples/java/com/azure/storage/file/datalake/FileSystemClientJavaDocCodeSamples.java

## Description
When run, this sample will:

- Create an Azure Batch pool with a single dedicated node
- Wait for the nodes to be ready
- Create a storage container and upload a resource file to it
- Submit a job with 5 tasks associated with the resource file
- Wait for all tasks to finish
- Delete the job, the pool and the storage container

## Running this Sample
To run this sample:

Set the following environment variables:
- `AZURE_BATCH_ACCOUNT` -- The Batch account name.
- `AZURE_BATCH_ACCESS_KEY` -- The Batch account key.
- `AZURE_BATCH_ENDPOINT` -- The Batch account endpoint.
- `STORAGE_ACCOUNT_NAME` -- The storage account to hold resource files.
- `STORAGE_ACCOUNT_KEY` -- The storage account key.

Install JRE on Pool Node Startup
- CentOs: sudo yum -y install java-1.8.0-openjdk
- Ubuntu: /bin/bash -c "sudo apt-get update&&sudo apt-get install -y openjdk-8-jdk"

Resource gets created from startup resource
- CentOS "/mnt/resource/batch/tasks/startup/wd"
- Ubuntu "/mnt/batch/tasks/startup/wd/"  
- command to run from ADF /bin/bash -c "sudo java -cp /mnt/batch/tasks/startup/wd/AzureBatchService-1.0.0-jar-with-dependencies.jar AzureBatchService "/mnt/batch/tasks/startup/wd/batch_service_config_01.xml""

Set Command to run Executable JAR while creating a Tak
-  .withCommandLine("java -cp boots-voltage-fle-utility-0.0.1-jar-with-dependencies.jar com.boots.voltage.VoltageMainApplication \"voltage_service_config_01.xml\" both")

Clone repo and compile the code:

    git clone https://github.com/Azure/azure-batch-samples.git

    cd azure-batch-samples/Java/PoolAndResourceFile

    mvn clean compile exec:java
