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

import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.FieldValidator;
import org.exolab.castor.xml.TypeValidator;
import org.exolab.castor.xml.XMLFieldDescriptor;
import org.exolab.castor.xml.handlers.*;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;
import org.exolab.castor.xml.validators.*;

/**
 * 
 * @version $Revision$ $Date$
**/
public class ChannelDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String nsPrefix;

    private java.lang.String nsURI;

    private java.lang.String xmlName;

    private org.exolab.castor.xml.XMLFieldDescriptor identity;


      //----------------/
     //- Constructors -/
    //----------------/

    public ChannelDescriptor() {
        super();
        nsURI = "http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content";
        xmlName = "channel";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        //-- initialize attribute descriptors
        
        //-- initialize element descriptors
        
        //-- _topics
        desc = new XMLFieldDescriptorImpl(Topics.class, "_topics", "topics", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getTopics();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setTopics( (Topics) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new Topics();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _topics
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _itemList
        desc = new XMLFieldDescriptorImpl(Item.class, "_itemList", "item", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getItem();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.addItem( (Item) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new Item();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _itemList
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(0);
        desc.setValidator(fieldValidator);
        
        //-- _title
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_title", "title", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getTitle();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setTitle( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _title
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _link
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_link", "link", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getLink();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setLink( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _link
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _description
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_description", "description", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getDescription();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setDescription( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _description
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _image
        desc = new XMLFieldDescriptorImpl(Image.class, "_image", "image", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getImage();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setImage( (Image) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new Image();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _image
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _textinput
        desc = new XMLFieldDescriptorImpl(Textinput.class, "_textinput", "textinput", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getTextinput();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setTextinput( (Textinput) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new Textinput();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _textinput
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _rating
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_rating", "rating", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getRating();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setRating( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _rating
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _copyright
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_copyright", "copyright", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getCopyright();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setCopyright( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _copyright
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _pubDate
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_pubDate", "pubDate", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getPubDate();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setPubDate( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _pubDate
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _lastBuildDate
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_lastBuildDate", "lastBuildDate", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getLastBuildDate();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setLastBuildDate( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _lastBuildDate
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _docs
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_docs", "docs", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getDocs();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setDocs( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _docs
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _managingEditor
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_managingEditor", "managingEditor", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getManagingEditor();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setManagingEditor( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _managingEditor
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _webMaster
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_webMaster", "webMaster", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getWebMaster();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setWebMaster( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _webMaster
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _language
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_language", "language", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                Channel target = (Channel) object;
                return target.getLanguage();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    Channel target = (Channel) object;
                    target.setLanguage( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _language
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
    } //-- org.apache.jetspeed.xml.api.jcm.ChannelDescriptor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public org.exolab.castor.mapping.AccessMode getAccessMode()
    {
        return null;
    } //-- org.exolab.castor.mapping.AccessMode getAccessMode() 

    /**
    **/
    public org.exolab.castor.mapping.ClassDescriptor getExtends()
    {
        return null;
    } //-- org.exolab.castor.mapping.ClassDescriptor getExtends() 

    /**
    **/
    public org.exolab.castor.mapping.FieldDescriptor getIdentity()
    {
        return identity;
    } //-- org.exolab.castor.mapping.FieldDescriptor getIdentity() 

    /**
    **/
    public java.lang.Class getJavaClass()
    {
        return org.apache.jetspeed.xml.api.jcm.Channel.class;
    } //-- java.lang.Class getJavaClass() 

    /**
    **/
    public java.lang.String getNameSpacePrefix()
    {
        return nsPrefix;
    } //-- java.lang.String getNameSpacePrefix() 

    /**
    **/
    public java.lang.String getNameSpaceURI()
    {
        return nsURI;
    } //-- java.lang.String getNameSpaceURI() 

    /**
    **/
    public org.exolab.castor.xml.TypeValidator getValidator()
    {
        return this;
    } //-- org.exolab.castor.xml.TypeValidator getValidator() 

    /**
    **/
    public java.lang.String getXMLName()
    {
        return xmlName;
    } //-- java.lang.String getXMLName() 

}
