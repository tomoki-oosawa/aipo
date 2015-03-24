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

/**
 * Utility for formatting paths
 * 
 * @author <a href="mailto:jlim@gluecode.com">Jonas Lim</a>
 * @version $Id: FormatPath.java,v 1.1 2004/03/10 22:53:59 taylor Exp $
 */
public class FormatPath
{

    public FormatPath()
    {
    }

    /**
     * Given a directory path with Windows or Unix file separators,
     * normalize to use Unix style separators and always
     * append a final separator for the directory.
     * @param path
     * @return
     */
    public static String normalizeDirectoryPath(String path)
    {
        if (path == null)
        {
            return path;
        }
        // convert all backslashes with normalized forward
        String resultPath = path.replace('\\', '/');
        if (!resultPath.endsWith("/"))
        {
            return resultPath.concat("/");
        }
        return resultPath;
    }

}