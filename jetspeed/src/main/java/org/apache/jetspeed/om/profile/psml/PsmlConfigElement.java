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

package org.apache.jetspeed.om.profile.psml;

import java.util.Vector;
import java.util.Iterator;

import org.apache.jetspeed.om.profile.*;

/**
 * Base simple bean-like implementation of the ConfigElement interface
 * suitable for Castor XML serialization.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PsmlConfigElement.java,v 1.6 2004/02/23 03:02:54 jford Exp $
 */
public /*abstract*/ class PsmlConfigElement implements ConfigElement, java.io.Serializable
{

    private String name = null;
    
    private Vector parameters = new Vector();


    public PsmlConfigElement()
    {}
         
    /** @see org.apache.jetspeed.om.registry.RegistryEntry#getName */
    public String getName()
    {
        return this.name;
    }
                                
    /** @see org.apache.jetspeed.om.registry.RegistryEntry#setName */
    public void setName( String name )
    {
        this.name = name;
    }

    /** @return the parameters */
    public Vector getParameters()
    {
        return this.parameters;
    }
                                
    /** Sets the parameters for this element
     * @param parameters 
     */
    public void setParameters(Vector parameters)
    {
        this.parameters = parameters;
    }

    public String getParameterValue(String name)
    {
        if (parameters == null)
            return null;

        for (int ix=0; ix < parameters.size(); ix++)
        {
            Parameter param = (Parameter)parameters.elementAt(ix);
            if (param.getName().equals(name))
                return param.getValue();
        }
        return null;
   }

    public Parameter getParameter(String name)
    {
        if (parameters == null)
            return null;

        for (int ix=0; ix < parameters.size(); ix++)
        {
            Parameter param = (Parameter)parameters.elementAt(ix);
            if (param.getName().equals(name))
                return param;
        }
        return null;
   }

    public Iterator getParameterIterator()
    {
        return parameters.iterator();
    }

    public Parameter getParameter(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > parameters.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Parameter) parameters.elementAt(index);
    } //-- Parameter getParameter(int) 

    public int getParameterCount()
    {
        return parameters.size();
    } //-- int getParameterCount() 

    public void removeAllParameter()
    {
        parameters.removeAllElements();
    } //-- void removeAllParameter() 

    public Parameter removeParameter(int index)
    {
        Object obj = parameters.elementAt(index);
        parameters.removeElementAt(index);
        return (Parameter) obj;
    } //-- Parameter removeParameter(int) 

    public void setParameter(int index, Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > parameters.size())) {
            throw new IndexOutOfBoundsException();
        }
        parameters.setElementAt(vParameter, index);
    } //-- void setParameter(int, Parameter) 

    public Parameter[] getParameter()
    {
        int size = parameters.size();
        Parameter[] mArray = new Parameter[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Parameter) parameters.elementAt(index);
        }
        return mArray;
    } //-- Parameter[] getParameter() 

    public void addParameter(Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        parameters.addElement(vParameter);
    } //-- void addParameter(Parameter) 

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        // clone the vector's Parameter contents
        if (this.parameters != null)
        {
            ((PsmlConfigElement)cloned).parameters = new Vector(this.parameters.size());
            Iterator it = this.parameters.iterator();
            while (it.hasNext())
            {
                ((PsmlConfigElement)cloned).parameters.add((Parameter) ((Parameter)it.next()).clone());
            }
        }
        
        return cloned;

    }   // clone

}
