/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id$
 */

package org.apache.jetspeed.xml.api.jcm;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision$ $Date$
**/
public class Topics implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _entryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Topics() {
        super();
        _entryList = new Vector();
    } //-- org.apache.jetspeed.xml.api.jcm.Topics()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param vEntry
    **/
    public void addEntry(Entry vEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        _entryList.addElement(vEntry);
    } //-- void addEntry(Entry) 

    /**
    **/
    public java.util.Enumeration enumerateEntry()
    {
        return _entryList.elements();
    } //-- java.util.Enumeration enumerateEntry() 

    /**
     * 
     * @param index
    **/
    public Entry getEntry(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _entryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Entry) _entryList.elementAt(index);
    } //-- Entry getEntry(int) 

    /**
    **/
    public Entry[] getEntry()
    {
        int size = _entryList.size();
        Entry[] mArray = new Entry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Entry) _entryList.elementAt(index);
        }
        return mArray;
    } //-- Entry[] getEntry() 

    /**
    **/
    public int getEntryCount()
    {
        return _entryList.size();
    } //-- int getEntryCount() 

    /**
    **/
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * 
     * @param out
    **/
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler) 

    /**
    **/
    public void removeAllEntry()
    {
        _entryList.removeAllElements();
    } //-- void removeAllEntry() 

    /**
     * 
     * @param index
    **/
    public Entry removeEntry(int index)
    {
        Object obj = _entryList.elementAt(index);
        _entryList.removeElementAt(index);
        return (Entry) obj;
    } //-- Entry removeEntry(int) 

    /**
     * 
     * @param index
     * @param vEntry
    **/
    public void setEntry(int index, Entry vEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _entryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _entryList.setElementAt(vEntry, index);
    } //-- void setEntry(int, Entry) 

    /**
     * 
     * @param entryArray
    **/
    public void setEntry(Entry[] entryArray)
    {
        //-- copy array
        _entryList.removeAllElements();
        for (int i = 0; i < entryArray.length; i++) {
            _entryList.addElement(entryArray[i]);
        }
    } //-- void setEntry(Entry) 

    /**
     * 
     * @param reader
    **/
    public static org.apache.jetspeed.xml.api.jcm.Topics unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.apache.jetspeed.xml.api.jcm.Topics) Unmarshaller.unmarshal(org.apache.jetspeed.xml.api.jcm.Topics.class, reader);
    } //-- org.apache.jetspeed.xml.api.jcm.Topics unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
