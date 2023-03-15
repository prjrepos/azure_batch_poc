package azbatch.utils;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
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

    public static CloudBlobContainer createBlobContainerIfNotExists(Map<String, String> configMap) {

        CloudBlobContainer container = null;
        String STORAGE_ACCOUNT_NAME = configMap.get("STORAGE_ACCOUNT_NAME");
        String STORAGE_ACCOUNT_KEY = configMap.get("STORAGE_ACCOUNT_KEY");
        String STORAGE_CONTAINER_NAME = configMap.get("STORAGE_CONTAINER_NAME");
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

    /**
     * Get SAS key of the application files to be downloaded
     *
     * @param container The container from download
     * @param dir    The remote directory to download
     * @param source    The remote file to download
     *
     * @return A SAS key for the file
     */
    public static String getAppStorageUri(CloudBlobContainer container, String dir, String file, Map<String, String> configMap)
            throws URISyntaxException, IOException, InvalidKeyException, StorageException {

        CloudBlobDirectory blobDir = null;
        blobDir = container.getDirectoryReference(configMap.get("APP_METADATA_DIR") + "/" + dir);            
            
        CloudBlockBlob blob = blobDir.getBlockBlobReference(file);      
        // Set SAS expiry time to 1 day from now
        SharedAccessBlobPolicy policy = new SharedAccessBlobPolicy();
        EnumSet<SharedAccessBlobPermissions> perEnumSet = EnumSet.of(SharedAccessBlobPermissions.READ);
        policy.setPermissions(perEnumSet);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        policy.setSharedAccessExpiryTime(cal.getTime());
        // Create SAS key
        String sas = blob.generateSharedAccessSignature(policy, null);       
        return blob.getUri() + "?" + sas;
    }

    /**
     * Get SAS key of all files to be downloaded from a Blob Directory
     *
     * @param container The container from download
     * @param dir    The remote directory to download
     *
     * @return A SAS key for the file
     */
    public static Map<String, String> getAppStorageUri(CloudBlobContainer container, String dir, Map<String, String> configMap)
            throws URISyntaxException, IOException, InvalidKeyException, StorageException {

        Map<String, String> map = new HashMap<String, String>();       
        CloudBlobDirectory blobDir = null;        
        blobDir = container.getDirectoryReference(configMap.get("APP_METADATA_DIR") + "/" + dir);
               
        Iterable<ListBlobItem> blobs = blobDir.listBlobs();        
        for (ListBlobItem blob : blobs) {
            if (blob instanceof CloudBlobDirectory) {
                continue;                
            }
            String path = blob.getUri().getPath();
            String file = path.substring(path.lastIndexOf("/") + 1);          
            CloudBlockBlob blockBlob = blobDir.getBlockBlobReference(file);
            // Set SAS expiry time to 1 day from now
            SharedAccessBlobPolicy policy = new SharedAccessBlobPolicy();
            EnumSet<SharedAccessBlobPermissions> perEnumSet = EnumSet.of(SharedAccessBlobPermissions.READ);
            policy.setPermissions(perEnumSet);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, 1);
            policy.setSharedAccessExpiryTime(cal.getTime());
            // Create SAS key
            String sas = blockBlob.generateSharedAccessSignature(policy, null);
            map.put(file, blob.getUri() + "?" + sas);
        }         
        return map;
    } 

}
