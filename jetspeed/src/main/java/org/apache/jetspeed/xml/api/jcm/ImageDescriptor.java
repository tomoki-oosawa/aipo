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
import org.exolab.castor.xml.validators.IntegerValidator;
import org.exolab.castor.xml.validators.StringValidator;

/**
 *
 * @version $Revision$ $Date$
 **/
public class ImageDescriptor extends
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

  public ImageDescriptor() {
    super();
    nsURI = "http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content";
    xmlName = "image";
    XMLFieldDescriptorImpl desc = null;
    XMLFieldHandler handler = null;
    FieldValidator fieldValidator = null;
    // -- initialize attribute descriptors

    // -- initialize element descriptors

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
        Image target = (Image) object;
        return target.getTitle();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Image target = (Image) object;
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
    desc.setRequired(true);
    desc.setMultivalued(false);
    addFieldDescriptor(desc);

    // -- validation code for: _title
    fieldValidator = new FieldValidator();
    fieldValidator.setMinOccurs(1);
    { // -- local scope
      StringValidator sv = new StringValidator();
      sv.setWhiteSpace("preserve");
      fieldValidator.setValidator(sv);
    }
    desc.setValidator(fieldValidator);

    // -- _url
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.String.class,
        "_url",
        "url",
        NodeType.Element);
    desc.setImmutable(true);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Image target = (Image) object;
        return target.getUrl();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Image target = (Image) object;
          target.setUrl((java.lang.String) value);
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

    // -- validation code for: _url
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
        Image target = (Image) object;
        return target.getLink();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Image target = (Image) object;
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
    desc.setRequired(true);
    desc.setMultivalued(false);
    addFieldDescriptor(desc);

    // -- validation code for: _link
    fieldValidator = new FieldValidator();
    fieldValidator.setMinOccurs(1);
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
        Image target = (Image) object;
        return target.getDescription();
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Image target = (Image) object;
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

    // -- _width
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.Integer.TYPE,
        "_width",
        "width",
        NodeType.Element);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Image target = (Image) object;
        if (!target.hasWidth()) {
          return null;
        }
        return new Integer(target.getWidth());
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Image target = (Image) object;
          // if null, use delete method for optional primitives
          if (value == null) {
            target.deleteWidth();
            return;
          }
          target.setWidth(((Integer) value).intValue());
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

    // -- validation code for: _width
    fieldValidator = new FieldValidator();
    { // -- local scope
      IntegerValidator iv = new IntegerValidator();
      fieldValidator.setValidator(iv);
    }
    desc.setValidator(fieldValidator);

    // -- _height
    desc =
      new XMLFieldDescriptorImpl(
        java.lang.Integer.TYPE,
        "_height",
        "height",
        NodeType.Element);
    handler = (new XMLFieldHandler() {
      @Override
      public Object getValue(Object object) throws IllegalStateException {
        Image target = (Image) object;
        if (!target.hasHeight()) {
          return null;
        }
        return new Integer(target.getHeight());
      }

      @Override
      public void setValue(Object object, Object value)
          throws IllegalStateException, IllegalArgumentException {
        try {
          Image target = (Image) object;
          // if null, use delete method for optional primitives
          if (value == null) {
            target.deleteHeight();
            return;
          }
          target.setHeight(((Integer) value).intValue());
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

    // -- validation code for: _height
    fieldValidator = new FieldValidator();
    { // -- local scope
      IntegerValidator iv = new IntegerValidator();
      fieldValidator.setValidator(iv);
    }
    desc.setValidator(fieldValidator);

  } // -- org.apache.jetspeed.xml.api.jcm.ImageDescriptor()

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
  public Class<Image> getJavaClass() {
    return org.apache.jetspeed.xml.api.jcm.Image.class;
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
