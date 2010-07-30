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

package org.apache.jetspeed.modules.actions.portlets;

// Turbine stuff
import org.apache.turbine.util.RunData;

// Jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

// Java stuff
import java.util.Hashtable;
import java.util.Iterator;
import java.io.File;

import javax.mail.Session;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;

/**
 * This action sets up the template context for retrieving stock quotes.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: QuestionnaireAction.java,v 1.7 2004/02/23 02:56:58 jford Exp $ 
 */

public class QuestionnaireAction extends JspPortletAction
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(QuestionnaireAction.class.getName());     
    
    /**
     * Build the normal state content for this portlet.
     *
     * @param portlet The jsp-based portlet that is being built.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildNormalContext(Portlet portlet, RunData rundata)
    {
        PortletEntry entry = (PortletEntry) Registry.getEntry(Registry.PORTLET, portlet.getName());
        Iterator i = entry.getParameterNames();
        Hashtable qa = new Hashtable();

        while (i.hasNext())
        {
            String name = (String) i.next();
            Parameter param = entry.getParameter(name);
            if (param.isHidden() == false)
            {
                String title = param.getTitle();
                String value = portlet.getPortletConfig().getInitParameter(name);
                qa.put(title, value);
            }
        }

        rundata.getRequest().setAttribute("questions", qa);

        // After successful send, the user may or may not click the Continue button so
        // reset to default template here
        if (rundata.getRequest().getAttribute("email") == null)
        {
            //this.setTemplate(rundata, portlet, null);
            resetTemplate(rundata);
        }

    }

    /**
     * Continue event handler.
     *
     * @param portlet The jsp-based portlet that is being built.
     * @param rundata The turbine rundata context for this request.
     */
    public void doContinue(RunData rundata, Portlet portlet)
    {
        // this.setTemplate(rundata, portlet, null);
        resetTemplate(rundata);
    }

    /**
     * Sort the quotes.
     *
     * @param portlet The jsp-based portlet that is being built.
     * @param rundata The turbine rundata context for this request.
     */
    public void doEmail(RunData rundata, Portlet portlet)
    {
        StringBuffer emailBody = new StringBuffer();
        PortletEntry entry = (PortletEntry) Registry.getEntry(Registry.PORTLET, portlet.getName());
        Iterator i = entry.getParameterNames();

        while (i.hasNext())
        {
            String name = (String) i.next();
            Parameter param = entry.getParameter(name);
            if (param.isHidden() == false)
            {
                String title = param.getTitle();
                String value = portlet.getPortletConfig().getInitParameter(name);
                value = value == null || value.length() == 0 ? "NOT PROVIDED" : value;
                emailBody.append(title);
                emailBody.append(" ===> ");
                emailBody.append(value);
                emailBody.append("\n\n");

            }
        }

        String emailSmtp = JetspeedResources.getString(JetspeedResources.MAIL_SERVER_KEY);
        String emailFrom = JetspeedResources.getString("mail.support", "david@bluesunrise.com");
        String emailTo = rundata.getParameters().getString("emailTo", "jetspeed-dev@jakarta.apache.org");
        String emailAttachment = rundata.getRequest().getParameter("emailAttachment");
        try
        {
            String emailText = emailBody.toString();

            // Create the JavaMail session
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", emailSmtp);
            Session emailSession = Session.getInstance(properties, null);

            // Construct the message
            MimeMessage message = new MimeMessage(emailSession);

            // Set the from address
            Address fromAddress = new InternetAddress(emailFrom);
            message.setFrom(fromAddress);

            // Parse and set the recipient addresses
            Address[] toAddresses = InternetAddress.parse(emailTo);
            message.setRecipients(Message.RecipientType.TO, toAddresses);

            // Set the subject and text
            message.setSubject("Jetspeed Questionnaire from " + rundata.getUser().getEmail());
            message.setText(emailText);

            // Attach file with message
            if (emailAttachment != null)
            {
                File file = new File(emailAttachment);
                if (file.exists())
                {
                    // create and fill the first message part
                    MimeBodyPart mbp1 = new MimeBodyPart();
                    mbp1.setText(emailText);

                    // create the second message part
                    MimeBodyPart mbp2 = new MimeBodyPart();

                    // attach the file to the message
                    FileDataSource fds = new FileDataSource(emailAttachment);
                    mbp2.setDataHandler(new DataHandler(fds));
                    mbp2.setFileName(fds.getName());

                    // create the Multipart and its parts to it
                    Multipart mp = new MimeMultipart();
                    mp.addBodyPart(mbp1);
                    mp.addBodyPart(mbp2);

                    // add the Multipart to the message
                    message.setContent(mp);
                }
                else
                {
                    message.setText(emailBody.toString());
                }
            }

            // send the message
            Transport.send(message);

            // Display confirmation
            rundata.getRequest().setAttribute("email", emailBody.toString());
            String confirmTemplate = portlet.getPortletConfig().getInitParameter("confirm.template", 
                                                                                 "JetspeedQuestionnaireConfirmation.jsp");
           // this.setTemplate(rundata, portlet, confirmTemplate);
            setTemplate(rundata, confirmTemplate, true);

            rundata.setMessage("Email successfully sent");
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
            rundata.setMessage("Error sending email: " + e);
        }

        //buildNormalContext(portlet, rundata);

    }


}

