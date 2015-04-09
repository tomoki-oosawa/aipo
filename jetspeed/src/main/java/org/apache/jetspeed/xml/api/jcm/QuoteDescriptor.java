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
public class QuoteDescriptor extends
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

  public QuoteDescriptor() {
    super();
    nsURI = "http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content";
    xmlName = "quote";
    XMLFieldDescriptorImpl desc = null;
    XMLFieldHandler handler = null;
    FieldValidator fieldValidator = null;
    // -- initialize attribute descriptors

    // -- initialize element descriptors

    // -- _author
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.String.class,
        "_author",
        "author",
        NodeType.Element);
    desc.setImmutable(true);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Quote target = (Quote) object;
        return target.getAuthor();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Quote target = (Quote) object;
          target.setAuthor((java.lang.String) value);
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
    desc.setRequired(true);
    desc.setMultivalued(false);
    addFieldDescriptor(desc);

    // -- validation code for: _author
    fieldValidator = new FieldValidator();
    fieldValidator.setMinOccurs(1);
    { // -- local scope
      StringValidator sv = new StringValidator();
      sv.setWhiteSpace("preserve");
      fieldValidator.setValidator(sv);
    }
    desc.setValidator(fieldValidator);

    // -- _link
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.String.class,
        "_link",
        "link",
        NodeType.Element);
    desc.setImmutable(true);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Quote target = (Quote) object;
        return target.getLink();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Quote target = (Quote) object;
          target.setLink((java.lang.String) value);
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
    desc.setMultivalued(false);
    addFieldDescriptor(desc);

    // -- validation code for: _link
    fieldValidator = new FieldValidator();
    { // -- local scope
      StringValidator sv = new StringValidator();
      sv.setWhiteSpace("preserve");
      fieldValidator.setValidator(sv);
    }
    desc.setValidator(fieldValidator);

    // -- _p
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.String.class,
        "_p",
        "p",
        NodeType.Element);
    desc.setImmutable(true);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Quote target = (Quote) object;
        return target.getP();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Quote target = (Quote) object;
          target.setP((java.lang.String) value);
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
    desc.setRequired(true);
    desc.setMultivalued(false);
    addFieldDescriptor(desc);

    // -- validation code for: _p
    fieldValidator = new FieldValidator();
    fieldValidator.setMinOccurs(1);
    { // -- local scope
      StringValidator sv = new StringValidator();
      sv.setWhiteSpace("preserve");
      fieldValidator.setValidator(sv);
    }
    desc.setValidator(fieldValidator);

  } // -- org.apache.jetspeed.xml.api.jcm.QuoteDescriptor()

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
  public Class<Quote> getJavaClass() {
    return org.apache.jetspeed.xml.api.jcm.Quote.class;
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
