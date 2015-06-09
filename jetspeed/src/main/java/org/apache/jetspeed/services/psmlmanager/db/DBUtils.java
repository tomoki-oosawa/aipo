/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.apache.jetspeed.services.psmlmanager.db;

//standard java stuff
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;

// Jetspeed classes
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger; 

//castor support
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.Marshaller;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.ValidationException;

/**
 * This is a utility class used for database PSML implementation.
 *
 * @author <a href="mailto:adambalk@cisco.com">Atul Dambalkar</a>
 * @version $Id: DBUtils.java,v 1.7 2004/02/23 03:32:19 jford Exp $
 */
public class DBUtils 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(DBUtils.class.getName());
    
    /** Deserialize a PSML structure read from bytes array using Castor
     *  XML unmarshaller
     *
     * @param portletBytes Bytes array to load the PSML from
     * @return PSML structure Portlets object
     */
    public static Portlets bytesToPortlets(byte[] portletBytes, Mapping mapping) 
    {
        Reader reader = new StringReader(new String(portletBytes));
        try 
        {
            Unmarshaller unmarshaller = new Unmarshaller((Mapping)mapping);
            return (Portlets)unmarshaller.unmarshal(reader);

//            return Portlets.unmarshal(reader);
        }
        catch (MarshalException e)
        {
            logger.error("PSMLManager: Could not unmarshal the inputstream ", e);
        }  
        catch (MappingException e)
        {
            logger.error("PSMLManager: Could not unmarshal the inputstream ", e);
        }  

        catch (ValidationException e)
        {
            logger.error("PSMLManager: document is not valid", e);
        }
        finally
        {
            try { 
                reader.close(); 
            } 
            catch (IOException e) 
            { 
                logger.error("", e); 
            }
        }
        return null; // control shouldn't arrive here 
    }

    /** Serialize a PSML structure using string writer with Castor XML 
     * marshaller, put it in bytes array and return it.
     *
     * @param portlets the structure to convert to bytes array
     * @return Bytes array object for portles
     */
    public static byte[] portletsToBytes(Portlets portlets, Mapping mapping) 
    {
        if (portlets == null)
        {
            String message = "PSMLManager: Must specify portlets";
            logger.error( message );
            throw new IllegalArgumentException( message );
        }

        StringWriter writer = new StringWriter();
        try 
        {
//            portlets.marshal(writer);

            Marshaller marshaller = new Marshaller(writer);
            marshaller.setMapping(mapping);
            marshaller.marshal(portlets);
            
            if (logger.isDebugEnabled())
            	logger.debug("Portlets: " + writer.toString());

            /**** Platform's default character encoding will be used ****/
            return writer.toString().getBytes(); 
        }
        catch (MarshalException e)
        {
            logger.error("PSMLManager: Could not marshal the stringwriter ", e);
        }
        catch (IOException e)
        {
            logger.error("PSMLManager: Could not marshal the stringwriter ", e);
        }
        catch (MappingException e)
        {
            logger.error("PSMLManager: Could not marshal the stringwriter ", e);
        }
        catch (ValidationException e)
        {
            logger.error("PSMLManager: document is not valid", e);
        }
        finally
        {
            try 
            { 
                writer.close(); 
            } 
            catch (IOException e) 
            { 
                logger.error("", e); 
            }
        }
        return null; // control shouldn't arrive here 
    }

}
