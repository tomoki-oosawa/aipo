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

import org.exolab.castor.xml.FieldValidator;
import org.exolab.castor.xml.NodeType;
import org.exolab.castor.xml.XMLFieldHandler;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;
import org.exolab.castor.xml.validators.StringValidator;

/**
 *
 * @version $Revision$ $Date$
 **/
public class EntryDescriptor extends
    org.exolab.castor.xml.util.XMLClassDescriptorImpl {

  // --------------------------/
  // - Class/Member Variables -/
  // --------------------------/

  private java.lang.String nsPrefix;

  private java.lang.String nsURI;

  private java.lang.String xmlName;

  private org.exolab.castor.xml.XMLFieldDescriptor identity;

  // ----------------/
  // - Constructors -/
  // ----------------/

  public EntryDescriptor() {
    super();
    nsURI = "http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content";
    xmlName = "entry";
    XMLFieldDescriptorImpl desc = null;
    XMLFieldHandler handler = null;
    FieldValidator fieldValidator = null;
    // -- initialize attribute descriptors

    // -- _name
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.String.class,
        "_name",
        "name",
        NodeType.Attribute);
    desc.setImmutable(true);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Entry target = (Entry) object;
        return target.getName();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Entry target = (Entry) object;
          target.setName((java.lang.String) value);
        } catch (Exception ex) {
          throw new IllegalStateException(ex.toString());
        }
      }

      @Override
      public Object newInstance(Object parent) {
        return null;
      }
    });
    desc.setHandler(handler);
    desc
      .setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
    addFieldDescriptor(desc);

    // -- validation code for: _name
    fieldValidator = new FieldValidator();
    { // -- local scope
      StringValidator sv = new StringValidator();
      sv.setWhiteSpace("preserve");
      fieldValidator.setValidator(sv);
    }
    desc.setValidator(fieldValidator);

    // -- initialize element descriptors

    // -- _image
    desc =
      new XMLFieldDescriptorImpl(
        Image.class,
        "_image",
        "image",
        NodeType.Element);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Entry target = (Entry) object;
        return target.getImage();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Entry target = (Entry) object;
          target.setImage((Image) value);
        } catch (Exception ex) {
          throw new IllegalStateException(ex.toString());
        }
      }

      @Override
      public Object newInstance(Object parent) {
        return new Image();
      }
    });
    desc.setHandler(handler);
    desc
      .setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
    desc.setRequired(true);
    desc.setMultivalued(false);
    addFieldDescriptor(desc);

    // -- validation code for: _image
    fieldValidator = new FieldValidator();
    fieldValidator.setMinOccurs(1);
    desc.setValidator(fieldValidator);

  } // -- org.apache.jetspeed.xml.api.jcm.EntryDescriptor()

  // -----------/
  // - Methods -/
  // -----------/

  /**
    **/
  @Override
  public org.exolab.castor.mapping.AccessMode getAccessMode() {
    return null;
  } // -- org.exolab.castor.mapping.AccessMode getAccessMode()

  /**
    **/
  @Override
  public org.exolab.castor.mapping.ClassDescriptor getExtends() {
    return null;
  } // -- org.exolab.castor.mapping.ClassDescriptor getExtends()

  /**
    **/
  @Override
  public org.exolab.castor.mapping.FieldDescriptor getIdentity() {
    return identity;
  } // -- org.exolab.castor.mapping.FieldDescriptor getIdentity()

  /**
    **/
  @Override
  public Class<Entry> getJavaClass() {
    return org.apache.jetspeed.xml.api.jcm.Entry.class;
  } // -- java.lang.Class getJavaClass()

  /**
    **/
  @Override
  public java.lang.String getNameSpacePrefix() {
    return nsPrefix;
  } // -- java.lang.String getNameSpacePrefix()

  /**
    **/
  @Override
  public java.lang.String getNameSpaceURI() {
    return nsURI;
  } // -- java.lang.String getNameSpaceURI()

  /**
    **/
  @Override
  public org.exolab.castor.xml.TypeValidator getValidator() {
    return this;
  } // -- org.exolab.castor.xml.TypeValidator getValidator()

  /**
    **/
  @Override
  public java.lang.String getXMLName() {
    return xmlName;
  } // -- java.lang.String getXMLName()

}
