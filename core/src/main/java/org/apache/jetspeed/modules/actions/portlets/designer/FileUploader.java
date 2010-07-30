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
package org.apache.jetspeed.modules.actions.portlets.designer;


import java.io.File;
import org.apache.turbine.util.upload.FileItem;

//for logging
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * File Uploader helper
 * 
 * @author <a href="mailto:jlim@gluecode.com">Jonas Lim</a>
 * @version $Id: FileUploader.java,v 1.1 2004/03/10 22:53:59 taylor Exp $
 */
public class FileUploader
{

    private static Log log = LogFactory.getLog(FileUploader.class);

    public boolean upload(FileItem fileItem, String location,
            String fileTypes[])
    {
        boolean hasUpload = false;

        try
        {
            File file = new File(fileItem.getFileName());
            String filename = file.getName();
            String contentType = fileItem.getContentType();

            int index = filename.lastIndexOf("\\");
            int index2 = filename.lastIndexOf("//");

            if (index > 0)
            {
                filename = filename.substring(index + 1);
            }

            if (index2 > 0)
            {
                filename = filename.substring(index2 + 1);
            }
            // remove restrictions in upload
/*
            boolean isFileType = false;
            for (int i = 0; i < fileTypes.length; i++)
            {
                if (contentType.equalsIgnoreCase(fileTypes[i]))
                {
                    isFileType = true;
                    break;
                }
            }

            if (isFileType == true || fileTypes == null
                    || fileTypes.length == 0)
            {
                fileItem.write(location + filename);
                hasUpload = true;
            }
          */  
            
            fileItem.write(location + filename);
            hasUpload = true;            
        } catch (Exception e)
        {
            log.info("error in FileUploader class");
            hasUpload = false;
            log.error(e);
        }
        return hasUpload;
    }

    public String getFilename(FileItem fileItem, String location,
            String fileTypes[])
    {
        String filename = "no result";
        try
        {
            File file = new File(fileItem.getFileName());
            filename = fileItem.getName();

            int index = filename.lastIndexOf("\\");
            int index2 = filename.lastIndexOf("//");

            if (index > 0)
            {
                filename = filename.substring(index + 1);
            }

            if (index2 > 0)
            {
                filename = filename.substring(index2 + 1);
            }
            filename = location + filename;
        } catch (Exception e)
        {
            log.error(e);
        }
        return filename;
    }
}