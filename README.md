---
services: Batch
platforms: java
---
## References
Java Doc Support link: https://learn.microsoft.com/en-us/java/api/com.microsoft.azure.batch.protocol.models.cloudjob?view=azure-java-stable
Microsoft Documentation: https://learn.microsoft.com/en-us/azure/batch/batch-technical-overview
https://learn.microsoft.com/en-us/answers/questions/106667/azure-batch-execute-java-processes
Stakoverflow updates: https://stackoverflow.com/questions/tagged/azure-batch

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
- sudo yum -y install java-1.8.0-openjdk

Set Command to run Executable JAR while creating a Tak
-  .withCommandLine("java -cp batchdemo-1.0-jar-with-dependencies.jar com.sample.testapp.App \"config.xml\"")

Clone repo and compile the code:

    git clone https://github.com/Azure/azure-batch-samples.git

    cd azure-batch-samples/Java/PoolAndResourceFile

    mvn clean compile exec:java
