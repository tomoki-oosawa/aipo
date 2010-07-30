/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.jetspeed.om.security.ldap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.Base64;

/**
 * The Base LDAP Object extending DirContext.
 * 
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender Kilicoglu</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino</a>
 * 
 */

public class BaseLDAPObject implements DirContext {
  // Constants
  protected final static String OK = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  protected final static String LDAP_DATE_PATTERN = "yyyyMMddmmHHss'Z'";

  // Instance variables
  protected LDAPURL ldapurl = null;

  protected boolean updated = false;

  protected String name = null;

  protected String id = null;

  protected Attributes myAttrs = null;

  protected boolean isNew = false;

  protected BasicAttributes rmAttrs = new BasicAttributes();

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(BaseLDAPObject.class.getName());

  /**
   * <p>
   * Creates an "LDAP-safe" ID from a String so that the generated ID is as
   * similar as possible to the original value. For example, value "Ryhm\u00e4"
   * ("group" in Finnish language) would be converted to "ryhma". If the value
   * contains an unknown character, it will be replaced by letter 'X'.
   * </p>
   */
  public String createId(String value) {
    value = value.replace('\u00e5', 'a');
    value = value.replace('\u00e4', 'a');
    value = value.replace('\u00f6', 'o');
    value = value.replace('\u00c5', 'A');
    value = value.replace('\u00c4', 'A');
    value = value.replace('\u00d6', 'O');
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < value.length(); i++) {
      char currentChar = value.charAt(i);
      if (isOK(currentChar))
        buf.append(currentChar);
      else
        buf.append('X');
    }

    return buf.toString();
  }

  /**
   * <p>
   * Checks if the specified character can be used in LDAP attribute name.
   * </p>
   */
  public boolean isOK(char value) {
    for (int i = 0; i < OK.length(); i++) {
      if (value == OK.charAt(i))
        return true;
    }

    return false;
  }

  /**
   * <p>
   * Set's the objectClass for this object.
   * </p>
   * 
   */
  public void setObjectClass(String name) {
    Attribute oc = new BasicAttribute("objectclass");
    oc.add(name);
    myAttrs.put(oc);
  }

  /**
   * <p>
   * Set's multiple objectClasses for this object.
   * </p>
   * 
   */
  public void setObjectClasses(String[] names) {
    Attribute oc = new BasicAttribute("objectclass");
    for (int i = 0; i < names.length; i++) {
      oc.add(names[i]);
    }
    myAttrs.put(oc);
  }

  protected String formatDate(Date date) {
    if (date == null) {
      date = new Date();
    }

    SimpleDateFormat formatter = new SimpleDateFormat(LDAP_DATE_PATTERN);
    formatter.setTimeZone(TimeZone.getDefault());
    return formatter.format(date);
  }

  protected Date parseDate(String value) {
    return parseDate(value, true);
  }

  protected Date parseDate(String value, boolean initializeIfNotSet) {
    try {
      SimpleDateFormat parser = new SimpleDateFormat(LDAP_DATE_PATTERN);
      parser.setTimeZone(TimeZone.getDefault());
      return parser.parse(value);
    } catch (Exception e) {
      logger.warn("Could not parse date '" + value + "'");
      if (initializeIfNotSet)
        return new Date();
      else
        return null;
    }
  }

  protected String serializePerm(Hashtable permStorage) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(baos);
    os.writeObject(permStorage);
    return Base64.encodeAsString(baos.toByteArray());
  }

  protected Hashtable deserializePerm(String permStorageContentsEncoded)
      throws Exception {
    byte[] decoded = Base64.decodeAsByteArray(permStorageContentsEncoded);
    ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
    ObjectInputStream is = new ObjectInputStream(bais);
    return (Hashtable) is.readObject();
  }

  /**
   * Updated Function
   * 
   * Return State of updated property
   * 
   * @return boolean updated property value
   */
  public boolean getupdated() {
    return this.updated;
  }

  /**
   * ID Function
   * 
   * Return Value of ID property
   * 
   * @return String ID property value
   */
  public String getId() {
    return id;
  }

  /**
   * ID Function
   * 
   * Set Value of ID property
   * 
   * @param Id
   *            ID property value
   */
  public void setId(String Id) {
    this.id = Id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    // throw new java.lang.UnsupportedOperationException();
  }

  public LDAPURL getldapurl() {
    return ldapurl;
  }

  public void setLdapUrl(LDAPURL url) {
    ldapurl = url;
  }

  public boolean isNew() {
    return isNew;
  }

  // UTIL Funcs

  protected void removeutil(String field, boolean updatable) {
    myAttrs.remove(field);

    if (updatable) {
      rmAttrs.remove(field);
    }
  }

  protected void setutil(String field, String value) {
    myAttrs.remove(field);
    if (value == null || value.length() == 0)
      value = " ";
    myAttrs.put(field, value);
    updated = true;
  }

  protected void setutil(String field, Vector values) {
    setutil(field, values, false);
  }

  protected void setutil(String field, Vector values, boolean create) {
    myAttrs.remove(field);

    if (values == null || (values.size() == 0 && create)) {
      updated = true;
      return;
    }

    Attribute attr = new BasicAttribute(field);
    for (Enumeration enu = values.elements(); enu.hasMoreElements();) {
      String nextValue = (String) enu.nextElement();
      attr.add(nextValue);
    }

    myAttrs.put(attr);
    updated = true;
  }

  protected void setutil(String field, Object value) {
    myAttrs.remove(field);
    myAttrs.put(field, value);
    updated = true;
  }

  protected String getutil(String field) {
    return getutil(field, myAttrs);
  }

  protected Vector getutil(String field, boolean empty) {
    return getutil(field, myAttrs, empty);
  }

  protected String getutil(String field, Attributes attrs) {
    if (attrs.get(field) == null) {
      return "";
    } else {
      try {
        return attrs.get(field).getAll().next().toString();
      } catch (NamingException e) {
        logger.warn("getUtil(): " + e.getMessage(), e);
        return "";
      }
    }
  }

  protected String fastgetutil(String field, Attributes attrs)
      throws NamingException {
    Attribute values = attrs.get(field);
    if (values == null)
      return "";

    NamingEnumeration e = values.getAll();
    if (e == null || !e.hasMore())
      return "";

    return e.next().toString();
  }

  protected Vector getutil(String field, Attributes attrs, boolean empty) {
    Vector values = null;
    String temp;

    if (empty) {
      values = new Vector();
    }

    if (!(attrs.get(field) == null)) {
      try {
        for (NamingEnumeration enu = attrs.get(field).getAll(); enu.hasMore();) {
          temp = (String) enu.nextElement();

          if (null != temp) {
            values.add(temp);
          }
        }
      } catch (NamingException e) {
        logger.warn("getUtil(): " + e.getMessage(), e);
      }
    }

    return values;
  }

  public Attributes getAttributes(String name) throws NamingException {
    if (!name.equals("")) {
      throw new NameNotFoundException();
    }

    return myAttrs;
  }

  public Attributes getAttributes(Name name) throws NamingException {
    return getAttributes(name.toString());
  }

  public Attributes getAttributes(String name, String[] ids)
      throws NamingException {
    if (!name.equals("")) {
      throw new NameNotFoundException();
    }

    Attributes answer = new BasicAttributes(true);
    Attribute target;

    for (int i = 0; i < ids.length; i++) {
      target = myAttrs.get(ids[i]);
      if (target != null) {
        answer.put(target);
      }
    }
    return answer;
  }

  public Attributes getAttributes(Name name, String[] ids)
      throws NamingException {
    return getAttributes(name.toString(), ids);
  }

  public String toString() {
    return ldapurl.getUrl();
  }

  // not used for this example

  public Object lookup(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public String getNameInNamespace() throws NamingException {
    throw new OperationNotSupportedException();
  }

  public Object lookup(String name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void bind(Name name, Object obj) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void bind(String name, Object obj) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void rebind(Name name, Object obj) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void rebind(String name, Object obj) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void unbind(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void unbind(String name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void rename(Name oldName, Name newName) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void rename(String oldName, String newName) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration list(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration list(String name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration listBindings(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration listBindings(String name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void destroySubcontext(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void destroySubcontext(String name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public Context createSubcontext(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public Context createSubcontext(String name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public Object lookupLink(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public Object lookupLink(String name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NameParser getNameParser(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NameParser getNameParser(String name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public String composeName(String name, String prefix) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public Name composeName(Name name, Name prefix) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public Object addToEnvironment(String propName, Object propVal)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public Object removeFromEnvironment(String propName) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public Hashtable getEnvironment() throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void close() throws NamingException {
    throw new OperationNotSupportedException();
  }

  // -- DirContext

  public void modifyAttributes(Name name, int mod_op, Attributes attrs)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void modifyAttributes(String name, int mod_op, Attributes attrs)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void modifyAttributes(Name name, ModificationItem[] mods)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void modifyAttributes(String name, ModificationItem[] mods)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void bind(Name name, Object obj, Attributes attrs)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void bind(String name, Object obj, Attributes attrs)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void rebind(Name name, Object obj, Attributes attrs)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public void rebind(String name, Object obj, Attributes attrs)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public DirContext createSubcontext(Name name, Attributes attrs)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public DirContext createSubcontext(String name, Attributes attrs)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public DirContext getSchema(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public DirContext getSchema(String name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public DirContext getSchemaClassDefinition(Name name) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public DirContext getSchemaClassDefinition(String name)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration search(Name name, Attributes matchingAttributes,
      String[] attributesToReturn) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration search(String name, Attributes matchingAttributes,
      String[] attributesToReturn) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration search(Name name, Attributes matchingAttributes)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration search(String name, Attributes matchingAttributes)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration search(Name name, String filter, SearchControls cons)
      throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration search(String name, String filter,
      SearchControls cons) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration search(Name name, String filterExpr,
      Object[] filterArgs, SearchControls cons) throws NamingException {
    throw new OperationNotSupportedException();
  }

  public NamingEnumeration search(String name, String filterExpr,
      Object[] filterArgs, SearchControls cons) throws NamingException {
    throw new OperationNotSupportedException();
  }

}
