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


// Java imports
import java.util.Vector;
import java.util.Iterator;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.*;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.om.profile.Portlets;


/**
 * Base simple bean-like implementation of the Portlets interface
 * suitable for Castor XML serialization.
 *
 * sure wish I could figure out how to use Proxies with Castor...
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PsmlReference.java,v 1.5 2004/02/23 03:02:54 jford Exp $
 */
public class PsmlReference extends PsmlPortlets implements Reference, java.io.Serializable                                                
{
    protected String path;

    protected PsmlPortlets ref = new PsmlPortlets();

    /** Holds value of property securityRef. */
    private SecurityReference securityRef;
    
    public Portlets getPortletsReference()
    {
        return ref;
    }

    public void setPath(String path)
    {
        this.path = path;
        PsmlPortlets tempRef = (PsmlPortlets)PortalToolkit.getReference(path);
        if(tempRef != null)
        {
            ref = tempRef;
        }
    }

    public String getPath()
    {
        return this.path;
    }

    public PsmlReference()
    {
        super();
    }

    public Controller getController()
    {
        return ref.getController();
    }

    public void setController(Controller controller)
    {
        ref.setController(controller);       
    }

    public void setSecurity(Security security)
    {
        ref.setSecurity(security);
    }
 
    public Security getSecurity()
    {
        return ref.getSecurity();
    }

    public Vector getEntries()
    {
        return ref.getEntries();
    }

    public void setEntries(Vector entries)
    {
        ref.setEntries(entries);
    }

    public Vector getPortlets()
    {
        return ref.getPortlets();
    }

    public void setPortlets(Vector portlets)
    {
        ref.setPortlets(portlets);
    }

    public int getEntryCount()
    {
        return ref.getEntryCount();
    }

    public int getPortletsCount()
    {
        return ref.getPortletsCount();
    }

    public Entry removeEntry(int index)
    {
        return ref.removeEntry(index);
    } 

    public Portlets removePortlets(int index)
    {
        return ref.removePortlets(index);
    } 

    public Entry getEntry(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        return ref.getEntry(index);
    } 

    public Portlets getPortlets(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        return ref.getPortlets(index);
    } 


    public Iterator getEntriesIterator()
    {
        return ref.getEntriesIterator();
    }

    public Iterator getPortletsIterator()
    {
        return ref.getPortletsIterator();
    }

    public void addEntry(Entry entry)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.addEntry(entry);
    } 

    public void addPortlets(Portlets p)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.addPortlets(p);
    } 

    public Entry[] getEntriesArray()
    {
        return ref.getEntriesArray();
    }

    public Portlets[] getPortletsArray()
    {
        return ref.getPortletsArray();
    }

    //////////////////////////////////////////////////////////////////////////

    public Control getControl()
    {
        return ref.getControl();
    }

    public void setControl(Control control)
    {
        ref.setControl(control);
    }


    // Castor serialization methods
    
    /** Required by Castor 0.8.11 XML serialization for retrieving the metainfo
      */
    public MetaInfo getMetaInfo()
    {
        MetaInfo info = super.getMetaInfo();
        if (info == null)
        {
            info = ref.getMetaInfo();
        }        
        return info;
    }
                                
// helper getter setters into meta info

    /** @see org.apache.jetspeed.om.registry.MetaInfo#getTitle */
    public String getTitle()
    {
        return ref.getTitle();
    }
                                
    /** @see org.apache.jetspeed.om.registry.MetaInfo#setTitle */
    public void setTitle(String title)
    {
        ref.setTitle(title);
    }

    /** @see org.apache.jetspeed.om.registry.MetaInfo#getDescription */
    public String getDescription()
    {
        return ref.getDescription();
    }
                                
    /** @see org.apache.jetspeed.om.registry.MetaInfo#setDescription */
    public void setDescription(String description)
    {
        ref.setDescription(description);
    }

    /** @see org.apache.jetspeed.om.registry.MetaInfo#getImage */
    public String getImage()
    {
        return ref.getImage();
    }
                                
    /** @see org.apache.jetspeed.om.registry.MetaInfo#setImage */
    public void setImage(String image)
    {
        ref.setImage(image);
    }

    /////////////////////////////////////////////////////////////////////////

   /** @return the parameters */
    public Vector getParameters()
    {
        return ref.getParameters();
    }
                                
    /** Sets the parameters for this element
     * @param parameters 
     */
    public void setParameters(Vector parameters)
    {
        ref.setParameters(parameters);
    }

    public String getParameterValue(String name)
    {
        return ref.getParameterValue(name);
    }

    public Parameter getParameter(String name)
    {
        return ref.getParameter(name);
    }

    public Iterator getParameterIterator()
    {
        return ref.getParameterIterator();
    }

    public Parameter getParameter(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        return ref.getParameter(index);
    } 

    public int getParameterCount()
    {
        return ref.getParameterCount();
    } 

    public int getReferenceCount()
    {
        return ref.getReferenceCount();
    }

    public void removeAllParameter()
    {
        ref.removeAllParameter();
    } 

    public Parameter removeParameter(int index)
    {
        return ref.removeParameter(index);
    } 

    public void setParameter(int index, Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.setParameter(index,vParameter);
    } 

    public Parameter[] getParameter()
    {
        return ref.getParameter();
    } 

    public void addParameter(Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.addParameter(vParameter);
    } 

    public Reference getReference(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        return ref.getReference(index);
    }

    public Reference removeReference(int index)
    {
        return ref.removeReference(index);
    }

    public Iterator getReferenceIterator()
    {
        return ref.getReferenceIterator();
    }

    public void addReference(Reference ref)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.addReference(ref);
    }

    public Reference[] getReferenceArray()
    {
        return ref.getReferenceArray();
    }

    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef()
    {
        return securityRef;
    }    

    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef)
    {
        this.securityRef = securityRef;
    }    

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        ((PsmlReference)cloned).ref = ((this.ref == null) ? null : (PsmlPortlets) this.ref.clone());
        ((PsmlReference)cloned).securityRef = ((this.securityRef == null) ? null : (SecurityReference) this.securityRef.clone());

        return cloned;

    }   // clone

}

