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
public class ItemDescriptor extends
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

  public ItemDescriptor() {
    super();
    nsURI = "http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content";
    xmlName = "item";
    XMLFieldDescriptorImpl desc = null;
    XMLFieldHandler handler = null;
    FieldValidator fieldValidator = null;
    // -- initialize attribute descriptors

    // -- initialize element descriptors

    // -- _topic
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.String.class,
        "_topic",
        "topic",
        NodeType.Element);
    desc.setImmutable(true);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Item target = (Item) object;
        return target.getTopic();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Item target = (Item) object;
          target.setTopic((java.lang.String) value);
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

    // -- validation code for: _topic
    fieldValidator = new FieldValidator();
    { // -- local scope
      StringValidator sv = new StringValidator();
      sv.setWhiteSpace("preserve");
      fieldValidator.setValidator(sv);
    }
    desc.setValidator(fieldValidator);

    // -- _pubDate
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.String.class,
        "_pubDate",
        "pubDate",
        NodeType.Element);
    desc.setImmutable(true);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Item target = (Item) object;
        return target.getPubDate();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Item target = (Item) object;
          target.setPubDate((java.lang.String) value);
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

    // -- validation code for: _pubDate
    fieldValidator = new FieldValidator();
    { // -- local scope
      StringValidator sv = new StringValidator();
      sv.setWhiteSpace("preserve");
      fieldValidator.setValidator(sv);
    }
    desc.setValidator(fieldValidator);

    // -- _title
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.String.class,
        "_title",
        "title",
        NodeType.Element);
    desc.setImmutable(true);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Item target = (Item) object;
        return target.getTitle();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Item target = (Item) object;
          target.setTitle((java.lang.String) value);
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

    // -- validation code for: _title
    fieldValidator = new FieldValidator();
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
        Item target = (Item) object;
        return target.getLink();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Item target = (Item) object;
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

    // -- _description
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.String.class,
        "_description",
        "description",
        NodeType.Element);
    desc.setImmutable(true);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Item target = (Item) object;
        return target.getDescription();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Item target = (Item) object;
          target.setDescription((java.lang.String) value);
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

    // -- validation code for: _description
    fieldValidator = new FieldValidator();
    { // -- local scope
      StringValidator sv = new StringValidator();
      sv.setWhiteSpace("preserve");
      fieldValidator.setValidator(sv);
    }
    desc.setValidator(fieldValidator);

    // -- _quote
    desc =
      new XMLFieldDescriptorImpl(
        Quote.class,
        "_quote",
        "quote",
        NodeType.Element);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Item target = (Item) object;
        return target.getQuote();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Item target = (Item) object;
          target.setQuote((Quote) value);
        } catch (Exception ex) {
          throw new IllegalStateException(ex.toString());
        }
      }

      @Override
      public Object newInstance(Object parent) {
        return new Quote();
      }
    });
    desc.setHandler(handler);
    desc
      .setNameSpaceURI("http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content");
    desc.setMultivalued(false);
    addFieldDescriptor(desc);

    // -- validation code for: _quote
    fieldValidator = new FieldValidator();
    desc.setValidator(fieldValidator);

  } // -- org.apache.jetspeed.xml.api.jcm.ItemDescriptor()

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
  public Class<Item> getJavaClass() {
    return org.apache.jetspeed.xml.api.jcm.Item.class;
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
