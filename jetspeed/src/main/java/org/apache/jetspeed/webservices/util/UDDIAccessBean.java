/*
 * UDDIAccessBean.java
 *
 * Created on October 23, 2001, 7:39 PM
 */

package org.apache.jetspeed.webservices.util;
// import javabeans packages
import java.io.Serializable;
// import uddi packages
import org.uddi4j.UDDIException;
import org.uddi4j.transport.TransportException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.response.*;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * UDDIAccessBean provides a command bean interface
 * to UDDI registry services.
 * Please note that  currently only inquiry tasks are supported. I
 * hope to add other task types soon.
 * @author  Scott A. Roehrig@IBM Corporation
 * @version 1.0
 */
public class UDDIAccessBean extends Object implements Serializable 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(UDDIAccessBean.class.getName());
    
    // declare member variables
    private transient UDDIProxy proxy;
    private String queryURL;
   
    /** setter method for queryURL */
    public void setQueryURL(String queryURL) {
        this.queryURL = queryURL;
    }
    
    /** getter method for queryURL */
    public String getQueryURL() {
        return queryURL;
    }
    
    /** creates new UDDIAccessBean */
    public UDDIAccessBean() {
        proxy = new UDDIProxy();
        queryURL = "http://www-3.ibm.com/services/uddi/testregistry/inquiryapi";
    }
    
    /** creates new UDDIAccessBean with provided queryURL */
    public UDDIAccessBean(String queryURL) {
        proxy = new UDDIProxy();
        this.queryURL = queryURL;
    }
    
    /** performs business lookup against registry */
    public java.util.List queryBusiness(String name) {
        // create list to store results
        java.util.List businessList = new java.util.Vector();
        // perform query
        try {
            proxy.setInquiryURL(queryURL);
            BusinessList results = proxy.find_business(name, null, 0);
            businessList = results.getBusinessInfos().getBusinessInfoVector();
            if (logger.isDebugEnabled()) {
                java.util.ListIterator iterator = businessList.listIterator();
                while (iterator.hasNext() == true) {
                    org.uddi4j.response.BusinessInfo business = (org.uddi4j.response.BusinessInfo)iterator.next();
                    logger.debug(business.getDefaultDescriptionString());
                }
            }
        }
        catch (java.net.MalformedURLException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
        }
        catch (UDDIException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
        }
        catch (TransportException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
        }
        // return results
        return businessList;
    }
    
    /** returns businessKey object associated with provided business */
   public String getBusinessKey(String businessName) {
       String businessKey = null;
       // create List to store results
       java.util.List businessList = new java.util.Vector();
       // perform query
       try {
            proxy.setInquiryURL(queryURL);
            BusinessList results = proxy.find_business(businessName, null, 0);
            businessList = results.getBusinessInfos().getBusinessInfoVector();
            // create iterator to search for match
            java.util.ListIterator iterator = businessList.listIterator();
            while (iterator.hasNext() == true) {
                BusinessInfo business  = (BusinessInfo)iterator.next();
                if (business.getNameString().equals(businessName)) {
                    businessKey = business.getBusinessKey();
                    if (logger.isDebugEnabled()) {
                        logger.debug(business.getNameString()+" has associated key "+businessKey);
                    }
                }
            }
       }
       catch (java.net.MalformedURLException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
       }
       catch (UDDIException exception) {
            exception.printStackTrace();
           if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            } 
       }
        catch (TransportException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
        }
       // return result
       return businessKey;
   }
   
    /** performs service lookup against registry */
    public java.util.List queryService(String businessKey, String description) {
        // create List to store results
        java.util.List serviceList = new java.util.Vector();
        // perform query
        try {
            proxy.setInquiryURL(queryURL);
            ServiceList results = proxy.find_service(businessKey, description, null, 0);
            serviceList = results.getServiceInfos().getServiceInfoVector();
            if (logger.isDebugEnabled()) {
                java.util.ListIterator iterator = serviceList.listIterator();
                while (iterator.hasNext() == true) {
                    org.uddi4j.response.ServiceInfo service = (org.uddi4j.response.ServiceInfo)iterator.next();
                    logger.debug(service.getNameString());
                }
            }
        }
        catch (java.net.MalformedURLException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
        }
        catch (UDDIException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
        }
        catch (TransportException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
        }
        // return results
        return serviceList;
    }
    
    /** returns serviceKey associated with provided service */
    public String getServiceKey(String businessKey, String serviceName) {
        String serviceKey = null;
       // create List to store results
       java.util.List serviceList = new java.util.Vector();
       // perform query
       try {
            proxy.setInquiryURL(queryURL);
            ServiceList results = proxy.find_service(businessKey, serviceName, null, 0);
            serviceList = results.getServiceInfos().getServiceInfoVector();
            // create iterator to search for match
            java.util.ListIterator iterator = serviceList.listIterator();
            while (iterator.hasNext() == true) {
                ServiceInfo service  = (ServiceInfo)iterator.next();
                if (service.getNameString().equals(serviceName)) {
                    serviceKey = service.getServiceKey();
                    if (logger.isDebugEnabled()) {
                        logger.debug(service.getNameString()+" has associated key "+serviceKey);
                    }
                }
            }
       }
       catch (java.net.MalformedURLException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
       }
       catch (UDDIException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
       }
        catch (TransportException exception) {
            exception.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
        }
       // return result
       return serviceKey;
    }
    
    /** called before object is garbage collected */
    public void finalize() {
        proxy = null;
        queryURL = null;
    }
    
}
