/*
 * SoapAccessBean.java
 *
 * Created on August 6, 2001, 3:59 PM
 */
package org.apache.jetspeed.webservices.util;
// import soap packages
import org.apache.soap.SOAPException;
import org.apache.soap.Constants;
import org.apache.soap.rpc.Call;
import org.apache.soap.rpc.Parameter;
import org.apache.soap.rpc.Response;
// import utility classes
import java.util.Vector;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/** SoapAccessBean provides a Soap WebServices Client command bean pattern.
  *
  * This bean can be used to interact with webservices via soap. The WSDL
  * file for the target web service is required since it contains the
  * parameters required to interact with the service.
  *
  * @author Scott A. Roehrig
  * @version 1.0
  */
public class SoapAccessBean implements java.io.Serializable 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(SoapAccessBean.class.getName());
    
    // declare member variables
    private transient Call request;
    private Vector parameters; 
        
    // declare methods
    /** Default constructor required to support Serialization. */
    public SoapAccessBean() {
        request = new Call();
    }

    /** Creates new SoapAccessBean with parameters specified.
    *
    * @param targetURI type: java.lang.String - 
    * desc: targetURI for the services
    *
    * @param method type: java.lang.String - 
    * desc: service method
    */
    public SoapAccessBean(String targetURI, String method) {
        // create call object
        request = new Call();
        // set targetObjectURI, method, and encoding style
        request.setTargetObjectURI(targetURI);
        request.setMethodName(method);
        request.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
        // create Vector to store service parameters
        parameters = new java.util.Vector();
            
    }
    
    /** Setter method for parameters property.
    *
    * The parameters property is used to set
    * the various parameters required to interact
    * with the service.
    *
    * @param parameters
    * type: java.util.Vector - 
    * desc: stores service parameters
    */
    public void setParameters(Vector parameters) {
        this.parameters = parameters;
        
    }
    
    /** Parameters property getter method.
    *
    * @return java.util.Vector - 
    * desc: stores service parameters
    */
    public java.util.Vector getParameters() {
        return parameters;
        
    }
    
    /** Adds the needed parameter to the request.
    *
    * The parameters required by the service are defined within
    * its WSDL descriptor file
    *
    * @param paramName
    * type: java.lang.String - 
    * desc: the parameter name
    *
    * @param paramClass 
    * type: java.lang.Class - 
    * desc: the class of the parameter
    *
    * @param paramValue 
    * type:java.lang.Object - 
    * desc: the parameter
    *
    * @param encoding 
    * type:java.lang.String - 
    * desc: the parameter encoding
    */
    public void addParameter(String paramName, Class  paramClass, Object paramValue, String encoding) {
        parameters.addElement(new Parameter(paramName, paramClass, paramValue, encoding));
        
    }
    
    /** Processes client service requests.
    *
    * @param url
    * type: java.lang.String - 
    * desc: the service endpoint url
    *       defined in the WSDL file
    *
    * @throws SOAPException 
    * type: org.apache.soap.SOAPException - 
    * desc: thrown if an exception occurs
    *       contains details of the exception
    *
    * @return java.lang.Object - 
    * desc: the results of the service request
    *       must be cast by caller
    */
    public Object processRequest(String url) throws SOAPException {
        // create soap response
        Response response = null;
        Object result = null;
        try {
            request.setParams(parameters);
            response = request.invoke(new java.net.URL(url), "");
            // verify result
            if (response.generatedFault() == true ) {
                    logger.warn(response.getFault().getFaultString());
            }
            else {
            /* get result as object
              * caller must cast result to proper type
              */
                result = response.getReturnValue().getValue();
                // if logging enabled, output response
                if (logger.isDebugEnabled()) {
                    logger.debug(response.getReturnValue().toString());
                }
            }
        }
        catch (java.net.MalformedURLException exception) {
            // if logging enabled output exception
            if (logger.isDebugEnabled()) {
                logger.error(exception.getMessage(), exception);
            }
        }
        return result;
    }
    
}
