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

            logger.info("************** Batch Configurations  *************************");
            logger.info("Storage Account Name 	:" + map.get("STORAGE_ACCOUNT_NAME"));
            logger.info("Storge Container Name 	:" + map.get("STORAGE_CONTAINER_NAME"));          
            logger.info("*******************************************************************");       

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
