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

package org.apache.jetspeed.webservices.finance.stockmarket;

import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.*;


class SOAPResponseHandler extends XMLFilterImpl 
{
    String outputNameSpace = null;
    String outputElementName = null;
    String outputPartElementName = null;

    public SOAPResponseHandler(String ns, String en, String pen) {
        outputNameSpace = ns;
        outputElementName = en;
        outputPartElementName = pen;
    }

    boolean inSoapEnv = false;
    boolean inSoapBody = false;
    boolean inOperation = false;
    boolean inResult = false;
    boolean debugParse = false;

    StringBuffer resultBuffer = null;
    XMLFilterImpl resultHandler = null;
    int resultElementStack = 0;

    boolean wasFault = false;
    boolean inFault = false;
    boolean inFaultString = false;
    StringBuffer faultContent = null;

    public void setContentHandler(ContentHandler handler) {
        ((XMLReader)getParent()).setContentHandler(handler);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String uriName = uri+":"+localName;
        if ("http://schemas.xmlsoap.org/soap/envelope/:Fault".equals(uriName)) {
            if (debugParse) System.out.println("Found Fault");
            inFault = true;
        } else if (inFault && "faultstring".equals(localName)) {
            if (debugParse) System.out.println("Found Fault String");
            inFaultString = true;
            faultContent = new StringBuffer();
        } else if ("http://schemas.xmlsoap.org/soap/envelope/:Envelope".equals(uriName)) {
            if (debugParse) System.out.println("Found Envelope");
            inSoapEnv = true;
        } else if (inSoapEnv && "http://schemas.xmlsoap.org/soap/envelope/:Body".equals(uriName)) {
            if (debugParse) System.out.println("Found Body");
            inSoapBody = true;
        } else if (inSoapBody && (outputNameSpace+":"+outputElementName).equals(uriName)) {
            if (debugParse) System.out.println("Found operation: "+localName);
            inOperation = true;
        } else if (inOperation && outputPartElementName.equals(localName)) {
            if (attributes.getValue("href") != null) throw new SAXNotSupportedException("href attributes not supported");
            if (debugParse) System.out.println("Found Part: "+localName);
            if (resultHandler != null) {
                resultHandler.setParent(this);
                getParent().setContentHandler(resultHandler);
            } else {
                resultBuffer = new StringBuffer();
            }
            inResult = true;
        } else if (!inFault) {
            if (debugParse) System.out.println("Found Unknown Element: <"+qName+">    ns:"+uri);
            inResult = false;
            inSoapEnv = false;
            inSoapBody = false;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inResult && outputPartElementName.equals(localName)) {
            inResult = false;
        } else if ("http://schemas.xmlsoap.org/soap/envelope/:Fault".equals(uri+":"+localName)) {
            wasFault = true;
            inFault = false;
        } else if (inFault && "faultstring".equals(localName)) {
            inFaultString = false;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inResult) {
            resultBuffer.append(new String(ch,start,length));
        }
        if (inFault) faultContent.append(new String(ch,start,length));
    }
    public String getResult() {
        if (resultBuffer == null) return null;
        return resultBuffer.toString();
    }
    public void setResultHandler(XMLFilterImpl handler) {
        resultHandler = handler;
    }
    public boolean isFault() {
        return wasFault;
    }
    public String getFaultContent() {
        if (faultContent == null) return "";
        return faultContent.toString();
    }
}
