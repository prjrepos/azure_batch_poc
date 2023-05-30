import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;
import azbatch.utils.AzBatchUtilities;
import azbatch.utils.BatchConfigUtil;
import azbatch.utils.StorageUtil;
import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.batch.*;
import com.microsoft.azure.batch.auth.*;
import com.microsoft.azure.batch.protocol.models.*;
import com.microsoft.azure.batch.protocol.models.CloudPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AzureBatchService {

    private static final Logger logger = LogManager.getLogger(AzureBatchService.class);  
    static Map<String, String> ConfigMap = new HashMap<String, String>();
    

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

        ConfigMap = BatchConfigUtil.readConfigXML(argv[0]);
        String operation = argv[1];
        
        // Get Batch and storage account information from environment        
        String BATCH_ACCOUNT = ConfigMap.get("BATCH_ACCOUNT");
        String BATCH_ACCESS_KEY = ConfigMap.get("BATCH_ACCESS_KEY");
        String BATCH_URI = ConfigMap.get("BATCH_URI");
        BatchClient client = BatchClient
                .open(new BatchSharedKeyCredentials(BATCH_URI, BATCH_ACCOUNT, BATCH_ACCESS_KEY));

        CloudBlobContainer container = StorageUtil.createBlobContainerIfNotExists(ConfigMap);
       
        String svcName = ConfigMap.get("SERVICE_NAME");       
        String poolId = ConfigMap.get("POOL_ID");
        String jobId = "azbatchjob-" + svcName + "-" +
                new Date().toString().replaceAll("(\\.|:|\\s)", "-");
        try {
            CloudPool sharedPool = null;
            if(Boolean.parseBoolean(ConfigMap.get("CREATE_POOL")))
                sharedPool = AzBatchUtilities.createPoolIfNotExists(client, ConfigMap, container);
            else{
                logger.info("Using Existing Pool: " +poolId);
                sharedPool = client.poolOperations().getPool(poolId);
            }                
            // Submit a job and wait for completion
            AzBatchUtilities.submitJob(client, container, sharedPool.id(), jobId, ConfigMap, operation);
            AzBatchUtilities.waitForTasksToComplete(client, jobId, Duration.ofMinutes(5));
            logger.info("Task Results");
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
            if (Boolean.parseBoolean(ConfigMap.get("CLEANUP_JOB"))) {
                try {
                    logger.info("Deleting job " + jobId);
                    client.jobOperations().deleteJob(jobId);
                } catch (BatchErrorException err) {
                    printBatchException(err);
                }
            }
            if (Boolean.parseBoolean(ConfigMap.get("CLEANUP_POOL"))) {
                try {
                    logger.info("Deleting pool " + poolId);
                    client.poolOperations().deletePool(poolId);
                } catch (BatchErrorException err) {
                    printBatchException(err);
                }
            }
            if (Boolean.parseBoolean(ConfigMap.get("CLEANUP_STORAGE_CONTAINER"))) {
                logger.info("Deleting storage container " + container.getName());
                container.deleteIfExists();
            }
        }

        logger.info("\nFinished");
        System.exit(0);
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
