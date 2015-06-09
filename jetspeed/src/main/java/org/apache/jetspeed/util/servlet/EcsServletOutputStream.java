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

package org.apache.jetspeed.util.servlet;

import javax.servlet.ServletOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/** This class is a ServletOutputStream wrapper around an existing
 *  OutputStream so that this OutputStream may be used within a 
 *  ServletResponse implementation.
 *
 * @author <a href="raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: EcsServletOutputStream.java,v 1.3 2004/02/23 03:19:26 jford Exp $
 */
public class EcsServletOutputStream extends ServletOutputStream {

    /** The real OutputStream to use */
    private OutputStream out = null;

    /** This constructor creates a new OutputStream and associates
     *  it with an existing OutputStream.
     *
     * @param out the OutputStream to use for writing data
     */
    protected EcsServletOutputStream(OutputStream out) {
        this.out = out;
    }

    /** Writes an integer to the data stream.
     *  This call is delegated to the wrapped OutputStream.
     *  All the inherited methods from ServletOutputStream will use
     *  this method to output data
     *
     * @param c the integer to write to the stream
     */
    public void write(int c) throws IOException {
        this.out.write(c);
    }
}
