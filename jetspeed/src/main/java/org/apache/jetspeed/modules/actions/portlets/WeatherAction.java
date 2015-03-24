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

package org.apache.jetspeed.modules.actions.portlets;

// Jetspeed stuff
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction;
import org.apache.jetspeed.util.PortletConfigState;

// Turbine stuff
import org.apache.turbine.util.RunData;

// Velocity Stuff
import org.apache.velocity.context.Context;

//Java stuff
import java.util.StringTokenizer;

/**
 * WeatherAction portlet uses WeatherUnderground's weather condition
 * stickers/banners to build the portlet view.
 * 
 * <p> Donated by Community Grids Java Source Package</p>
 * <p> Provides supporting classess for portal environments.</p>
 * 
 * @author <a href="mailto:obalsoy@indiana.edu">Ozgur Balsoy</a>
 * @version $Id: WeatherAction.java,v 1.9 2004/02/23 02:56:58 jford Exp $
 */
public class WeatherAction extends VelocityPortletAction
{
    public static final String WEATHER_CITY_INFO = "weather_city_info";
    public static final String WEATHER_STATE = "weather_state";
    public static final String WEATHER_CITY = "weather_city";
    public static final String WEATHER_STATION = "weather_station";
    public static final String WEATHER_STYLE = "weather_style";

    /**
     * Nothing specific for maximized view.
     * 
     * @param portlet
     * @param context
     * @param rundata
     * @see VelocityPortletAction#buildMaximizedContext
     */
    protected void buildMaximizedContext( VelocityPortlet portlet,
                                          Context context,
                                          RunData rundata )
    {
        buildNormalContext( portlet, context, rundata);
    }

    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     * 
     * @param portlet
     * @param context
     * @param rundata
     * @see VelocityPortletAction#buildNormalContext
     */
    protected void buildNormalContext( VelocityPortlet portlet,
                                       Context context,
                                       RunData rundata )
    {

        String cityInfo = PortletConfigState.getParameter(portlet, rundata, WEATHER_CITY_INFO, null);
        //if (cityInfo == null)
        //{
            String city = portlet.getPortletConfig().getInitParameter(WEATHER_CITY);
            String state = portlet.getPortletConfig().getInitParameter(WEATHER_STATE);
            String station = portlet.getPortletConfig().getInitParameter(WEATHER_STATION);
            cityInfo = getCityInfo(city, state, station);            
        //}
        context.put(WEATHER_CITY_INFO, cityInfo);
        //PortletConfigState.setInstanceParameter(portlet, rundata, WEATHER_CITY_INFO, cityInfo);

        String style = PortletConfigState.getParameter(portlet, rundata, WEATHER_STYLE, "infobox");
        context.put(WEATHER_STYLE,style);
    }

    /**
     * Builds the path for US cities. The format is US/ST/City.html, i.e.
     * for New York City, the city path is US/NY/New_York
     * 
     * @param city
     * @param state
     * @return 
     */
    private String getUSInfo(String city, String state)
    {
        city = city.trim().toLowerCase()+" ";
        if (city.indexOf(" ")>0)
        {
            String newCity = "";
            StringTokenizer st = new StringTokenizer(city, " ");
            while (st.hasMoreTokens())
            {
                String token = st.nextToken();
                newCity = newCity + token.substring(0,1).toUpperCase() +
                          token.substring(1) + "_";
            }
            city = newCity.substring(0, newCity.length()-1); // remove last '_'
        }
        state = state.toUpperCase();
        return "US/" + state + "/" + city;
    }

    /**
     * Builds the city path for US or other world cities. For world cities,
     * the city path is global/station/station_number, i.e.
     * for Istanbul, Turkey, it is global/stations/17060. The station numbers
     * need to be obtained from the Weather Underground's site.
     * 
     * @param city
     * @param state
     * @param station
     * @return 
     */
    private String getCityInfo(String city, String state, String station)
    {
        String cityInfo = null;
        if (city!=null && state !=null && !city.equals("") && !state.equals(""))
        {
            cityInfo = getUSInfo(city, state);
        }
        else if (station != null && !station.equals(""))
        {
            cityInfo = "global/stations/" + station;
        }
        return cityInfo;
    }

    /**
     * 
     * @param data
     * @param context
     * @see VelocityPortletAction#doCancel
     */
    public void doCancel(RunData data, Context context)
    {
        VelocityPortlet portlet = (VelocityPortlet) context.get("portlet");
        buildNormalContext(portlet, context, data);
    }

}

