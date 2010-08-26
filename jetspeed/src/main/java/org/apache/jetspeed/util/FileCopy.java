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

import java.io.*;
import java.net.URL;

/*
 * File Copy Utilities. Some utilities that java.io doesn't give us.
 *
 *    copy() - copies one file source to another file destination.
 *    copyFromURL)() - copies from a URL source to a file destination.
 *
 *  @author David S. Taylor <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 */

public class FileCopy {

    public static final int BUFFER_SIZE = 4096;

    /*
     *  Copies one file source to another file destination. 
     *
     * @param source The source file.
     * @param destination The destination file.
     * @throws IOException When an IO error occurs, this exception is thrown.
     */
    public static final void copy(String source, String destination)
                throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];    
        BufferedInputStream input;
        BufferedOutputStream output;

        input = new BufferedInputStream(new FileInputStream(source));
        output = new BufferedOutputStream(new FileOutputStream(destination));

        copyStream(input, output, buffer);

        input.close();
        output.close();
    }

    /*
     *  Copies from a URL source to a file destination.
     *
     * @param source The source URL.
     * @param destination The destination file.
     * @throws IOException When an IO error occurs, this exception is thrown.
     */
    public static final void copyFromURL(String source, String destination)
              throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];    
        URL url = new URL(source);
          BufferedInputStream input;
          BufferedOutputStream output;
        
        
        input = new BufferedInputStream(new DataInputStream(url.openStream()));
        output = new BufferedOutputStream(new FileOutputStream(destination));
        
        copyStream(input, output, buffer);
        
        input.close();
        output.close();
    }

    /*
     *  Generic copy from a input stream to an output stream.
     *
     * @param input The source input stream.
     * @param output The destination output stream.
     * @param buffer The user provided buffer.
     * @throws IOException When an IO error occurs, this exception is thrown.
     */
    public static final void copyStream(InputStream input,
                                        OutputStream output,
                                        byte[] buffer)
                throws IOException
    {
        int bytesRead;

        while((bytesRead = input.read(buffer)) != -1)
            output.write(buffer, 0, bytesRead);
    }

}
