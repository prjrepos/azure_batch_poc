package utils;


import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;

import org.apache.commons.io.FileUtils;

import java.io.*;

public class StorageService {

    /**
     * Create blob container in order to upload file
     * 
     * @param storageAccountName The name of the storage account to create or look
     *                           up
     * @param storageAccountKey  An SAS key for accessing the storage account
     *
     * @return A newly created or existing storage container
     */
    public CloudBlobContainer createBlobContainerIfNotExists(String storageAccountName,
            String storageAccountKey, String containerName)
            throws URISyntaxException, StorageException {
        System.out.println("Creating storage container " + containerName);

        StorageCredentials credentials = new StorageCredentialsAccountAndKey(storageAccountName, storageAccountKey);
        CloudBlobClient blobClient = new CloudStorageAccount(credentials, true).createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(containerName);
        container.createIfNotExists();

        return container;
    }

    /**
     * Upload a file to a blob container and return an SAS key
     *
     * @param container The container to upload to
     * @param source    The local file to upload
     *
     * @return An SAS key for the uploaded file
     */
    public String uploadFileToCloud(CloudBlobContainer container, File source)
            throws URISyntaxException, IOException, InvalidKeyException, StorageException {
        CloudBlockBlob blob = container.getBlockBlobReference(source.getName());
        blob.upload(new FileInputStream(source), source.length());

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
     * download files from a blob container and return an SAS key
     *
     * @param container The source container
     * @param source    The local file to download
     *
     * @return An SAS key for the uploaded file
     */
    public void downloadFileFromCloud(CloudBlobContainer container)
            throws URISyntaxException, IOException, InvalidKeyException, StorageException {
       
        CloudBlockBlob blob = container.getDirectoryReference("trustScores").getBlockBlobReference(".");

        CloudBlobDirectory blobDir = container.getDirectoryReference("trustScores");
        Iterable<ListBlobItem> blobs = blobDir.listBlobs();      
        
        for (ListBlobItem blobItem : blobs) {
            String path = blobItem.getUri().getPath();      
            String filename = path.substring(path.lastIndexOf("/") + 1);
            String filePath = "voltageWorkingDir/trustScores/" + filename;
            FileUtils.forceMkdirParent(new File(filePath));
            File targetFile = new File("voltageWorkingDir/trustScores",filename);
            FileOutputStream outputStream = new FileOutputStream(targetFile);          
            blob.download(outputStream);
        }

    }

}
