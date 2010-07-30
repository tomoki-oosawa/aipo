package org.apache.jetspeed.util;

import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.portal.PortletSet;

import org.apache.turbine.util.RunData;

import java.util.*;

/**
 * This class is used to handle different mediatype profile
 *
 * @author <a href="mailto:A.Kempf@web.de">Andreas Kempf</a>
 * @version $Id: AutoProfile.java,v1.0 2001/10/31
 */
public class AutoProfile
{
    /** 
     * Load a mediaType specific profile
     * --------------------------------------------------------------------------
     * last modified: 10/31/01
     * Andreas Kempf, Siemens ICM S CP PE, Munich
     */
     /*
    public static Profile loadProfile (RunData rundata, String mediaType) throws Exception
    {
        if ((mediaType != null) && (mediaType.equalsIgnoreCase("wml")))
        {
            Profile runProfile = ((JetspeedRunData)rundata).getProfile();
            if (runProfile != null)
            {
              runProfile.store();
            }                  
            Profile myPro = Profiler.getProfile (rundata, MimeType.WML);
            ((JetspeedRunData)rundata).setProfile (myPro);

            // It is essential that a session object exists!
            rundata.getSession().setAttribute ("customizeType", "wml");
        }
        else if ((mediaType != null) && (mediaType.equalsIgnoreCase("xml")))
        {
            Profile runProfile = ((JetspeedRunData)rundata).getProfile();
            if (runProfile != null)
            {
                runProfile.store();
            }          
            Profile myPro = Profiler.getProfile (rundata, MimeType.XML);
            ((JetspeedRunData)rundata).setProfile (myPro);

            // It is essential that a session object exists!
            rundata.getSession().setAttribute ("customizeType", "xml");
        }
        else
        {
            Profile runProfile = ((JetspeedRunData)rundata).getProfile();
            if (runProfile != null)
            {
              runProfile.store();
            }          
            Profile myPro = Profiler.getProfile (rundata, MimeType.HTML);
            ((JetspeedRunData)rundata).setProfile (myPro);
            
            // It is essential that a session object exists!
            rundata.getSession().setAttribute ("customizeType", "html");
        }

        return ((JetspeedRunData)rundata).getProfile();                
    }
*/

    /** 
     * Load a profile - the mediatype is depending on rundata settings
     * The profile will load if the rundata.profile.mediatype differs from customtype (Session Param)
     * if the param notForce is false, the profile will be loaded wether is already used or not
     * --------------------------------------------------------------------------
     * last modified: 10/31/01
     * Andreas Kempf, Siemens ICM S CP PE, Munich
     */
     /*
    public static Profile doIt (RunData rundata, boolean notForce) throws Exception
    {
      // retrieve current customization mediatype (stored in the user session object)
      HttpSession ses = rundata.getSession ();

      String mediaType = (String) ses.getAttribute ("customizeType");
      
      
      if ((mediaType != null) && (mediaType.equalsIgnoreCase ("wml")))
      {
        // WML Profil nicht laden, wenn bereits vorhanden!
        if (notForce)                
        {
          if (!((JetspeedRunData)rundata).getProfile().getMediaType ().equalsIgnoreCase("wml"))
          {
            return loadProfile (rundata, "wml");
          }
        }
        // WML auf jeden Fall neu laden!
        else
            return loadProfile (rundata, "wml");
      }
      else if ((mediaType != null) && (mediaType.equalsIgnoreCase ("xml")))
      {
          // WML Profil nicht laden, wenn bereits vorhanden!
          if (notForce)                
          {
            if (!((JetspeedRunData)rundata).getProfile().getMediaType ().equalsIgnoreCase("xml"))
            {
              return loadProfile (rundata, "xml");
            }
          }
          // WML auf jeden Fall neu laden!
          else
              return loadProfile (rundata, "xml");


      }
      else if ((mediaType != null) && (mediaType.equalsIgnoreCase ("html")))
      {
        // HTML Profil nicht laden, wenn bereits vorhanden!
        if (notForce)                
        {
          if (!((JetspeedRunData)rundata).getProfile().getMediaType ().equalsIgnoreCase("html"))
          {
            return loadProfile (rundata, "html");
          }
        }
        // HTML auf jeden Fall neu laden!
        else
            return loadProfile (rundata, "html");
      }
      return ((JetspeedRunData)rundata).getProfile();
    }
    */
    
    // Create a list of all used portlets!
    // last modified: 10/31/01
    // Andreas Kempf, Siemens ICM S CP PE, Munich
    // ---------------------------------------------------------------------
    public static List getPortletList (RunData rundata)
    {
      Profile profile = ((JetspeedRunData)rundata).getCustomizedProfile();
      Portlets allPortlets = profile.getDocument().getPortletsById(((PortletSet)((JetspeedRunData)rundata).getCustomized()).getID());

      
      List installed = new ArrayList ();
      Entry iPortlet;


      if (allPortlets != null)
      {
        for (int ii = 0; ii < allPortlets.getEntryCount(); ii++) 
        {
          iPortlet = (Entry) allPortlets.getEntry (ii);
          installed.add (iPortlet);
        }
      }
      
      return installed;
    }
  
}
