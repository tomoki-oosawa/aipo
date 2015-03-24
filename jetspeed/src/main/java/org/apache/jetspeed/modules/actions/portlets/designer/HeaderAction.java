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
package org.apache.jetspeed.modules.actions.portlets.designer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.upload.FileItem;
import org.apache.velocity.context.Context;

/**
 * Header Action
 * 
 * @author <a href="mailto:jlim@gluecode.com">Jonas Lim</a>
 * @version $Id: HeaderAction.java,v 1.2 2004/03/22 22:26:58 taylor Exp $
 */
public class HeaderAction extends VelocityPortletAction
{
    private static final JetspeedLogger log = JetspeedLogFactoryService.getLogger(HeaderAction.class.getName());
    private static final String IMAGES_DIRECTORY = "images/designer/";
    private static final String DEFAULT_CSS = "css/default.css";

    private String fileTypes[] =
    { "image/jpg", 
      "image/gif", 
      "image/jpeg",
       "application/x-shockwave-flash",
       "image/png",
       "image/x-png"
    };
    
    
    protected void buildNormalContext(VelocityPortlet portlet, Context context,
            RunData rundata) throws Exception
    {

    }

    public void doUpload(RunData rundata, Context context)
    {
        Hashtable ht = new Hashtable();

        boolean setLogo = rundata.getParameters().getBoolean("setLogo");
        boolean setBgImage = rundata.getParameters().getBoolean("setBgImage");
        boolean setBgColor = rundata.getParameters().getBoolean("setBgColor");
        boolean setFontFace = rundata.getParameters().getBoolean("setFontFace");
        boolean setFontSize = rundata.getParameters().getBoolean("setFontSize");
        boolean setFontColor = rundata.getParameters().getBoolean(
                "setFontColor");
        boolean setTitle = rundata.getParameters().getBoolean("setTitle");

        FileItem fileLogo = null;
        FileItem fileBgImage = null;
        String bgColor = "";
        String fontFace = "";
        String fontSize = "";
        String fontColor = "";
        String bgImage = "";
        String title = "";

        if (setLogo)
        {
            fileLogo = rundata.getParameters().getFileItem("imgLogo");
        }

        if (setBgImage)
        {
            String sampleBg = rundata.getParameters()
                    .getString("sampleBgImage").trim();
            if (sampleBg != null && sampleBg.length() != 0)
            {
                bgImage = IMAGES_DIRECTORY + sampleBg;
            } else
            {
                fileBgImage = rundata.getParameters().getFileItem("bgImage");
            }
        }

        if (setBgColor)
        {
            bgColor = rundata.getParameters().getString("bgColor").trim();
        }
        if (setFontFace)
        {
            fontFace = rundata.getParameters().getString("fontFace").trim();
        }
        if (setFontSize)
        {
            fontSize = rundata.getParameters().getString("fontSize").trim();
        }
        if (setFontColor)
        {
            fontColor = rundata.getParameters().getString("fontColor").trim();
        }
        if (setTitle)
        {
            title = StringEscapeUtils.escapeHtml(rundata.getParameters().getString("portalTitle").trim());
        }

        log.info("fontColor : " + fontColor);

        String message = "";
        try
        {
            String logo = "";

            String slash = System.getProperty("file.separator");
            String imagePath = FormatPath.normalizeDirectoryPath(rundata
                    .getServletContext().getRealPath("/" + IMAGES_DIRECTORY));

            if (fileLogo != null)
            {
                File file = new File(fileLogo.getFileName());
                logo = file.getName();

                int index = logo.lastIndexOf("\\");
                int index2 = logo.lastIndexOf("//");

                if (index > 0)
                {
                    logo = logo.substring(index + 1);
                }

                if (index2 > 0)
                {
                    logo = logo.substring(index2 + 1);
                }

                File f = new File(imagePath + logo);
                if (f.exists()) 
                {
                    f.delete();//nik
                }
                FileUploader fu = new FileUploader();
                boolean hasUploaded = fu.upload(fileLogo, imagePath, fileTypes);
                //String filename = fu.getFilename(fileLogo, imagePath,
                // fileTypes);

                if (hasUploaded == true)
                {
                    logo = IMAGES_DIRECTORY + logo;
                } else
                {
                    context
                            .put(
                                    "logoStatus",
                                    "Error occurred while uploading "
                                            + logo
                                            + ". Only gif, jpg, and jpeg image files can be uploaded. ");
                    logo = "";
                }

                //context.put("logoFile", filename);

            }

            if (fileBgImage != null)
            {
                File file1 = new File(fileBgImage.getFileName());
                bgImage = file1.getName();

                int index = bgImage.lastIndexOf("\\");
                int index2 = bgImage.lastIndexOf("//");

                if (index > 0)
                {
                    bgImage = bgImage.substring(index + 1);
                }

                if (index2 > 0)
                {
                    bgImage = bgImage.substring(index2 + 1);
                }

                File f = new File(imagePath + bgImage);
                if (f.exists()) f.delete();//nik
                FileUploader fu = new FileUploader();
                boolean hasUploaded = fu.upload(fileBgImage, imagePath,
                        fileTypes);
                log.info("hasUploaded : " + hasUploaded);

                if (hasUploaded == true)
                    bgImage = IMAGES_DIRECTORY + bgImage;
                else
                {
                    context
                            .put(
                                    "bgStatus",
                                    "Error while uploading "
                                            + bgImage
                                            + ". Only gif, jpg, and jpeg image files can be uploaded. ");
                    bgImage = "";
                }

            }

            boolean hasColor = (bgColor == null || bgColor.length() < 1);
            boolean hasFontFace = (fontFace == null || fontFace.length() < 1);
            boolean hasFontSize = (fontSize == null || fontSize.length() < 1);
            boolean hasFontColor = (fontColor == null || fontColor.length() < 1);

            if (hasColor)
            {
                bgColor = "white";
            }
            if (hasFontFace)
            {
                fontFace = "verdana";
            }
            if (hasFontSize)
            {
                fontSize = "12";
            }
            if (hasFontColor)
            {
                fontColor = "black";
            }

            ht.put("logo", logo);
            ht.put("BgImage", bgImage);
            ht.put("BgColor", bgColor);
            ht.put("FontFace", fontFace);
            ht.put("FontSize", fontSize);
            ht.put("FontColor", fontColor);
            ht.put("PortalTitle", title);
            ht.put("SetLogo", String.valueOf(setLogo));
            ht.put("SetBgImage", String.valueOf(setBgImage));
            ht.put("SetBgColor", String.valueOf(setBgColor));
            ht.put("SetFontFace", String.valueOf(setFontFace));
            ht.put("SetFontSize", String.valueOf(setFontSize));
            ht.put("SetFontColor", String.valueOf(setFontColor));
            ht.put("SetTitle", String.valueOf(setTitle));

            editHeader(rundata, context, ht);

        } catch (Exception ee)
        {
            message = ee.getMessage();
        }
    }

    public void editHeader(RunData rundata, Context context, Hashtable ht)
            throws Exception
    {
        try
        {
            String logo = (String) ht.get("logo");
            String bgImage = (String) ht.get("BgImage");
            String bgColor = (String) ht.get("BgColor");
            String fontSize = (String) ht.get("FontSize");
            String fontColor = (String) ht.get("FontColor");
            String title = (String) ht.get("PortalTitle");

            boolean setLogo = Boolean.valueOf((String) ht.get("SetLogo")).booleanValue();
            boolean setBgImage = Boolean.valueOf((String) ht.get("SetBgImage"))
                    .booleanValue();
            boolean setBgColor = Boolean.valueOf((String) ht.get("SetBgColor"))
                    .booleanValue();
            boolean setFontSize = Boolean.valueOf((String) ht.get("SetFontSize"))
                    .booleanValue();
            boolean setFontColor = Boolean.valueOf((String) ht.get("SetFontColor"))
                    .booleanValue();
            boolean setPortalTitle = Boolean.valueOf((String) ht.get("SetTitle"))
                    .booleanValue();

            String DEFAULT_ROOT = File.separator + "WEB-INF" + File.separator
                    + "conf" + File.separator;
            String root = rundata.getServletConfig().getServletContext()
                    .getRealPath(DEFAULT_ROOT)
                    + File.separator;

            Properties prop = new Properties();
            prop.clear();
            prop.load(new FileInputStream(root + "JetspeedResources.properties"));

            TurbineResources trProp = (TurbineResources) TurbineResources
                    .getResources(root + "JetspeedResources.properties");

            int counter = 0;
            if (setBgImage && !setBgColor && (counter == 0))
            {
                setBgColor = true;
                bgColor = "FFFFFF";
                counter = 1;
            }
            if (!setBgImage && setBgColor && (counter == 0))
            {
                setBgImage = true;
                bgImage = "";
                counter = 1;
            }
            if (setBgImage && setBgColor && (counter == 0))
            {
                setBgColor = true;
                bgColor = "FFFFFF";
                counter = 1;
            }

            if (setLogo)
            {
                TurbineResources.setProperty("topnav.logo.file", logo);
                prop.setProperty("topnav.logo.file", logo);
            }
            if (setBgImage)
            {
                TurbineResources.setProperty("topnav.bg.image", bgImage);
                prop.setProperty("topnav.bg.image", bgImage);
            }
            if (setBgColor)
            {
                TurbineResources.setProperty("topnav.bg.color", bgColor);
                prop.setProperty("topnav.bg.color", bgColor);
            }
            if (setFontSize)
            {
                TurbineResources.setProperty("topnav.font.size", fontSize);
                prop.setProperty("topnav.font.size", fontSize);
            }
            if (setFontColor)
            {
                TurbineResources.setProperty("topnav.font.color", fontColor);
                prop.setProperty("topnav.font.color", fontColor);
            }
            if (setPortalTitle)
            {
                TurbineResources.setProperty("portal.title", title);
                prop.setProperty("portal.title", title);
            }
            	
            FileOutputStream stream = new FileOutputStream(root + "JetspeedResources.properties");
            prop.store(stream, "topnav.logo.file");
            prop.store(stream, "topnav.bg.image");
            prop.store(stream, "topnav.bg.color");
            prop.store(stream, "topnav.font.size");
            prop.store(stream, "topnav.font.color");
            stream.close();
            
            String logoJR = prop.getProperty("topnav.logo.file");
            String bgImageJR = prop.getProperty("topnav.bg.image");
            String bgColorJR = prop.getProperty("topnav.bg.color");
            String fontSizeJR = prop.getProperty("topnav.font.size");
            String fontColorJR = prop.getProperty("topnav.font.color");

            log.info("new Logo : " + logoJR);
            log.info("new bgImageJR : " + bgImageJR);
            log.info("new bgColorJR : " + bgColorJR);
            log.info("new fontSizeJR : " + fontSizeJR);
            log.info("new fontColorJR : " + fontColorJR);

            prop = null;
            System.gc();

            context.put("settingStatus", "Successfully changed settings.");
        } catch (Exception e)
        {
            context.put("settingStatus",
                    "Error occurred while changing settings.");
            log.error(e);
        }
    }

    public void doDefault(RunData rundata, Context context) throws Exception
    {
        try
        {
            String logo = IMAGES_DIRECTORY + "jetspeed-logo.gif";
            String bgImage = "";
            String fontSize = "10";
            String title = "Jakarta Jetspeed";

            String DEFAULT_ROOT = File.separator + "WEB-INF" + File.separator
                    + "conf" + File.separator;
            String root = rundata.getServletConfig().getServletContext()
                    .getRealPath(DEFAULT_ROOT)
                    + File.separator;

            Properties prop = new Properties();
            prop
                    .load(new FileInputStream(root
                            + "JetspeedResources.properties"));
            prop.clone();

            TurbineResources trProp = (TurbineResources) TurbineResources
                    .getResources(root + "JetspeedResources.properties");

            TurbineResources.setProperty("portal.title", title);
            prop.setProperty("portal.title", title);
            TurbineResources.setProperty("topnav.logo.file", logo);
            prop.setProperty("topnav.logo.file", logo);
            TurbineResources.setProperty("topnav.bg.image","");
            prop.setProperty("topnav.bg.image","");
            TurbineResources.setProperty("topnav.bg.color", "");
            prop.setProperty("topnav.bg.color", ""); 
      
            TurbineResources.setProperty("topnav.font.size", ""); 
            prop.setProperty("topnav.font.size", "");
      
            TurbineResources.setProperty("topnav.font.color", ""); 
	    prop.setProperty("topnav.font.color", "");  


            FileOutputStream stream = new FileOutputStream(root + "JetspeedResources.properties");
                        
            prop.save(stream, "topnav.logo.file");
            
            prop.save(stream, "portal.title");
            prop.save(stream, "topnav.bg.image");
            prop.save(stream, "topnav.bg.color");
            prop.save(stream, "ptopnav.font.size");
            prop.save(stream, "ptopnav.font.size");
            stream.close();
            
            context.put("settingStatus",
                    "Successfully changed to default settings.");
        } catch (Exception e)
        {
            context.put("settingStatus",
                    "Error occurred while changing to default settings. ");
            log.error(e);
        }
    }
    
    
    // Methods used by the portlet designer. Not yet implemented 
    /*
    public void doScheme(RunData rundata, Context context)
    {
        Registry reg = org.apache.jetspeed.services.Registry
                .get("PortletControl");

        //parameters for border design and skin
        String portletTheme = rundata.getParameters().getString("PortletTheme");
        String tabTheme = rundata.getParameters().getString("TabTheme");
        //conditions for changing portlet settings
        boolean setPBgImage = rundata.getParameters().getBoolean(
                "setPortletBgImage");
        boolean setPBgColor = rundata.getParameters().getBoolean(
                "setPortletBgColor");
        boolean setPFontFace = rundata.getParameters().getBoolean(
                "setPortletFontFace");
        boolean setPFontColor = rundata.getParameters().getBoolean(
                "setPortletFontColor");

        try
        {
            if (setPBgImage || setPBgColor || setPFontFace || setPFontColor)
            {
                Hashtable ht = new Hashtable();
                ht.put("setPBgImage", Boolean.valueOf(setPBgImage));
                ht.put("setPBgColor", Boolean.valueOf(setPBgColor));
                ht.put("setPFontFace", Boolean.valueOf(setPFontFace));
                ht.put("setPFontColor", Boolean.valueOf(setPFontColor));

                editPortlet(rundata, context, ht);
            }

            if (!portletTheme.equalsIgnoreCase("none"))
            {
                BasePortletControlEntry bpce = (BasePortletControlEntry) reg
                        .getEntry("TitlePortletControl");
                Parameter paramName = bpce.getParameter("theme");
                String theme = paramName.getValue();
                log.info("portletTheme : " + theme);
                log.info("!portletTheme.equals('none') : "
                        + !portletTheme.equals("none"));
                log.info("new portletTheme : " + portletTheme);

                bpce.removeParameter("theme");
                bpce.addParameter("theme", portletTheme);
            }

            if (!tabTheme.equalsIgnoreCase("none"))
            {
                BasePortletControlEntry bpce = (BasePortletControlEntry) reg
                        .getEntry("TabControl");
                Parameter paramName = bpce.getParameter("theme");
                String theme = paramName.getValue();
                log.info("tabTheme : " + theme);
                log.info("!tabTheme.equals('none') : "
                        + !tabTheme.equals("none"));
                log.info("new tabTheme : " + tabTheme);

                bpce.removeParameter("theme");
                bpce.addParameter("theme", tabTheme);

                bpce = (BasePortletControlEntry) reg
                        .getEntry("PanedPortletControl");
                paramName = bpce.getParameter("theme");
                theme = paramName.getValue();
                log.info("tabtheme : " + theme);
                log.info("new tabTheme : " + tabTheme);

                bpce.removeParameter("theme");
                bpce.addParameter("theme", tabTheme);

                //setting for menu pane.
                String menuTheme = "";
                if (tabTheme.indexOf("blue") != -1)
                        menuTheme = "jetspeed-menu_blue.vm";
                if (tabTheme.indexOf("gray") != -1)
                        menuTheme = "jetspeed-menu_gray.vm";
                if (tabTheme.indexOf("green") != -1)
                        menuTheme = "jetspeed-menu_green.vm";
                if (tabTheme.indexOf("red") != -1)
                        menuTheme = "jetspeed-menu_red.vm";
                if (tabTheme.equals("jetspeed-tab.vm"))
                        menuTheme = "jetspeed-menu.vm";

                bpce = (BasePortletControlEntry) reg.getEntry("MenuControl");
                paramName = bpce.getParameter("theme");
                theme = paramName.getValue();

                bpce.removeParameter("theme");
                bpce.addParameter("theme", menuTheme);

            }
            context.put("settingPStatus",
                    "Successfully changed to new settings.");
        } catch (Exception e)
        {
            context.put("settingPStatus",
                    "Error occurred while changing settings.");
            log.error(e);
        }
    }

    public void editPortlet(RunData rundata, Context context, Hashtable ht)
    {
        try
        {
            boolean setPBgImage = false;
            boolean setPBgColor = false;
            boolean setPFontFace = false;
            boolean setPFontColor = false;

            if (ht != null || ht.size() > 0)
            {
                setPBgImage = ((Boolean) ht.get("setPBgImage")).booleanValue();
                setPBgColor = ((Boolean) ht.get("setPBgColor")).booleanValue();
                setPFontFace = ((Boolean) ht.get("setPFontFace"))
                        .booleanValue();
                setPFontColor = ((Boolean) ht.get("setPFontColor"))
                        .booleanValue();
            }

            FileItem filePBgImage = null;
            String pBgColor = "";
            String pFontFace = "";
            String pFontSize = "";
            String pFontColor = "";

            String pBgImage = "";

            if (setPBgImage)
            {
                String sampleBg = rundata.getParameters().getString(
                        "samplePBgImage").trim();
                log.info("samplePBgImage : " + sampleBg);

                if (sampleBg != null && sampleBg.length() != 0)
                {
                    pBgImage = IMAGES_DIRECTORY + sampleBg;
                } else
                {
                    filePBgImage = rundata.getParameters().getFileItem(
                            "portletBgImage");
                }
            }

            if (setPBgColor)
            {
                pBgColor = rundata.getParameters().getString("portletBgColor")
                        .trim();
            }
            if (setPFontFace)
            {
                pFontFace = rundata.getParameters()
                        .getString("portletFontFace").trim();
            }
            if (setPFontColor)
            {
                pFontColor = rundata.getParameters().getString(
                        "portletFontColor").trim();
            }

            String slash = System.getProperty("file.separator");
            String imagePath = FormatPath.normalizeDirectoryPath(rundata.getServletContext().getRealPath("/" + IMAGES_DIRECTORY));
                    
            String cssFullPath = FormatPath.normalizeDirectoryPath(rundata.getServletContext().getRealPath("/" + DEFAULT_CSS));

            if (filePBgImage != null)
            {
                log.info("Entering uploadBgImage");
                File file1 = new File(filePBgImage.getFileName());
                pBgImage = file1.getName();

                int index = pBgImage.lastIndexOf("\\");
                int index2 = pBgImage.lastIndexOf("//");

                if (index > 0)
                {
                    pBgImage = pBgImage.substring(index + 1);
                }

                if (index2 > 0)
                {
                    pBgImage = pBgImage.substring(index2 + 1);
                }

                log.info("File2 : " + pBgImage);

                //filePBgImage.write(imagePath + pBgImage);
                FileUploader fu = new FileUploader();
                boolean hasUploaded = fu.upload(filePBgImage, imagePath,
                        fileTypes);

                if (hasUploaded == true)
                    pBgImage = IMAGES_DIRECTORY + pBgImage;
                else
                {
                    context
                            .put(
                                    "bgPStatus",
                                    "Error occurred while uploading "
                                            + pBgImage
                                            + ". Only gif, jpg, and jpeg image files can be uploaded. ");
                    pBgImage = "";
                }

                log.info("writing : " + imagePath + pBgImage);
            }

            boolean hasPColor = (pBgColor == null || pBgColor.length() < 1);
            boolean hasPFontFace = (pFontFace == null || pFontFace.length() < 1);
            boolean hasPFontSize = (pFontSize == null || pFontSize.length() < 1);
            boolean hasPFontColor = (pFontColor == null || pFontColor.length() < 1);

            if (hasPColor)
            {
                pBgColor = "white";
            }
            if (hasPFontFace)
            {
                pFontFace = "verdana";
            }
            if (hasPFontSize)
            {
                pFontSize = "12";
            }
            if (hasPFontColor)
            {
                pFontColor = "000000";
            }

            String DEFAULT_ROOT = File.separator + "WEB-INF" + File.separator
                    + "conf" + File.separator;
            String root = rundata.getServletConfig().getServletContext()
                    .getRealPath(DEFAULT_ROOT)
                    + File.separator;

            Properties prop = new Properties();
            prop.clear();
            prop
                    .load(new FileInputStream(root
                            + "JetspeedResources.properties"));

            TurbineResources trProp = (TurbineResources) TurbineResources
                    .getResources(root + "JetspeedResources.properties");

            int counter = 0;
            if (setPBgImage && !setPBgColor && (counter == 0))
            {
                setPBgColor = true;
                pBgColor = "FFFFFF";
                counter = 1;
            }
            if (!setPBgImage && setPBgColor && (counter == 0))
            {
                setPBgImage = true;
                pBgImage = "";
                counter = 1;
            }
            if (setPBgImage && setPBgColor && (counter == 0))
            {
                setPBgColor = true;
                pBgColor = "FFFFFF";
                counter = 1;
            }

            if (setPBgImage)
            {
                trProp.setProperty("portlet.bg.image", pBgImage);
                prop.setProperty("portlet.bg.image", pBgImage);
            }
            if (setPBgColor)
            {
                trProp.setProperty("portlet.bg.color", pBgColor);
                prop.setProperty("portlet.bg.color", pBgColor);
            }
            if (setPFontFace)
            {
                trProp.setProperty("portlet.font.face", pFontFace);
                prop.setProperty("portlet.font.face", pFontFace);
            }
            if (setPFontColor)
            {
                trProp.setProperty("portlet.font.color", pFontColor);
                prop.setProperty("portlet.font.color", pFontColor);
            }

            FileOutputStream stream = new FileOutputStream(root + "JetspeedResources.properties");                      
            prop.save(stream, "portlet.logo.file");
            prop.save(stream, "portlet.bg.image");
            prop.save(stream, "portlet.bg.color");
            prop.save(stream, "portlet.font.face");
            prop.save(stream, "portlet.font.color");
            stream.close();
            String pbgImageJR = prop.getProperty("portlet.bg.image");
            String pbgColorJR = prop.getProperty("portlet.bg.color");
            String pfontFaceJR = prop.getProperty("portlet.font.face");
            //String pfontSizeJR = prop.getProperty("portlet.font.size");
            String pfontColorJR = prop.getProperty("portlet.font.color");

            CSSTemplate.buildTemplate(cssFullPath, pfontFaceJR, pfontColorJR);

            log.info("new bgImageJR : " + pbgImageJR);
            log.info("new bgColorJR : " + pbgColorJR);
            log.info("new fontFaceJR : " + pfontFaceJR);
            log.info("new fontColorJR : " + pfontColorJR);

            context.put("settingPStatus",
                    "Successfully change to new settings.");

        } catch (Exception e)
        {
            context.put("settingPStatus",
                    "Error occured while changing settings.");
            log.error(e);
        }
    }

    public void doPdefault(RunData rundata, Context context)
    {
        try
        {
            //change to default for tab and menu theme
            String portletTheme = "jetspeed.vm";
            String tabTheme = "jetspeed-tab.vm";
            String menuTheme = "jetspeed-menu.vm";

            Registry reg = org.apache.jetspeed.services.Registry
                    .get("PortletControl");

            //for portletTheme
            BasePortletControlEntry bpce = (BasePortletControlEntry) reg
                    .getEntry("TitlePortletControl");
            Parameter paramName = bpce.getParameter("theme");
            String theme = paramName.getValue();
            log.info("portletTheme : " + theme);
            log.info("!portletTheme.equals('none') : "
                    + !portletTheme.equals("none"));
            log.info("new portletTheme : " + portletTheme);

            bpce.removeParameter("theme");
            bpce.addParameter("theme", portletTheme);

            //for tabTheme
            bpce = (BasePortletControlEntry) reg.getEntry("TabControl");
            paramName = bpce.getParameter("theme");
            theme = paramName.getValue();
            log.info("tabTheme : " + theme);
            log.info("!tabTheme.equals('none') : " + !tabTheme.equals("none"));
            log.info("new tabTheme : " + tabTheme);

            bpce.removeParameter("theme");
            bpce.addParameter("theme", tabTheme);

            bpce = (BasePortletControlEntry) reg
                    .getEntry("PanedPortletControl");
            paramName = bpce.getParameter("theme");
            theme = paramName.getValue();
            log.info("tabtheme : " + theme);
            log.info("new tabTheme : " + tabTheme);

            bpce.removeParameter("theme");
            bpce.addParameter("theme", tabTheme);

            //for menuTheme
            bpce = (BasePortletControlEntry) reg.getEntry("MenuControl");
            paramName = bpce.getParameter("theme");
            theme = paramName.getValue();

            bpce.removeParameter("theme");
            bpce.addParameter("theme", menuTheme);

            //change to default for font face and size, background,
            String slash = System.getProperty("file.separator");
            String cssFullPath = FormatPath.normalizeDirectoryPath(rundata.getServletContext().getRealPath("/" + DEFAULT_CSS));
            
            CSSTemplate.buildTemplate(cssFullPath, "verdana", "000000");

            String bgImage = "";
            String bgColor = "white";

            String DEFAULT_ROOT = File.separator + "WEB-INF" + File.separator
                    + "conf" + File.separator;
            String root = rundata.getServletConfig().getServletContext()
                    .getRealPath(DEFAULT_ROOT)
                    + File.separator;

            Properties prop = new Properties();
            prop.clear();
            
            FileInputStream istream = new FileInputStream(root + "JetspeedResources.properties");
                        
            prop.load(istream);
            istream.close();
            
            TurbineResources trProp = (TurbineResources) TurbineResources
                    .getResources(root + "JetspeedResources.properties");

            trProp.setProperty("portlet.bg.image", bgImage);
            prop.setProperty("portlet.bg.image", bgImage);
            trProp.setProperty("portlet.bg.color", bgColor);
            prop.setProperty("portlet.bg.color", bgColor);
            trProp.setProperty("portlet.font.color", "000000");
            prop.setProperty("portlet.font.color", "000000");
            trProp.setProperty("portlet.font.face", "verdana");
            prop.setProperty("portlet.font.face", "verdana");

            FileOutputStream stream = new FileOutputStream(root + "JetspeedResources.properties");
            
            prop.store(stream, "portlet.bg.image");
            prop.store(stream, "portlet.bg.color");
            stream.close();
            context.put("settingPStatus",
                    "Successfully changed to default settings.");
        } catch (Exception e)
        {
            context.put("settingPStatus",
                    "Error occurred while changing to default settings.");
            log.error(e);
        }
    }
   */ 

}

