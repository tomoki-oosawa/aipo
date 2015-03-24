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

package org.apache.jetspeed.om.registry.base;

import org.apache.jetspeed.om.registry.MimetypeMap;
import org.apache.jetspeed.util.MimeType;
import java.util.Vector;
import java.util.Iterator;

/**
 * Simple bean-like implementation of the CapabilityMap
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BaseMimetypeMap.java,v 1.3 2004/02/23 03:08:26 jford Exp $
 */
public class BaseMimetypeMap implements MimetypeMap, java.io.Serializable
{
    private Vector mimetypesVector = new Vector();

    private transient Vector mimes;

    public BaseMimetypeMap()
    {
    }

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object==null)
        {
            return false;
        }

        BaseMimetypeMap obj = (BaseMimetypeMap)object;

        Iterator i = mimetypesVector.iterator();
        Iterator i2 = obj.mimetypesVector.iterator();
        while(i.hasNext())
        {
            String c1 = (String)i.next();
            String c2 = null;

            if (i2.hasNext())
            {
                c2 = (String)i2.next();
            }
            else
            {
                return false;
            }

            if (!c1.equals(c2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        return true;
    }

    public Iterator getMimetypes()
    {
        if (mimes == null)
        {
            buildMimetable();
        }

        return mimes.iterator();
    }

    public MimeType getPreferredMimetype()
    {
        if (mimes == null)
        {
            buildMimetable();
        }

        return (MimeType)mimes.get(0);
    }

    public void addMimetype(String name)
    {
        if (!mimetypesVector.contains(name))
        {
            mimetypesVector.add(name);
            buildMimetable();
        }
    }

    public void removeMimetype(String name)
    {
        mimetypesVector.remove(name);
        buildMimetable();
    }

    protected void buildMimetable()
    {
        Vector types = new Vector();
        Iterator i = mimetypesVector.iterator();

        while(i.hasNext())
        {
            String mime = (String)i.next();
            types.add(new MimeType(mime));
        }

        this.mimes = types;
    }

    // castor related method definitions

    public Vector getMimetypesVector()
    {
        return mimetypesVector;
    }

}
