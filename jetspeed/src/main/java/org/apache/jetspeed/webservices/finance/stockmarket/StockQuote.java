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
    StockQuote holds the information for one company's quote.
        
    @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
    @version $Id: StockQuote.java,v 1.2 2004/02/23 03:15:29 jford Exp $
*/

public interface StockQuote
{
    String getName();

    void setName( String s );

    String getSymbol();

    void setSymbol( String s );

    String getPrice();

    void setPrice( String s );

    String getDate();

    void setDate( String s );

    String getTime();

    void setTime( String s );

    String getChange();
    
    void setChange( String s );

    String getOpening();
    
    void setOpening( String s );

    String getHigh();  // subliminal

    void setHigh( String s );

    String getLow();

    void setLow( String s );

    String getVolume();

    void setVolume( String s );

}