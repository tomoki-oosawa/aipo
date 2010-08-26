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

/**
    BaseStockQuote implements StockQuote, 
    holding the information for one company's quote.
        
    @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
    @version $Id: BaseStockQuote.java,v 1.2 2004/02/23 03:15:29 jford Exp $
*/

public class BaseStockQuote implements StockQuote
{
    String price = "";
    String name = "";
    String symbol = "";
    String time = "";
    String date = "";
    String high = "";
    String volume = "";
    String change = "";
    String opening = "";
    String low = "";

    public void setPrice(String v) 
    {
        price = v;
    }

    public String getPrice() 
    {
        return price;
    }

    public void setName(String v) 
    {
        name = v;
    }

    public String getName() 
    {
        return name;
    }

    public void setSymbol(String v) 
    {
        symbol = v;
    }

    public String getSymbol() 
    {
        return symbol;
    }

    public void setTime(String v)
    {
        time = v;
    }

    public String getTime() 
    {
        return time;
    }

    public void setDate(String v) 
    {
        date = v;
    }

    public String getDate() 
    {
        return date;
    }

    public void setHigh(String v) 
    {
        high = v;
    }

    public String getHigh() // subliminal
    {
        return high;
    }

    public void setVolume(String v) 
    {
        volume = v;
    }

    public String getVolume() 
    {
        return volume;
    }

    public void setChange(String v) 
    {
        change = v;
    }

    public String getChange() 
    {
        return change;
    }

    public void setOpening(String v) 
    {
        opening = v;
    }

    public String getOpening() 
    {
        return opening;
    }

    public void setLow(String v) 
    {
        low = v;
    }

    public String getLow() 
    {
        return low;
    }

    public String toString() 
    {
        return toXML();
    }

    public String toXML() 
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("        <Price>");
        buffer.append(price);
        buffer.append("</Price>\n");
        buffer.append("        <Name>");
        buffer.append(name);
        buffer.append("</Name>\n");
        buffer.append("        <Symbol>");
        buffer.append(symbol);
        buffer.append("</Symbol>\n");
        buffer.append("        <Time>");
        buffer.append(time);
        buffer.append("</Time>\n");
        buffer.append("        <Date>");
        buffer.append(date);
        buffer.append("</Date>\n");
        buffer.append("        <High>");
        buffer.append(high);
        buffer.append("</High>\n");
        buffer.append("        <Volume>");
        buffer.append(volume);
        buffer.append("</Volume>\n");
        buffer.append("        <Change>");
        buffer.append(change);
        buffer.append("</Change>\n");
        buffer.append("        <Low>");
        buffer.append(low);
        buffer.append("</Low>\n");
        return buffer.toString();
    }

}
