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
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision$ $Date$
**/
public class Item implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _topic;

    private java.lang.String _pubDate;

    private java.lang.String _title;

    private java.lang.String _link;

    private java.lang.String _description;

    private Quote _quote;


      //----------------/
     //- Constructors -/
    //----------------/

    public Item() {
        super();
    } //-- org.apache.jetspeed.xml.api.jcm.Item()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
    **/
    public java.lang.String getLink()
    {
        return this._link;
    } //-- java.lang.String getLink() 

    /**
    **/
    public java.lang.String getPubDate()
    {
        return this._pubDate;
    } //-- java.lang.String getPubDate() 

    /**
    **/
    public Quote getQuote()
    {
        return this._quote;
    } //-- Quote getQuote() 

    /**
    **/
    public java.lang.String getTitle()
    {
        return this._title;
    } //-- java.lang.String getTitle() 

    /**
    **/
    public java.lang.String getTopic()
    {
        return this._topic;
    } //-- java.lang.String getTopic() 

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
     * 
     * @param description
    **/
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * 
     * @param link
    **/
    public void setLink(java.lang.String link)
    {
        this._link = link;
    } //-- void setLink(java.lang.String) 

    /**
     * 
     * @param pubDate
    **/
    public void setPubDate(java.lang.String pubDate)
    {
        this._pubDate = pubDate;
    } //-- void setPubDate(java.lang.String) 

    /**
     * 
     * @param quote
    **/
    public void setQuote(Quote quote)
    {
        this._quote = quote;
    } //-- void setQuote(Quote) 

    /**
     * 
     * @param title
    **/
    public void setTitle(java.lang.String title)
    {
        this._title = title;
    } //-- void setTitle(java.lang.String) 

    /**
     * 
     * @param topic
    **/
    public void setTopic(java.lang.String topic)
    {
        this._topic = topic;
    } //-- void setTopic(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static org.apache.jetspeed.xml.api.jcm.Item unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.apache.jetspeed.xml.api.jcm.Item) Unmarshaller.unmarshal(org.apache.jetspeed.xml.api.jcm.Item.class, reader);
    } //-- org.apache.jetspeed.xml.api.jcm.Item unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
