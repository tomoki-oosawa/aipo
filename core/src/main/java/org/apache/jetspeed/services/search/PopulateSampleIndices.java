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
package org.apache.jetspeed.services.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;

import org.apache.turbine.util.TurbineConfig;

/**
 * Populate sample data for Search Portlet
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PopulateSampleIndices.java,v 1.2 2004/02/23 03:48:47 jford Exp $
 */
public class PopulateSampleIndices
{
    static public final String SAMPLE_URLS = "./test/search-sample-urls.txt";
    
    public static void main(String args[])
    {
        try
        {
            TurbineConfig config = null;
            
            config = new TurbineConfig( "./webapp", "/WEB-INF/conf/TurbineResources.properties");
            config.init();
                    
            FileReader reader = new FileReader(SAMPLE_URLS);
            BufferedReader breader = new BufferedReader(reader);
            String line = null;
            System.out.println("================= Populate Sample Indices =================");
            System.out.println("... creates Search sample portlet test data from URLs provided in file: " + SAMPLE_URLS);
            System.out.println("...");                        
            
            while (null != (line = breader.readLine()))
            {
                System.out.println("Creating index for: " + line);
                URL url = new URL(line);
                
                Search.add(url);                        
            }
            System.out.println("===========================================================");
            
            reader.close();           
        }
        catch (Exception e)
        {
            System.out.println("Exception reading URLS" + e);
        }
    }
    
}
