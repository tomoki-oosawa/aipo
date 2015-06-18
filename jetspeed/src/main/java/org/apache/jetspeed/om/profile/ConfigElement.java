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

package org.apache.jetspeed.om.profile;

import java.util.Iterator;

/**
 * ConfigElement is the base interface that objects must implement in order
 * to be used with the Profile service.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ConfigElement.java,v 1.5 2004/02/23 03:05:01 jford Exp $
 */
public interface ConfigElement extends Cloneable
{    

    /**
     * @return the name of this entry. This value is guaranteed to be unique at
     * least within the current Document.
     */
    public String getName();

    /**
     * Changes the name of this entry
     * @param name the new name for this entry
     */
    public void setName(String name);
    
    public String getParameterValue(String name);

    public Parameter getParameter(String name);

    public Iterator getParameterIterator();

    public Parameter getParameter(int index)
        throws java.lang.IndexOutOfBoundsException;

    public int getParameterCount();

    public void removeAllParameter();

    public Parameter removeParameter(int index);

    public void setParameter(int index, Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException;

    public Parameter[] getParameter();

    public void addParameter(Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException;

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;
}
