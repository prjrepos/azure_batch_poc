import java.io.*;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import azbatch.constants.AzBatchConfig;
import azbatch.utils.AzBatchUtilities;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.batch.*;
import com.microsoft.azure.batch.auth.*;
import com.microsoft.azure.batch.protocol.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AzureBatchService {

    private static final Logger logger = LogManager.getLogger(AzureBatchService.class);
    // Get Batch and storage account information from environment
    static String BATCH_ACCOUNT = AzBatchConfig.BATCH_ACCOUNT;
    static String BATCH_ACCESS_KEY = AzBatchConfig.BATCH_ACCESS_KEY;
    static String BATCH_URI = AzBatchConfig.BATCH_URI;
    static String STORAGE_ACCOUNT_NAME = AzBatchConfig.STORAGE_ACCOUNT_NAME;
    static String STORAGE_ACCOUNT_KEY = AzBatchConfig.STORAGE_ACCOUNT_KEY;
    static String STORAGE_CONTAINER_NAME = AzBatchConfig.STORAGE_CONTAINER_NAME;
    // How many tasks to run across how many nodes
    // static int TASK_COUNT = AzBatchConfig.TASK_COUNT;
    // Modify these values to change which resources are deleted after the job
    // finishes.
    static boolean CLEANUP_STORAGE_CONTAINER = false;
    static boolean CLEANUP_JOB = true;
    // warning: Skipping pool deletion will greatly speed up subsequent runs
    static boolean CLEANUP_POOL = false;

    /**
     * Method to read create Pool, assign node into Pool, create Job, create task
     * based on parameters
     * 
     * @argv1 Service Name e.g. which service is invoked in Batch like
     *        voltage-encryption or voltage-decryption
     * @argv2 Executable Jar name
     * @argv3 Configuration File name
     * @return
     * @throws Exception
     */

    public static void main(String[] argv) throws Exception {

        BatchClient client = BatchClient
                .open(new BatchSharedKeyCredentials(BATCH_URI, BATCH_ACCOUNT, BATCH_ACCESS_KEY));
        CloudBlobContainer container = createBlobContainerIfNotExists(STORAGE_ACCOUNT_NAME, STORAGE_ACCOUNT_KEY,
                STORAGE_CONTAINER_NAME);
        String svcName = argv[0];
        String poolId = AzBatchConfig.POOL_ID;
        String jobId = "AzBatchJob-" + svcName + "-" +
                new Date().toString().replaceAll("(\\.|:|\\s)", "-");
        try {
            CloudPool sharedPool = AzBatchUtilities.createPoolIfNotExists(client);
            // Submit a job and wait for completion
            AzBatchUtilities.submitJob(client, container, sharedPool.id(), jobId);
            AzBatchUtilities.waitForTasksToComplete(client, jobId, Duration.ofMinutes(5));
            logger.info("\nTask Results");
            logger.info("------------------------------------------------------");
            List<CloudTask> tasks = client.taskOperations().listTasks(jobId);
            for (CloudTask task : tasks) {
                if (task.executionInfo().failureInfo() != null) {
                    logger.info("Task " + task.id() + " failed: " + task.executionInfo().failureInfo().message());
                }

                String outputFileName = task.executionInfo().exitCode() == 0 ? "stdout.txt" : "stderr.txt";
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                client.fileOperations().getFileFromTask(jobId, task.id(), outputFileName, stream);
                String fileContent = stream.toString("UTF-8");
                logger.info("\nTask " + task.id() + " output (" + outputFileName + "):");
                logger.info(fileContent);
            }

            logger.info("------------------------------------------------------\n");
        } catch (BatchErrorException err) {
            printBatchException(err);
        } catch (IllegalArgumentException err) {
            err.printStackTrace();
        } catch (IOException err) {
            err.printStackTrace();
        } catch (InterruptedException err) {
            err.printStackTrace();
        } catch (TimeoutException err) {
            err.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // Clean up resources
            if (CLEANUP_JOB) {
                try {
                    logger.info("Deleting job " + jobId);
                    client.jobOperations().deleteJob(jobId);
                } catch (BatchErrorException err) {
                    printBatchException(err);
                }
            }
            if (CLEANUP_POOL) {
                try {
                    logger.info("Deleting pool " + poolId);
                    client.poolOperations().deletePool(poolId);
                } catch (BatchErrorException err) {
                    printBatchException(err);
                }
            }
            if (CLEANUP_STORAGE_CONTAINER) {
                logger.info("Deleting storage container " + container.getName());
                container.deleteIfExists();
            }
        }

        logger.info("\nFinished");
        System.exit(0);
    }

    /**
     * Create blob container in order to upload file
     * 
     * @param storageAccountName The name of the storage account to create or lookup
     * @param storageAccountKey  An SAS key for accessing the storage account
     * @return A newly created or existing storage container
     */

    private static CloudBlobContainer createBlobContainerIfNotExists(String storageAccountName,
            String storageAccountKey, String containerName)
            throws URISyntaxException, StorageException {
        logger.info("Creating storage container " + containerName);
        StorageCredentials credentials = new StorageCredentialsAccountAndKey(storageAccountName, storageAccountKey);
        CloudBlobClient blobClient = new CloudStorageAccount(credentials, true).createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(containerName);
        container.createIfNotExists();
        return container;
    }
    
    private static void printBatchException(BatchErrorException err) {
        System.out.printf("BatchError %s%n", err.toString());
        if (err.body() != null) {
            System.out.printf("BatchError code = %s, message = %s%n", err.body().code(),
                    err.body().message().value());
            if (err.body().values() != null) {
                for (BatchErrorDetail detail : err.body().values()) {
                    System.out.printf("Detail %s=%s%n", detail.key(), detail.value());
                }
            }
        }
    }

}
