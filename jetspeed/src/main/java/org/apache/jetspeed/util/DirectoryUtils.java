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

package org.apache.jetspeed.util;

import java.io.File;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;


/*
 * File System Directory Utilities. Some utilities that java.io doesn't give us.
 *
 *    rmdir() - removes a directory and all subdirectories and files underneath.
 *
 *  @author David S. Taylor <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public class DirectoryUtils
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(DirectoryUtils.class.getName());
    
    public static void main(String[] args)
    {
        DirectoryUtils.rmdir(args[0]);
    }

    /*
     *  Removes a directory and all subdirectories and files beneath it.
     *
     * @param directory The name of the root directory to be deleted.
     * @return boolean If all went successful, returns true, otherwise false.
     * 
     */
    public static final boolean rmdir(String directory)
    {    
        try
        {
            File dir = new File(directory);
            if (!dir.isDirectory())            
            {
                dir.delete();
                return true;
            }

            deleteTraversal(directory);

            dir.delete();
            return true;

        }
        catch (Exception e)
        {
            logger.error("Error in rmdir utility:" , e);
            return false;
        }
    }

    /*
     *  Recursive deletion engine, traverses through all subdirectories, 
     *  attempting to delete every file and directory it comes across.
     *  NOTE: this version doesn't do any security checks, nor does it 
     *  check for file modes and attempt to change them.
     *
     * @param path The directory path to be traversed.
     * 
     */            
    private static void deleteTraversal(String path)
    {
        File file = new File(path);
        if (file.isFile()) 
        {
            try 
            {
                file.delete();
            }
            catch (Exception e)
            {
                logger.error("Failed to Delete file: " + path + " : " , e);
                file.deleteOnExit(); // try to get it later...
            }
        } 
        else if (file.isDirectory()) 
        {
            if (!path.endsWith(File.separator))
                path += File.separator;

            String list[] = file.list();

            // Process all files recursivly
            for(int ix = 0; list != null && ix < list.length; ix++)
                deleteTraversal(path + list[ix]);

            // now try to delete the directory
            try 
            {
                file.delete();
            }
            catch (Exception e)
            {
                logger.error("Failed to Delete directory: " + path + " : " , e);
                file.deleteOnExit(); // try to get it later...
            }
            
        }
    }            
}



