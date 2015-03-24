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
package org.apache.jetspeed.util.parser;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.CapabilityMapFactory;
import org.apache.jetspeed.om.registry.MediaTypeEntry;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.util.MimeType;
import org.apache.turbine.util.parser.DefaultParameterParser;

/**
 * DefaultJetspeedParameterParser is a utility object to handle parsing and
 * retrieving the data passed via the GET/POST/PATH_INFO arguments.
 * 
 * <p>
 * NOTE: The name= portion of a name=value pair may be converted to lowercase or
 * uppercase when the object is initialized and when new data is added. This
 * behaviour is determined by the url.case.folding property in
 * TurbineResources.properties. Adding a name/value pair may overwrite existing
 * name=value pairs if the names match:
 * 
 * <pre>
 * ParameterParser pp = data.getParameters();
 * pp.add(&quot;ERROR&quot;, 1);
 * pp.add(&quot;eRrOr&quot;, 2);
 * int result = pp.getInt(&quot;ERROR&quot;);
 * </pre>
 * 
 * In the above example, result is 2.
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke SUGAYA </a>
 */
public class DefaultJetspeedParameterParser extends DefaultParameterParser {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(DefaultJetspeedParameterParser.class.getName());

  public DefaultJetspeedParameterParser() {
    super();
  }

  public DefaultJetspeedParameterParser(String characterEncoding) {
    super(characterEncoding);
  }

  /**
   * Sets the servlet request to be parser. This requires a valid
   * HttpServletRequest object. It will attempt to parse out the
   * GET/POST/PATH_INFO data and store the data into a Hashtable. There are
   * convenience methods for retrieving the data as a number of different
   * datatypes. The PATH_INFO data must be a URLEncoded() string.
   * 
   * <p>
   * To add name/value pairs to this set of parameters, use the
   * <code>add()</code> methods.
   * 
   * @param req
   *          An HttpServletRequest.
   */
  @Override
  public void setRequest(HttpServletRequest req) {
    super.setRequest(req);

    // String enc = JetspeedResources.getString(
    // JetspeedResources.CONTENT_ENCODING_KEY, "US-ASCII");
    CapabilityMap cm =
      CapabilityMapFactory.getCapabilityMap(req.getHeader("User-Agent"));

    MimeType mime = cm.getPreferredType();
    String characterSet =
      JetspeedResources.getString(
        JetspeedResources.CONTENT_ENCODING_KEY,
        "utf-8");
    if (mime != null) {
      // 理由等：同じ Content-Type で異なる CharacterSet を指定できるように，
      // MediaTypeEntry の取得キーとして，cm.getPreferredMediaType() を使用した．
      // MediaTypeEntry media = (MediaTypeEntry) Registry.getEntry(
      // Registry.MEDIA_TYPE, mime.getCode());
      MediaTypeEntry media =
        (MediaTypeEntry) Registry.getEntry(Registry.MEDIA_TYPE, cm
          .getPreferredMediaType());
      if (media != null && media.getCharacterSet() != null) {
        characterSet = media.getCharacterSet();
      }
    }
    setCharacterEncoding(characterSet);

    // String mimeCode = cm.getPreferredType().getCode();
    // if ( mimeCode != null )
    // {
    // MediaTypeEntry media =
    // (MediaTypeEntry)Registry.getEntry(Registry.MEDIA_TYPE, mimeCode);
    // if ( media != null && media.getCharacterSet() != null)
    // {
    // enc = media.getCharacterSet();
    // }
    //
    // }
    // setCharacterEncoding( enc );
  }

  /**
   * Return a String for the given name. If the name does not exist, return
   * null.
   * 
   * @param name
   *          A String with the name.
   * @return A String.
   */
  @Override
  public String getString(String name) {
    String str = super.getString(name);
    if (str == null) {
      return null;
    }

    String header = getRequest().getHeader("Content-type");
    if (header != null && header.startsWith("multipart/form-data")) {
      return str;
    }

    try {
      if (header != null && header.indexOf("charset=") >= 0) {
        // 理由等：
        // form からの受信時，charset が指定されている場合には，
        // Tomcat がその charset で文字コードを変換済みなので，
        // ここでは無処理の文字列を返す．
        return str;
      } else {
        return new String(str.getBytes("8859_1"), getCharacterEncoding());
      }
    } catch (Exception e) {
      logger.warn(
        "DefaultJetspeedParameterParser: Exception: " + e.toString(),
        e);
      return str;
    }

  }

}
