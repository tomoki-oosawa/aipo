/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.services.search.handlers;

// Java imports
import java.io.IOException;
import java.net.URL;

// Commons HTTPClient
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

// Jetspeed imports
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.search.AbstractObjectHandler;
import org.apache.jetspeed.services.search.BaseParsedObject;
import org.apache.jetspeed.services.search.ParsedObject;

/**
 * This object handler deals with URLs.
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: URLToDocHandler.java,v 1.4 2004/02/23 03:47:46 jford Exp $
 */
public class URLToDocHandler extends AbstractObjectHandler
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(URLToDocHandler.class.getName());
    
    /**
     * Parses a specific object into a document suitable for index placement
     * 
     * @param o
     * @return 
     */
    public ParsedObject parseObject(Object o)
    {
        ParsedObject result = new BaseParsedObject();

        if ((o instanceof URL) == false)
        {
            logger.error("URLToDocHandler: invalid object type: " + o);
            return null;
        }

        URL pageToAdd = (URL) o;

        HttpClient client = new HttpClient();
        client.startSession(pageToAdd);
        GetMethod method = new GetMethod(pageToAdd.getPath());
        method.setFollowRedirects(true);
        int statusCode = -1;
        int attempt = 0;

        // We will retry up to 3 times.
        while (statusCode == -1 && attempt < 3)
        {
            try
            {
                // execute the method.
                client.executeMethod(method);
                statusCode = method.getStatusCode();
                if (logger.isDebugEnabled())
                {
                    logger.debug("URL = " + pageToAdd.toString() + "Status code = " + statusCode);
                }
            }
            catch (HttpException e)
            {
                // We will retry
            }
            catch (IOException e)
            {
                return null;
            }
        }
        // Check that we didn't run out of retries.
        if (statusCode != -1)
        {
            String content = null;
            try
            {
                content = method.getDataAsString();
            }
            catch (IOException ioe)
            {
                logger.error("Getting content for " + pageToAdd.toString(), ioe);
            }

            if (content != null)
            {
                try
                {
                    result.setKey(java.net.URLEncoder.encode(pageToAdd.toString()));
                    result.setType(ParsedObject.OBJECT_TYPE_URL);
                    // TODO: We should extract the <title> tag here.
                    result.setTitle(pageToAdd.toString());
                    result.setContent(content);
                    result.setDescription("");
                    result.setLanguage("");
                    result.setURL(pageToAdd);
                    result.setClassName(o.getClass().getName());
                    logger.info("Parsed '" + pageToAdd.toString() + "'");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    logger.error("Adding document to index", e);
                }
            }
        }
        try
        {
            client.endSession();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            logger.error("Ending session to " + pageToAdd.toString(), ioe);
        }

        return result;

    }
}

