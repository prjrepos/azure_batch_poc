package azbatch.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BatchConfigUtil {

    private static final Logger logger = LogManager.getLogger(BatchConfigUtil.class);

    /**
     * Method to parse configuration xml file
     * 
     * We can keep in a separate utility class to read this XML by returning the
     * result as POJO
     * 
     * @param xmlFilePath
     * @throws Exception
     */
    public static Map<String, String> readConfigXML(String xmlFilePath) {

        Map<String, String> map = new HashMap<String, String>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();           
            File fXmlFile = new File(xmlFilePath);
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList confList = doc.getElementsByTagName("Config");
            Node cNode = confList.item(0);
            Element cElem = (Element) cNode;
            map.put("STORAGE_ACCOUNT_NAME", cElem.getElementsByTagName("StorageAccountName").item(0).getTextContent());
            map.put("STORAGE_ACCOUNT_KEY", cElem.getElementsByTagName("StorageAccountKey").item(0).getTextContent());
            map.put("STORAGE_CONTAINER_NAME", cElem.getElementsByTagName("StorageContainerName").item(0).getTextContent());
            map.put("BATCH_ACCOUNT", cElem.getElementsByTagName("BatchAccountName").item(0).getTextContent());
            map.put("BATCH_ACCESS_KEY", cElem.getElementsByTagName("BatchAccessKey").item(0).getTextContent());  
            map.put("BATCH_URI", cElem.getElementsByTagName("BatchUri").item(0).getTextContent());
            map.put("APP_METADATA_DIR", cElem.getElementsByTagName("AppMetdataDirectory").item(0).getTextContent());
            map.put("APP_LOG_DIR", cElem.getElementsByTagName("AppLogDirectory").item(0).getTextContent());
            map.put("POOL_ID", cElem.getElementsByTagName("PoolId").item(0).getTextContent());
            map.put("TASK_COUNT", cElem.getElementsByTagName("TaskCount").item(0).getTextContent());
            map.put("NODE_COUNT", cElem.getElementsByTagName("NodeCount").item(0).getTextContent());
            map.put("OS_PUBLISHER", cElem.getElementsByTagName("OSPublisher").item(0).getTextContent()); 
            map.put("OS_OFFER", cElem.getElementsByTagName("OSOffer").item(0).getTextContent()); 
            map.put("POOL_VM_SIZE", cElem.getElementsByTagName("PoolVmSize").item(0).getTextContent()); 
            map.put("POOL_VM_COUNT", cElem.getElementsByTagName("PoolVmCount").item(0).getTextContent());
            map.put("TARGET_DEDICATED_NODE", cElem.getElementsByTagName("TargetDedicatedNode").item(0).getTextContent());
            map.put("TARGET_LOW_PRIORITY_NODE", cElem.getElementsByTagName("TargetLowPriorityNode").item(0).getTextContent());
            map.put("CLEANUP_STORAGE_CONTAINER", cElem.getElementsByTagName("CleanUpStorage").item(0).getTextContent());
            map.put("CLEANUP_JOB", cElem.getElementsByTagName("CleanUpJob").item(0).getTextContent());
            map.put("CLEANUP_POOL", cElem.getElementsByTagName("CleanUpPool").item(0).getTextContent());
            map.put("SERVICE_NAME", cElem.getElementsByTagName("ServiceName").item(0).getTextContent());
            map.put("VOLTAGE_OPERATION", cElem.getElementsByTagName("Operation").item(0).getTextContent());
            map.put("CREATE_POOL", cElem.getElementsByTagName("CreatePool").item(0).getTextContent());            

            logger.info("************** Batch Configurations  *************************");
            logger.info("Storage Account Name 	    : " + map.get("STORAGE_ACCOUNT_NAME"));
            logger.info("Storge Container Name 	    : " + map.get("STORAGE_CONTAINER_NAME"));
            logger.info("Batch Account Name 	    : " + map.get("BATCH_ACCOUNT"));
            logger.info("Batch URI 	                : " + map.get("BATCH_URI"));
            logger.info("Application Metdata Dir    : " + map.get("APP_METADATA_DIR"));
            logger.info("Application Log Dir        : " + map.get("APP_LOG_DIR"));
            logger.info("Pool Id                    : " + map.get("POOL_ID"));
            logger.info("Task Count                 : " + map.get("TASK_COUNT"));
            logger.info("Node Count                 : " + map.get("NODE_COUNT"));
            logger.info("OS Publisher               : " + map.get("OS_PUBLISHER"));
            logger.info("OS Offer                   : " + map.get("OS_OFFER"));
            logger.info("VM Size                    : " + map.get("POOL_VM_SIZE"));
            logger.info("Node Count                 : " + map.get("POOL_VM_COUNT"));
            logger.info("Target Dedicated Node      : " + map.get("TARGET_DEDICATED_NODE"));
            logger.info("Target Spot Node           : " + map.get("TARGET_LOW_PRIORITY_NODE"));
            logger.info("Cleanup Storage Flag       : " + map.get("CLEANUP_STORAGE_CONTAINER"));
            logger.info("Cleanup Job Flag           : " + map.get("CLEANUP_JOB"));
            logger.info("Cleanup Pool Flag          : " + map.get("CLEANUP_POOL"));
            logger.info("Service Name               : " + map.get("SERVICE_NAME"));
            logger.info("Voltage Operation          : " + map.get("VOLTAGE_OPERATION"));
            logger.info("Create New Pool            : " + map.get("CREATE_POOL"));  
            logger.info("**************************************************************");       

        } catch (IOException ioe) {
            logger.info("Failed [readConfigXML]: " + ioe.getMessage());
            ioe.printStackTrace();
        } catch (Exception e) {
            logger.info("Failed [readConfigXML]: " + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

}
