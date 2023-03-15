package azbatch.utils;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URISyntaxException;
import java.util.Map;

public class StorageUtil {

    private static final Logger logger = LogManager.getLogger(StorageUtil.class);

    /**
     * Create blob container in order to upload file
     * 
     * @param storageAccountName The name of the storage account to create or lookup
     * @param storageAccountKey  An SAS key for accessing the storage account
     * @return A newly created or existing storage container
     */

    public static CloudBlobContainer createBlobContainerIfNotExists(Map<String, String> map) {

        CloudBlobContainer container = null;
        String STORAGE_ACCOUNT_NAME = map.get("STORAGE_ACCOUNT_NAME");
        String STORAGE_ACCOUNT_KEY = map.get("STORAGE_ACCOUNT_KEY");
        String STORAGE_CONTAINER_NAME = map.get("STORAGE_CONTAINER_NAME");
        logger.info("Creating storage container " + STORAGE_CONTAINER_NAME);
        
        try {
            StorageCredentials credentials = new StorageCredentialsAccountAndKey(STORAGE_ACCOUNT_NAME,
                    STORAGE_ACCOUNT_KEY);
            CloudBlobClient blobClient = new CloudStorageAccount(credentials, true).createCloudBlobClient();
            container = blobClient.getContainerReference(STORAGE_CONTAINER_NAME);
            container.createIfNotExists();            

        } catch (URISyntaxException uriexp) {
            logger.info("Failed [creating Container]: " + uriexp.getMessage());
            uriexp.printStackTrace();

        } catch (StorageException strexp) {
            logger.info("Failed [creating Container]: " + strexp.getMessage());
            strexp.printStackTrace();

        } catch (Exception exp) {
            logger.info("Failed [creating Container]: " + exp.getMessage());
            exp.printStackTrace();

        }
        return container;
    }

}
