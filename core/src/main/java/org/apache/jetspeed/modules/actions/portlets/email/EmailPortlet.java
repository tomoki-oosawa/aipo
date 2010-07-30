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
package org.apache.jetspeed.modules.actions.portlets.email;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.PortletConfigState;
import org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction;

import org.apache.turbine.util.RunData;
import org.apache.torque.util.Criteria;
import org.apache.turbine.util.upload.FileItem;

import org.apache.velocity.context.Context;

// Java stuff
import java.util.Vector;
import java.util.Hashtable;

import java.util.List;
import java.util.Enumeration;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

//JavaMail

import javax.mail.Folder;
import javax.mail.AuthenticationFailedException;
import javax.mail.NoSuchProviderException;
import javax.mail.MessagingException;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Header;

// upload download to db
import javax.servlet.http.HttpServletResponse;

// email stuff
import org.apache.jetspeed.om.apps.email.EmailInboxPeer;
import org.apache.jetspeed.om.apps.email.EmailInbox;

import org.apache.jetspeed.util.PortletSessionState;
/**
 * Email Action
 * 
 * @author <a href="mailto:jlim@gluecode.com">Jonas Lim </a>
 * @version $Id: EmailPortlet.java,v 1.2 2004/03/22 22:26:58 taylor Exp $
 */

public class EmailPortlet extends VelocityPortletAction
{

    private static final JetspeedLogger log = JetspeedLogFactoryService
            .getLogger(EmailPortlet.class.getName());

    private final int maxPerPage = 10;

    /**
     * Subclasses should override this method if they wish to build specific
     * content when maximized. Default behavior is to do the same as normal
     * content.
     */
    protected void buildMaximizedContext(VelocityPortlet portlet,
            Context context, RunData rundata) throws Exception
    {
        buildNormalContext(portlet, context, rundata);

    }

    protected static final String CUSTOMIZE_TEMPLATE = "customizeTemplate";

    /**
     * Subclasses should override this method if they wish to provide their own
     * customization behavior. Default is to use Portal base customizer action
     */
    protected void buildConfigureContext(VelocityPortlet portlet,
            Context context, RunData rundata)
    {
        try
        {
            super.buildConfigureContext(portlet, context, rundata);
        } catch (Exception ex)
        {
            log.error("Exception", ex);
        }
        String template = PortletConfigState.getParameter(portlet, rundata,
                CUSTOMIZE_TEMPLATE, null);
        setTemplate(rundata, template);
    }

    /**
     * Subclasses must override this method to provide default behavior for the
     * portlet action
     */
    protected void buildNormalContext(VelocityPortlet portlet, Context context,
            RunData rundata) throws Exception
    {

        
        Hashtable userInfo = this.getEmailUserInfo(rundata, context);
        Email email = null;

        log.info("BuildNormalContext in EmailPortlet");
        String host = getPortletParameter(rundata, context, "hostname");
        context.put("host", host);

        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");
        String emailAdd = (String) userInfo.get("email");

        context.put("emailAdd", emailAdd);
        String message_prompt = (String)getPortletSession(rundata, context,
                "message_prompt");

        context.put("message_prompt", message_prompt);
        setPortletSession(rundata, context, "message_prompt", "");




        try
        {
            email = new Email(user, pass,
                    getPortletParameters(rundata, context));
            //email.authenticateUser(user, pass);
            context.put("hasLoggedIn", "yes");
        } catch (AuthenticationFailedException ae)
        {
            message_prompt = "Please Enter a Valid Username and Password.";
            context.put("message_prompt", message_prompt);
            context.put("hasLoggedIn", "no");
            log.error(ae);
            return;
        } catch (NoSuchProviderException np)
        {
            message_prompt = "Please Check Email parameters... Protocol(imap/pop3) is case-sensitive. ";
            context.put("message_prompt", message_prompt);
            context.put("hasLoggedIn", "no");
            log.error(np);
            return;
        } catch (Exception e)
        {
            message_prompt = e.getMessage();
            context.put("message_prompt", message_prompt);
            context.put("hasLoggedIn", "no");
            log.error(e);
            return;
        }


        String showNumNewmessages = (String)getPortletSession(rundata, context,
                "showNumNewmessages");

        if (showNumNewmessages == null || showNumNewmessages.equals(""))
        { // get the number of new messages
            // get the username and password in case the user did signout and
            // login again using another username and password..

            String protocol = getPortletParameter(rundata, context, "protocol");
            context.put("protocol", protocol);

            int numNewmessages = email.num_Newmessages();

            String showInbox = (String)getPortletSession(rundata, context,
                    "showInbox");

            log.info("showInbox " + showInbox);
            if (showInbox == null || showInbox.equals(""))// for initial load
                                                          // only
            {
                if (numNewmessages != 0)
                {
                    context.put("numNewmessages", String
                            .valueOf(numNewmessages)); // displays
                    // (numNewmessages)the
                    // number of new
                    // messages
                }
            }
            context.put("num_Newmessages", String.valueOf(numNewmessages)); // displays
            // no.
            // of
            // new
            // messages
            // beside
            // Inbox(num_Newmessages)
            setPortletSession(rundata, context, "showNumNewmessages", "yes");
            setPortletSession(rundata, context, "numNewmessages", String
                    .valueOf(numNewmessages));

        } else if (showNumNewmessages.equals("yes"))
        {
            String num_Newmessages = (String)getPortletSession(rundata, context,
                    "numNewmessages");
            context.put("num_Newmessages", num_Newmessages);
        }

        String compose = (String)getPortletSession(rundata, context, "compose");
        context.put("compose", compose);

        String showInbox = (String)getPortletSession(rundata, context, "showInbox");
        context.put("showInbox", showInbox);

        String showContent = (String)getPortletSession(rundata, context,
                "showContent");
        context.put("showContent", showContent);

        String createfolder = (String)getPortletSession(rundata, context,
                "createfolder");
        context.put("createfolder", createfolder);

        String showFolders = (String)getPortletSession(rundata, context,
                "showFolders");
        context.put("showFolders", showFolders);

        //get folder name
        String protocol = (String)getPortletParameter(rundata, context, "protocol");
        if (protocol.equals("imap"))
        {
            String folder_name = (String)getPortletSession(rundata, context,
                    "folder_name");

            if (folder_name == null || folder_name.equals(""))
            {
                context.put("folder_name", "Inbox");
            } else
            {
                context.put("folder_name", folder_name);
            }

            //messages on each folder
            String showmessagefolder = (String)getPortletSession(rundata, context,
                    "showmessagefolder");
            context.put("showmessagefolder", showmessagefolder);
        }

        String inboxMessages = (String)getPortletSession(rundata, context,
                "inboxMessages");

        checkMessages(rundata, context,email);

        String msgeContent = (String)getPortletSession(rundata, context,
                "msgeContent");

        log.info("msgecontent " + msgeContent);

        if (msgeContent == null || msgeContent.equals(""))
        {
            log.info("null");

            //need to get the value of inContent to be able to retrieve the
            // fields in reply form...
            Vector inContent = (Vector) getPortletSession(rundata, context, "inContent");
                    
            context.put("inContent", inContent);
        } else if (msgeContent.equals("yes"))
        {
            doShowcontent(rundata, context,email);
            setPortletSession(rundata, context, "msgeContent", null);

        }

        String reply = (String)getPortletSession(rundata, context, "reply");
        context.put("reply", reply);
        String forward = (String)getPortletSession(rundata, context, "forward");
        context.put("forward", forward);
        String rsubject = (String)getPortletSession(rundata, context, "rsubject");
        context.put("rsubject", rsubject);

        String msg = (String)getPortletSession(rundata, context, "msg");
        context.put("msg", msg);

        //multiple pages

        String msgctr = (String)getPortletSession(rundata, context, "msgcount");
        String msgcount = "";
        if (msgctr != null && !msgctr.equals(""))
        {
            Integer imsgctr = new Integer(msgctr);
            context.put("msgcount", imsgctr);
        } else
        {
            context.put("msgcount", "");
        }

        String pages = (String)getPortletSession(rundata, context,
                "total_no_of_pages");
        String total_no_of_pages = "";
        if (pages != null && !pages.equals(""))
        {
            Integer ipages = new Integer(pages);
            context.put("total_no_of_pages", ipages);
        } else
        {
            context.put("total_no_of_pages", "");
        }

        String cpage = (String)getPortletSession(rundata, context, "cur_page");
        String cur_page = "";
        if (cpage != null && !cpage.equals(""))
        {
            Integer icur_page = new Integer(cpage);
            context.put("cur_page", icur_page);
        } else
        {
            context.put("cur_page", "");
        }

        String in_index = (String)getPortletSession(rundata, context, "start_index");
        String start_index = "";
        if (in_index != null && !in_index.equals(""))
        {
            Integer iin_index = new Integer(in_index);
            context.put("start_index", iin_index);
        } else
        {
            context.put("start_index", "");
        }

        String cRange = (String)getPortletSession(rundata, context, "range_per_page");
        String range_per_page = "";
        if (cRange != null && !cRange.equals(""))
        {
            Integer irange_per_page = new Integer(cRange);
            context.put("range_per_page", irange_per_page);
        } else
        {
            context.put("range_per_page", "");
        }

        if (protocol.equals("imap"))
        {
            context.put("protocol", protocol);

            Vector vFolders = (Vector) email.allFolders();
            if (vFolders != null)
            {

                context.put("vFolders", vFolders);
            }

            // vector of messages in one folder
            Vector message_folder = (Vector) getPortletSession(rundata, context, "message_folder");

            if (message_folder != null)
            {

                context.put("message_folder", message_folder);
            }

        }
        email.close();
        System.gc();

    }//end of buildNormalcontext

    public void doAuth(RunData data, Context context)
    {
        log.info("doAuth in emailportlet");
        Hashtable userInfo = this.getEmailUserInfo(data, context);

        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        try
        {
            Email email = new Email(user, pass, getPortletParameters(data,
                    context));
            //log.info("after email");
            setPortletSession(data, context, "showFolders", "yes");
            setPortletSession(data, context, "hasLoggedIn", "yes");
            //always recheck the no. of new messages
            setPortletSession(data, context, "showNumNewmessages", null);
            setPortletSession(data, context, "showInbox", null);
            setPortletSession(data, context, "message_prompt", null);
            email.close();

        } catch (AuthenticationFailedException ae)
        {
            String message_prompt = "Authentication Failed... Bad LogIn.";
            setPortletSession(data, context, "hasLoggedIn", "no");
            setPortletSession(data, context, "message_prompt", message_prompt);
        } catch (NoSuchProviderException np)
        {
            String message_prompt = "Please Check Email parameters... Protocol(imap/pop3) is case-sensitive.";
            setPortletSession(data, context, "hasLoggedIn", "no");
            setPortletSession(data, context, "message_prompt", message_prompt);
        } catch (Exception e)
        {
            String message_prompt = e.getMessage();
            setPortletSession(data, context, "message_prompt", message_prompt);
            log.info("error : " + e.getMessage());
        }

        //checkMessages(data, user, pass);
    }

    public void doSignout(RunData data, Context context)
    {
        log.info("sign out");
        setPortletSession(data, context, "hasLoggedIn", "no");
        setPortletSession(data, context, "showFolders", "no");
        setPortletSession(data, context, "showInbox", "no");
        setPortletSession(data, context, "compose", "no");
        setPortletSession(data, context, "showContent", "no");
        setPortletSession(data, context, "reply", "no");
        setPortletSession(data, context, "forward", "no");
        setPortletSession(data, context, "createfolder", "no");
        setPortletSession(data, context, "message_prompt", null);
    }

    public void doCompose(RunData data, Context context) throws Exception
    {
        getEmailUserInfo(data, context);
        log.info("docompose");
        setPortletSession(data, context, "showFolders", "no");
        setPortletSession(data, context, "showInbox", "no");
        setPortletSession(data, context, "compose", "yes");
        setPortletSession(data, context, "showContent", "no");
        setPortletSession(data, context, "reply", "no");
        setPortletSession(data, context, "forward", "no");
        setPortletSession(data, context, "createfolder", "no");
        setPortletSession(data, context, "showmessagefolder", "no");
        setPortletSession(data, context, "message_prompt", null);

    }

    //check if it's a new message
    public int checkNewmessage(Message message[], int current_index)
            throws Exception
    {
        log.info("### check new message");

        //get message Id to know if it's a new message
        String msgeId = getMessageId(message[current_index]);

        Criteria cr = new Criteria();
        cr.add(EmailInboxPeer.MESSAGE_ID, msgeId);

        List vMsge = EmailInboxPeer.doSelect(cr); //tdk2.2 version
        if (vMsge.isEmpty())
        { // message id not found in db
            return 1;
        } else
        { // message found in db but flag is set to 0

            EmailInbox email = (EmailInbox) EmailInboxPeer.doSelect(cr).get(0);
            //tdk2.2 version
            if (email.getReadflag() == 0)
            {
                return 1; // return 1 if new
            } else
                return 0;
        }
    }

    public void doSend(RunData data, Context context)
    {
        try
        {
            Hashtable userInfo = this.getEmailUserInfo(data, context);

            log.info("do send in email portlet");
            String user = (String) userInfo.get("username");
            String pass = (String) userInfo.get("password");

            String addressTo = data.getParameters().getString("addressTo");
            String addressFrom = data.getParameters().getString("addressFrom");
            setPortletSession(data, context, "addressFrom", addressFrom);

            String subject = data.getParameters().getString("subject");
            String msg = data.getParameters().getString("msg");
            FileItem fileItem = data.getParameters().getFileItem("newfile");

            Email email = new Email(user, pass, getPortletParameters(data,
                    context));

            email.doSendEmail(addressTo, addressFrom, subject, msg, fileItem);
            email.close();
            String message_prompt = "Message Sent";
            setPortletSession(data, context, "message_prompt", message_prompt);
            setPortletSession(data, context, "msgeIndex", null);

        } catch (Exception e)
        {
            setPortletSession(data, context, "message_prompt",
                    "Message Sending Failed." + e.getMessage());

            log.error("doSend()", e);
        }
    }

    public void doReply(RunData data, Context context) throws Exception
    {

        log.info("doReply");

        setPortletSession(data, context, "showContent", "no");
        setPortletSession(data, context, "forward", "no");
        setPortletSession(data, context, "reply", "yes");
        
        Hashtable h = (Hashtable) getPortletSession(data, context, "hcontent");

        //get the subject
        String subject = (String) h.get("Subject").toString();

        //get the original message
        String msg = (String) h.get("message").toString();
        setPortletSession(data, context, "msg", "Original Message: " + "\n"
                + "From: " + h.get("From") + "\n" + "Subject: "
                + h.get("Subject") + "\n" + msg);

        //check if subject has a prefix "Re:"
        //if subject doesn't have a prefix "Re:", default as Re:Subject
        if (subject.length() > 3)
        {
            if (subject.substring(0, 3).equals("Re:"))
            {
                setPortletSession(data, context, "rsubject", subject);

            } else
            {
                setPortletSession(data, context, "rsubject", "Re:" + subject);
                log.info("subject" + subject);
            }
        } else
        {
            setPortletSession(data, context, "rsubject", "Re:" + subject);
        }
        setPortletSession(data, context, "msgeIndex", null);
    }

    public void doSendreply(RunData data, Context context)
    {
        String recipient = null;

        try
        {
            log.info("doSendReply");

            Hashtable userInfo = this.getEmailUserInfo(data, context);
            String user = (String) userInfo.get("username");
            String pass = (String) userInfo.get("password");
            String emailAdd = (String) userInfo.get("email");

            String index = (String)getPortletSession(data, context, "index");

            int current_index = Integer.parseInt(index);
            log.info("index" + current_index);

            String msg = (String) data.getParameters().getString("msg");
            log.info("reply message &&&&&&&&&&&&&&&& " + msg);
            setPortletSession(data, context, "msg", msg);
            //set the value of from
            
            String from = data.getParameters().getString("addressFrom");

            //set the value of addresTo
            //this will allow the user to change into new recipient
            String addressTo = data.getParameters().getString("addressTo");

            //set the value of subject
            String subject = data.getParameters().getString("subject");

            //set message
            msg = data.getParameters().getString("msg");

            String newmsge = checkFormat(msg);
            setPortletSession(data, context, "msg", newmsge);
            //check if the subject is not empty, else default as "none"
            if (subject.equals(""))
            {
                subject = "none";
            }

            //if subject doesn't have a prefix "Re:"
            // set the subject with prefix "Re:"
            if (subject.substring(0, 3).equals("Re:"))
            {
                setPortletSession(data, context, "rsubject", subject);
            } else
            {
                setPortletSession(data, context, "rsubject", "Re:" + subject);
            }

            String newSubj = (String)getPortletSession(data, context, "rsubject");
            msg = (String)getPortletSession(data, context, "msg");

            String msgecontent = convertMessage(msg);

            FileItem fileItem = data.getParameters().getFileItem(
                    "attachmentReply");

            Email email = new Email(user, pass, getPortletParameters(data,
                    context));
            //get the exact message
            Message message = email.getMessage(current_index);
            email.reply(from, addressTo, msgecontent, newSubj, fileItem,
                    message);
            email.close();

            String message_prompt = "Reply Sent";
            setPortletSession(data, context, "message_prompt", message_prompt);
            setPortletSession(data, context, "msgeIndex", null);
        } catch (Exception e)
        {

            setPortletSession(data, context, "message_prompt",
                    "Message Sending Failed." + e.getMessage());
            log.error("Error in doSendReply()", e);
        }
    }

    public void doForward(RunData data, Context context)
    {
        log.info("doforward");
        setPortletSession(data, context, "showContent", "no");
        setPortletSession(data, context, "reply", "no");
        setPortletSession(data, context, "createfolder", "no");
        setPortletSession(data, context, "forward", "yes");

        Hashtable h = (Hashtable) getPortletSession(data, context, "hcontent");

        //get subject
        String subject = (String) h.get("Subject").toString();
        log.info("subj in doforward" + subject);

        //check if the subject has a prefix "Fwd:"
        //if subject doesn't have a prefix "Fwd:", default as "Fwd:subject"
        if (subject.length() > 3)
        {
            if (subject.substring(0, 4).equals("Fwd:"))
            {
                setPortletSession(data, context, "rsubject", subject);
                log.info("subject" + subject);
            } else
            {
                setPortletSession(data, context, "rsubject", "Fwd:" + subject);
            }
        } else
        {
            setPortletSession(data, context, "rsubject", "Fwd:" + subject);
            log.info("subject" + subject);
        }

        //get the message
        String msg = (String) h.get("message").toString();
        setPortletSession(data, context, "msg", msg);
        setPortletSession(data, context, "msgeIndex", null);

    }

    public void doForwardsend(RunData data, Context context)
    {

        log.info("forwardsend");

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");
        String from = (String) userInfo.get("email");
        //set addressTo
        String to = data.getParameters().getString("addressTo");

        String index = (String)getPortletSession(data, context, "index");
        int current_index = Integer.parseInt(index);
        setPortletSession(data, context, "msgeIndex", null);
        try
        {
            // Set the from field

            // set subject
            String subject = data.getParameters().getString("subject");
            //check if subject is not empty, else default as "none"
            if (subject.equals(""))
            {
                subject = "none";
            }

            //check if subject has a prefix "Fwd:"
            //if subject doesn't hava a prefix "Fwd:",default as "Fwd:subject"
            if (subject.substring(0, 4).equals("Fwd:"))
            {
                //forward.setSubject(subject);
                setPortletSession(data, context, "rsubject", subject);
            } else
            {
                setPortletSession(data, context, "rsubject", "Fwd:" + subject);
            }
            String fsubject = (String)getPortletSession(data, context, "rsubject");

            //Set message content
            String content = data.getParameters().getString("msg");
            String msge = checkFormat(content);

            Email email = new Email(user, pass, getPortletParameters(data,
                    context));

            //get exact message
            Message message = email.getMessage(current_index);
            email.forward(to, from, fsubject, msge, message);
            email.close();

            String message_prompt = "Message Sent";
            setPortletSession(data, context, "message_prompt", message_prompt);
            setPortletSession(data, context, "msgeIndex", null);
        } catch (Exception e)
        {
            setPortletSession(data, context, "message_prompt",
                    "Message Sending Failed." + e.getMessage());
            log.error("Error in doForwardSend()", e);
        }
    }

    public void doDelete(RunData data, Context context)
    {
        log.info("delete");

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        String foldername = (String)getPortletSession(data, context, "folder_name");

        int current_index = 0;

        try
        {
            //delete
            String index = (String)getPortletSession(data, context, "index");
            Email email = new Email(user, pass, getPortletParameters(data,
                    context));

            String protocol = getPortletParameter(data, context, "protocol");

            //delete in showcontent
            if (index != null && !index.equals(""))
            {
                current_index = Integer.parseInt(index);
                if (protocol.equals("imap"))
                {
                    email.contentDelete(current_index, foldername, protocol);
                } else
                {
                    email.contentDelete(current_index);

                }

                setPortletSession(data, context, "message_prompt", "Deleted");
            }

            //multiple delete
            else
            {
                String[] checkboxes = null;
                checkboxes = data.getParameters().getStrings("check");
                if (checkboxes != null)
                {
                    if (protocol.equals("imap"))
                    {
                        email.checkboxDelete(foldername, checkboxes, protocol);
                        //email.close();
                    } else
                    {
                        email.checkboxDelete(checkboxes);

                    }

                    setPortletSession(data, context, "message_prompt",
                            "Deleted");
                } else
                {
                    setPortletSession(data, context, "message_prompt",
                            "Select message to be deleted.");
                }

            }
            email.close();
            log.info("deleted");
            setPortletSession(data, context, "showContent", "no");
            setPortletSession(data, context, "showInbox", "yes");
            setPortletSession(data, context, "msgeIndex", null);
        } catch (Exception e)
        {
            setPortletSession(data, context, "message_prompt",
                    "Delete Failed. " + e.getMessage());
            log.error("Error in doDelete()", e);

        }
    }

    public void doShowcontent(RunData data, Context context,Email email) throws Exception
    {

        log.info("show content");
        String AttachmentName = null;
        Vector vAttachments = new Vector();
        Message message = null;
        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        String folderMode = (String)getPortletSession(data, context, "folderMode");

        Vector vContent = new Vector();

        String protocol = getPortletParameter(data, context, "protocol");

        String index = (String)getPortletSession(data, context, "index");

        String folder_name = (String)getPortletSession(data, context, "folder_name");

        int current_index = Integer.parseInt(index);

        if (folderMode.equals("inboxFolder") || (protocol.equals("pop3")))
        {
            message = email.getMessage(current_index);
        } else
        {
            message = email.getMessage(current_index, folder_name);
            Folder current_folder = email.getFolder(folder_name);

        }

        String subject = email.getSubject(message);
        String from = email.getFrom(message);

        // POP3 does not provide a "received date", so the getReceivedDate
        // method will
        // return null. It may be possible to examine other message headers
        // (e.g., the "Received" headers) to estimate the received date,
        // but these techniques are error-prone at best.
        String date = null;

        Vector vAddr = email.getTo(message);
        String messageContent = email.getMessageContent(message);

        if (!message.isMimeType("text/plain"))
        {
            boolean hasAtt = email.checkAttachment(message);
            if (hasAtt == true)
            {
                AttachmentName = email.getAttachmentname(message);
                log.info("attachmentName" + AttachmentName);
                vAttachments = email.getAttachments(message);
                log.info("vAttachments " + vAttachments.size());
            }
        }

        if (protocol.equals("pop3"))
        {
            context.put("protocol", "pop3");

        } else if (protocol.equals("imap"))
        {
            date = email.getReceivedDate(message);
            context.put("protocol", "imap");
        }

        //check if there's a subject... else default as "none"
        if ((subject == null) || (subject.equals("")))
        {
            subject = "none";
            log.info("subj" + subject);
        }

        Hashtable hcontent = new Hashtable();
        // Store Message Id into context : RJPY
        hcontent.put("MessageId", getMessageId(message));

        hcontent.put("From", from);
        hcontent.put("Subject", subject);
        if (protocol.equals("imap"))
        {
            hcontent.put("ReceivedDate", date);
        }

        hcontent.put("message", messageContent);

        if (AttachmentName != null)
                hcontent.put("AttachmentName", AttachmentName);

        vContent.add(hcontent);
        //set message as Old
        setMessageflag(message);

        setPortletSession(data, context, "hcontent", hcontent);
        setPortletSession(data, context, "inContent", vContent);

        context.put("inContent", vContent);
        context.put("vAddr", vAddr);
        context.put("vAttachments", vAttachments);


    }

    public void doShow(RunData data, Context context) throws Exception
    {
        log.info("show");
        setPortletSession(data, context, "showInbox", "no");
        setPortletSession(data, context, "showContent", "yes");
        setPortletSession(data, context, "msgeContent", "yes");
        setPortletSession(data, context, "createfolder", "no");
        setPortletSession(data, context, "showmessagefolder", "no");

        //get the index of the message chosen by the user
        String index = (String) data.getParameters().getString("index");
        //get folderMode; to know what folder to open
        String folderMode = (String) data.getParameters().getString(
                "folderMode");

        setPortletSession(data, context, "index", index);
        setPortletSession(data, context, "folderMode", folderMode);

    }

    public void doInbox(RunData data, Context context) throws Exception
    {

        int cur_page = 1;
        int range_per_page = maxPerPage - 1;
        int start_index = 0;
        int total_no_of_pages = 0;

        //reset the value of index
        setPortletSession(data, context, "index", null);
        setPortletSession(data, context, "message_prompt", null);

        //for multiple pages
        setPortletSession(data, context, "cur_page", String.valueOf(cur_page));
        setPortletSession(data, context, "range_per_page", String
                .valueOf(range_per_page));
        setPortletSession(data, context, "start_index", String
                .valueOf(start_index));
        setPortletSession(data, context, "total_no_of_pages", String
                .valueOf(total_no_of_pages));
        setPortletSession(data, context, "showFolders", "no");
        setPortletSession(data, context, "showInbox", "yes");
        setPortletSession(data, context, "compose", "no");
        setPortletSession(data, context, "showContent", "no");
        setPortletSession(data, context, "reply", "no");
        setPortletSession(data, context, "forward", "no");
        setPortletSession(data, context, "createfolder", "no");
        setPortletSession(data, context, "showmessagefolder", "no");
        setPortletSession(data, context, "folder_name", "INBOX");

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        //always recheck the no. of new messages
        setPortletSession(data, context, "showNumNewmessages", null);

        setPortletSession(data, context, "inboxMessages", "yes");
        setPortletSession(data, context, "msgeIndex", null);

    }

    public void checkMessages(RunData data, Context context, Email email) throws Exception
    {

        String start_index1 = (String)getPortletSession(data, context, "start_index");

        String range_per_page1 = (String)getPortletSession(data, context,
                "range_per_page");
        String cur_page1 = (String)getPortletSession(data, context, "cur_page");
        String total_no_of_pages1 = (String)getPortletSession(data, context,
                "total_no_of_pages");
        int start_index = Integer.parseInt(start_index1);
        int range_per_page = Integer.parseInt(range_per_page1);
        int cur_page = Integer.parseInt(cur_page1);
        int total_no_of_pages = Integer.parseInt(total_no_of_pages1);
        
        try
        {
            Hashtable userInfo = this.getEmailUserInfo(data, context);
            String user = (String) userInfo.get("username");
            String pass = (String) userInfo.get("password");
            Vector vMessages = null;
            int msgectr = email.getNo_of_messages();

            setPortletSession(data, context, "msgcount", String
                    .valueOf(msgectr));

            //check if more than 1 page
            if (msgectr > maxPerPage)
            {

                //get the total no. of pages
                if ((msgectr % 10) == 0)
                {
                    total_no_of_pages = (msgectr / 10);
                    setPortletSession(data, context, "total_no_of_pages",
                            String.valueOf(total_no_of_pages));
                    log.info("total pages" + total_no_of_pages);
                    setPortletSession(data, context, "cur_page", String
                            .valueOf(cur_page));
                    log.info("cur_page" + cur_page);
                } else
                {
                    total_no_of_pages = ((msgectr / 10) + 1);
                    setPortletSession(data, context, "total_no_of_pages",
                            String.valueOf(total_no_of_pages));
                    log.info("total pages" + total_no_of_pages);
                    setPortletSession(data, context, "cur_page", String
                            .valueOf(cur_page));
                    log.info("cur_page" + cur_page);
                }
            }
            setPortletSession(data, context, "start_index", String
                    .valueOf(start_index));

            setPortletSession(data, context, "range_per_page", String
                    .valueOf(range_per_page));


            range_per_page1 = (String)getPortletSession(data, context,
                    "range_per_page");

            String protocol = getPortletParameter(data, context, "protocol");
            if (protocol.equals("pop3"))
            {
                context.put("protocol", "pop3");
            } else if (protocol.equals("imap"))
            {
                context.put("protocol", "imap");
            }

            Vector vAscmessages = email.openInbox(protocol);
            // all messages in ascending order

            int msgeIndexnum = 0; // initial message index per page
            String msgeIndexstring = (String)getPortletSession(data, context,
                    "msgeIndex");
            msgeIndexnum = vAscmessages.size();
            if (msgeIndexstring == null || msgeIndexstring.equals(""))
            {
                setPortletSession(data, context, "msgeIndex", String
                        .valueOf(msgeIndexnum));

            } else
            {

                //check if msgeIndex is > than the actual message size
                //if it is, reset msgeIndex
                try
                {
                    int msgIndex = Integer.parseInt(msgeIndexstring);
                    if (msgIndex > msgeIndexnum)
                    {
                        setPortletSession(data, context, "msgeIndex", null);
                    }

                } catch (NumberFormatException ne)
                {
                    setPortletSession(data, context, "msgeIndex", String
                            .valueOf(msgeIndexnum));
                }

            }

            boolean withAttachment = true;
            for (int i = 0; i < vAscmessages.size(); i++)
            {
                Hashtable ht = (Hashtable) vAscmessages.get(i);
                String s = (String) ht.get("hasAttachment");
                Message message = (Message) ht.get("message");

                if (s.equals(""))
                {
                    withAttachment = false;
                } else
                    withAttachment = true;

                DBInsert(data, message, withAttachment);

            }
            
            descendingOrder(data, context, vAscmessages, start_index,
                    range_per_page, msgectr);

        } catch (NoSuchProviderException np)
        {
            log.error("Please check email paramters... Protocol(imap/pop3) is case-sensitive.");
            log.error(np);
        } catch (MessagingException ms)
        {
            log.error(ms);
        } catch (IOException io)
        {
            log.error(io);
        }
    }

    /*-----------------------------------------------------------------------------------------*
     *  Retrieves message id from message : RJPY
     *-----------------------------------------------------------------------------------------*/
    public String getMessageId(Message message)
    {
        String messageid = "";
        try
        {
            // Retrieve message id from headers
            Enumeration e = message.getAllHeaders();
            while (messageid.equals(""))
            {
                Header header = (Header) e.nextElement();
                if (header.getName().equals("Message-ID"))
                {
                    messageid = header.getValue();
                }
            }
        } catch (Exception e)
        {

            log.error("Error in getMessageId()", e);
        }
        return messageid;
        // end Retrieve
    }

    /*-----------------------------------------------------------------------------------------*
     *  Inserts message and attachment detail to database : RJPY
     *-----------------------------------------------------------------------------------------*/

    public void DBInsert(RunData data, Message message, boolean withAttachment)
    {
        try
        {
            log.info("[RJPY] Trying to write into DB...");

            String messageid = getMessageId(message);
            Criteria crit = new Criteria();
            crit.add(EmailInboxPeer.MESSAGE_ID, messageid);
            //Vector vEmail = EmailInboxPeer.doSelect(crit);
            List vEmail = EmailInboxPeer.doSelect(crit); //tdk2.2 version
            if (vEmail.size() == 0)
            {

                log.info("[RJPY] This email is not yet in the DB...");
                String filename = "";
                InputStream is = null;
                //PreparedStatement ps = con.prepareStatement(sql2);

                int size = 0;

                // Get Attachment
                if (withAttachment)
                {

                    Object obj = message.getContent();
                    Multipart mpart = (Multipart) obj;

                    for (int j = 0, n = mpart.getCount(); j < n; j++)
                    {
                        Part part = mpart.getBodyPart(j);
                        String disposition = part.getDisposition();
                        if ((disposition != null)
                                && ((disposition
                                        .equalsIgnoreCase(Part.ATTACHMENT)) || (disposition
                                        .equals(Part.INLINE))))
                        {
                            if (part.getFileName() != null)
                            {
                                log.info("*** Attachment name: "
                                        + part.getFileName());
                                if (part.getContent() instanceof String)
                                {
                                    byte[] b = ((String) part.getContent())
                                            .getBytes();
                                    is = new ByteArrayInputStream(b);
                                } else
                                {
                                    is = part.getInputStream();
                                }
                                filename = part.getFileName();
                                size = part.getSize();
                            }
                        }
                    } //for
                } // if with attachment

                if (is != null)
                {
                    // ps.executeUpdate();
                    log.info("sure");
                    EmailInbox emailInbox = new EmailInbox();
                    emailInbox.setMessageId(messageid);
                    emailInbox.setReadflag(0);
                    emailInbox.setFilename(filename);

                    byte[] b = new byte[size];
                    int bytes = is.read(b);
                    while (bytes != -1)
                    {
                        bytes = is.read(b);
                    }
                    emailInbox.setAttachment(b);
                    EmailInboxPeer.doInsert(emailInbox);
                } else
                {
                    //EmailInboxPeer.executeStatement(sql);
                    EmailInbox emailInbox = new EmailInbox();
                    emailInbox.setMessageId(messageid);
                    emailInbox.setReadflag(0);
                    EmailInboxPeer.doInsert(emailInbox);
                }
                //ps.close();
            } //if email doesnt exist

        } catch (Exception ex)
        {

            log.error("Error in DBInsert()", ex);

        }

    }

    // recent messages dislayed on top
    public void descendingOrder(RunData data, Context context,
            Vector inMessages, int start_index, int range_per_page, int msgectr)
    {
        Vector vDescending = new Vector();
        int msgSize = inMessages.size();
        String msgeIndex1 = (String)getPortletSession(data, context, "msgeIndex");
        int msgIndex = 0;
        try
        {
            //check if msgeIndex is > than the actual message size
            //if it is, reset msgeIndex
            msgIndex = Integer.parseInt(msgeIndex1);
            if (msgIndex > msgSize)
            {
                setPortletSession(data, context, "msgeIndex", null);
            }

        } catch (NumberFormatException ne)
        {
            msgIndex = msgSize;
            setPortletSession(data, context, "msgeIndex", String
                    .valueOf(msgSize));
        }

        //display 10 messages per page (descending order)
        for (int j = start_index; ((j <= range_per_page) && (j < msgectr)); j++)
        {
            vDescending.add(inMessages.get(msgIndex - 1));
            msgIndex--;
        }
        context.put("inMessages", vDescending);

    }

    //check if message is to be move to a new line
    public String convertMessage(String msg) throws Exception
    {
        log.info("convert message");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < msg.length() - 1; i++)
        {
            char c = msg.charAt(i);
            if (c == '\n')
            {
                if (((msg.charAt(i + 1)) != '<') && (msg.charAt(i + 1) != ' '))
                {
                    log.info("new line");
                    sb.append("<br>");
                } else
                {
                    sb.append(c);
                }
            } else
            {
                sb.append(c);
            }
        }
        String returnString = sb.toString();
        return returnString;
    }

    // exclude displaying html tags in replying or forwarding message
    // there are still instances that html tags may be included/displayed in
    // reply/forward message.
    // only those tags below were checked... other than those, tags may be
    // displayed in some cases.

    public String checkFormat(String msg) throws Exception
    {
        log.info("check format ");
        int startIndex = 0;
        int end = 0;

        String testSubstring = null;

        for (int i = 0; i < msg.length() - 6; i++)
        {
            String sub = msg.substring(i, i + 2);
            if (sub.equals("<A"))
            {
                startIndex = i;
                end = msg.indexOf(">", startIndex);
                StringBuffer sb = new StringBuffer(msg);
                sb.replace(startIndex, end + 1, " ");
                msg = sb.toString();
            }
        }

        for (int i = 0; i < msg.length() - 7; i++)
        {
            String sub = msg.substring(i, i + 7);
            if (sub.equals("style=\""))
            {
                startIndex = i;
                end = msg.indexOf(">", startIndex);
                StringBuffer sb = new StringBuffer(msg);
                sb.replace(startIndex, end + 1, " ");
                msg = sb.toString();
            }
        }

        for (int j = 0; j < msg.length() - 6; j++)
        {
            String sub = msg.substring(j, j + 6);
            if (sub.equals("href=\""))
            {
                startIndex = j;
                end = msg.indexOf(">", startIndex);
                StringBuffer sb = new StringBuffer(msg);
                sb.replace(startIndex, end + 1, " ");
                msg = sb.toString();
            }
        }
        return msg;
    }

    //set flag as OLD
    public void setMessageflag(Message message) throws Exception
    {
        log.info("set message flag");

        String msgeId = getMessageId(message);

        Criteria cr = new Criteria();
        cr.add(EmailInboxPeer.MESSAGE_ID, msgeId);

        //Vector vMsge = EmailInboxPeer.doSelect(cr);
        List vMsge = EmailInboxPeer.doSelect(cr); //tdk2.2 version
        EmailInbox eMsge = (EmailInbox) vMsge.get(0);
        eMsge.setReadflag(1);
        EmailInboxPeer.doUpdate(eMsge);
    }

    public void doNext(RunData data, Context context) throws Exception
    {

        //view next page
        log.info("doNext");
        String start_index1 = (String)getPortletSession(data, context, "start_index");

        String range_per_page1 = (String)getPortletSession(data, context,
                "range_per_page");
        String cur_page1 = (String)getPortletSession(data, context, "cur_page");
        String msgeIndex1 = (String)getPortletSession(data, context, "msgeIndex");

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        int start_index = Integer.parseInt(start_index1);
        int range_per_page = Integer.parseInt(range_per_page1);
        int cur_page = Integer.parseInt(cur_page1);
        int msgeIndex = Integer.parseInt(msgeIndex1);

        start_index = (range_per_page + 1);
        range_per_page = (range_per_page + maxPerPage);
        msgeIndex = msgeIndex - 10;
        log.info("msgeIndex in doNext " + msgeIndex);
        setPortletSession(data, context, "start_index", String
                .valueOf(start_index));

        setPortletSession(data, context, "range_per_page", String
                .valueOf(range_per_page));
        setPortletSession(data, context, "msgeIndex", String
                .valueOf(msgeIndex));
        cur_page = cur_page + 1;
        setPortletSession(data, context, "cur_page", String.valueOf(cur_page));

        setPortletSession(data, context, "inboxMessages", "yes");

        log.info("checkmessages ---------");
        //   checkMessages(data,context);

        // store.close();
    }

    public void doPrevious(RunData data, Context context) throws Exception
    {

        //view previous page

        log.info("doPrevious");
        String start_index1 = (String)getPortletSession(data, context, "start_index");
        String range_per_page1 = (String)getPortletSession(data, context,
                "range_per_page");
        String cur_page1 = (String)getPortletSession(data, context, "cur_page");
        String msgeIndex1 = (String)getPortletSession(data, context, "msgeIndex");

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        int start_index = Integer.parseInt(start_index1);
        int range_per_page = Integer.parseInt(range_per_page1);
        int cur_page = Integer.parseInt(cur_page1);
        int msgeIndex = Integer.parseInt(msgeIndex1);

        start_index = start_index - maxPerPage;
        range_per_page = range_per_page - maxPerPage;
        msgeIndex = msgeIndex + 10;
        log.info("msgeIndex in previous " + msgeIndex);
        setPortletSession(data, context, "start_index", String
                .valueOf(start_index));
        setPortletSession(data, context, "range_per_page", String
                .valueOf(range_per_page));
        cur_page = cur_page - 1;
        setPortletSession(data, context, "cur_page", String.valueOf(cur_page));

        setPortletSession(data, context, "msgeIndex", String
                .valueOf(msgeIndex));
        setPortletSession(data, context, "inboxMessages", "yes");

    }

    /*-----------------------------------------------------------------------------------------*
     *  Downloads mail attachment from database using the message id as the key : RJPY
     *-----------------------------------------------------------------------------------------*/

    public void doDownload(RunData data)
    {

        log.info("[RJPY] Downloading Attachment");

        String messageid = data.getParameters().getString("messageid");
        String filename = data.getParameters().getString("filename");

        try
        {
            log.info("inside try in download");
            HttpServletResponse m_response = data.getResponse();

            Criteria crit = new Criteria();
            crit.add(EmailInboxPeer.MESSAGE_ID, messageid);
            List vMsge = EmailInboxPeer.doSelect(crit);

            if (vMsge.isEmpty())
            { // message id not found in db
                return;
            } else
            { // message found in db but flag is set to 0
                EmailInbox email = (EmailInbox) EmailInboxPeer.doSelect(crit)
                        .get(0);
                byte b[] = email.getAttachment();
                m_response.setContentType("application/x-msdownload");
                m_response.setContentLength(b.length);
                m_response.setHeader("Content-Disposition",
                        "attachment; filename="
                                .concat(String.valueOf(filename)));
                m_response.getOutputStream().write(b, 0, b.length);
            }

        } catch (Exception e)
        {
            log.error("Error in doDownload()", e);
        }
    }

    public void DBdelete(RunData data, int current_index, Context context)
            throws Exception
    {
        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        Email email = new Email(user, pass, getPortletParameters(data, context));
        Message message = email.getMessage(current_index);
        String messageId = email.getMessageId(message);
        email.close();

        Criteria crit = new Criteria();
        crit.add(EmailInboxPeer.MESSAGE_ID, messageId);
        EmailInboxPeer.doDelete(crit);
    }

    public void DBmultipleDelete(RunData data, String[] checkboxes,
            Context context) throws Exception
    {
        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        Email email = new Email(user, pass, getPortletParameters(data, context));
        for (int i = 0; i < checkboxes.length; i++)
        {
            int current_index = Integer.parseInt(checkboxes[i]);
            Message message = email.getMessage(current_index);
            String messageId = email.getMessageId(message);
            email.close();
            Criteria crit = new Criteria();
            crit.add(EmailInboxPeer.MESSAGE_ID, messageId);
            EmailInboxPeer.doDelete(crit);
        }
        email.close();
    }

    public void doCreatenewfolder(RunData data, Context context)
            throws AuthenticationFailedException, NoSuchProviderException,
            Exception
    {
        setPortletSession(data, context, "showFolders", "no");
        setPortletSession(data, context, "showInbox", "no");
        setPortletSession(data, context, "compose", "no");
        setPortletSession(data, context, "showContent", "no");
        setPortletSession(data, context, "reply", "no");
        setPortletSession(data, context, "forward", "no");
        setPortletSession(data, context, "createfolder", "yes");
        setPortletSession(data, context, "showmessagefolder", "no");
        setPortletSession(data, context, "message_prompt", null);

    }

    public void doGetfoldername(RunData data, Context context)
    {

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");
        try
        {
            Email email = new Email(user, pass, getPortletParameters(data,
                    context));
            String folder_name = data.getParameters().getString("folder_name");
            email.doCreatefolder(folder_name);
            email.close();
        } catch (Exception e)
        {
            log.error("Error in doGetfoldername()", e);
        }
    }

    public void doGetfolderdest(RunData data, Context context)
    {
        String toFolder = data.getParameters().getString("foldername");
        String fromFolder = (String)getPortletSession(data, context, "folder_name");
        String[] checkboxes = null;
        checkboxes = data.getParameters().getStrings("check");

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        try
        {
            Email email = new Email(user, pass, getPortletParameters(data,
                    context));
            if (checkboxes != null)
            {
                email.moveMessage(fromFolder, toFolder, checkboxes);
            }
            email.close();
            doInbox(data, context);

        } catch (Exception e)
        {
            log.error("Error in doGetfolderdest()", e);
        }
    }

    public void doMovesinglemsge(RunData data, Context context)
    {
        String index = (String)getPortletSession(data, context, "index");

        String fromFolder = (String)getPortletSession(data, context, "folder_name");

        int current_index = Integer.parseInt(index);

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        try
        {
            Email email = new Email(user, pass, getPortletParameters(data,
                    context));
            String toFolder = data.getParameters().getString("foldername");
            email.moveMessage(fromFolder, toFolder, current_index);
            email.close();
            doInbox(data, context);
        } catch (Exception e)
        {
            log.error("Error in doMovesinglemsge()", e);
        }

    }

    // show list of messages in a folder
    public void doOpenmyfolder(RunData data, Context context)
    {
        setPortletSession(data, context, "showInbox", "no");
        setPortletSession(data, context, "showmessagefolder", "yes");
        setPortletSession(data, context, "createfolder", "no");

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        String folder_name = data.getParameters().getString("folder_name");

        setPortletSession(data, context, "folder_name", folder_name);

        try
        {
            Email email = new Email(user, pass, getPortletParameters(data,
                    context));
            String protocol = getPortletParameter(data, context, "protocol");
            Vector message_folder = (Vector) email.openMyfolder(folder_name,
                    protocol);
            setPortletSession(data, context, "message_folder", message_folder);
            email.close();
        } catch (Exception e)
        {
            log.error("Error in doOpenmyfolder()", e);
        }
    }

    public void doFolderdelete(RunData data, Context context)
    {
        String folder_name = data.getParameters().getString("folder_name");

        Hashtable userInfo = this.getEmailUserInfo(data, context);
        String user = (String) userInfo.get("username");
        String pass = (String) userInfo.get("password");

        try
        {
            Email email = new Email(user, pass, getPortletParameters(data,
                    context));
            email.folderDelete(folder_name);
            email.close();

        } catch (Exception e)
        {
            log.error("Error in doFolderdelete()", e);
        }
    }

    /**
     * get user authentication info for email. Check first in porlet instance.
     * If not found retrieve current user name and password
     *  
     */
    public Hashtable getEmailUserInfo(RunData rundata, Context context)
    {
        Hashtable userHash = new Hashtable();
        String user = "";
        String password = "";
        String email = "";
        String jetspeedUser = "";
        String jetspeedPassword = "";
        String jetspeedEmail = "";
        try
        {
            //look first if there is a username passed by the login page
            user = rundata.getParameters().getString("emailUsername");
            password = rundata.getParameters().getString("emailPassword");
            email = rundata.getParameters().getString("addressFrom");

            if (email == null || email.equals(""))
            {
                email = jetspeedEmail = (JetspeedSecurity.getUser(rundata
                        .getUser().getUserName()).getEmail());
            }

            if (user == null || user.equals(""))
            {

                user = getPortletParameter(rundata, context, "username");
                password = getPortletParameter(rundata, context, "password");
                
                //check if there is an email address in the portlet parameter
                String emailParam = getPortletParameter(rundata, context, "email_address");
                if(emailParam != null && !emailParam.equals(""))
                {
                    email = emailParam;
                }

                //check if there is a default value in the portlet parameter
                if (user.equals(""))
                {

                    jetspeedUser = (JetspeedSecurity.getUser(rundata.getUser()
                            .getUserName()).getUserName());
                    jetspeedPassword = (JetspeedSecurity.getUser(rundata
                            .getUser().getUserName()).getPassword());
                    if (email.equals(""))
                    {
                        jetspeedEmail = (JetspeedSecurity.getUser(rundata
                                .getUser().getUserName()).getEmail());
                    }
                    //use turbine user info
                    user = jetspeedUser;
                    password = jetspeedPassword;
                    email = jetspeedEmail;

                }
            }

            userHash.put("username", user);
            userHash.put("password", password);
            userHash.put("email", email);
        } catch (Exception e)
        {
            log.error(e);
        }
        return userHash;
    }

    /**
     * get portlet parameter from portlet instance. if not found, get from
     * registry
     */
    public String getPortletParameter(RunData data, Context context,
            String paramName)
    {
        String ret = null;
        try
        {

            ret = PortletConfigState.getParameter(this.getPortlet(context),
                    data, paramName, null);
        } catch (Exception e)
        {
            ret = null;
        }
        return ret;
    }


    /** sets the portlet session */
    public void setPortletSession(RunData data,Context context,String paramName, Object value)
    {
        
        
      PortletSessionState.setAttribute(this.getPortlet(context),data,paramName,value);

    }    
    
    /** gets the portlet session */
    public Object getPortletSession(RunData data,Context context,String paramName)
    {
        
        
      return (Object)PortletSessionState.getAttribute(this.getPortlet(context),data,paramName);

    }     
    
    public Hashtable getPortletParameters(RunData data, Context context)
    {

        Hashtable param = new Hashtable();
        try
        {
            // PortletInstance instance = this.getPortletInstance(context);
            // Registry reg =
            // org.apache.jetspeed.services.Registry.get("Portlet");

            param.put("hostname",
                    getPortletParameter(data, context, "hostname"));
            param.put("protocol",
                    getPortletParameter(data, context, "protocol"));
            param.put("smtp_user", getPortletParameter(data, context,
                    "smtp_user"));
            param.put("smtp_port", getPortletParameter(data, context,
                    "smtp_port"));
            param.put("smtp_conn_timeout", getPortletParameter(data, context,
                    "smtp_conn_timeout"));
            param.put("smtp_timeout", getPortletParameter(data, context,
                    "smtp_timeout"));
            param.put("smtp_from", getPortletParameter(data, context,
                    "smtp_from"));
            param.put("smtp_localhost", getPortletParameter(data, context,
                    "smtp_localhost"));
            param.put("smtp_ehlo", getPortletParameter(data, context,
                    "smtp_ehlo"));
            param.put("smtp_auth", getPortletParameter(data, context,
                    "smtp_auth"));
            param.put("smtp_dsn_notify", getPortletParameter(data, context,
                    "smtp_dsn_notify"));
            param.put("smtp_dsn_ret", getPortletParameter(data, context,
                    "smtp_dsn_ret"));
            param.put("smtp_allow8bitmime", getPortletParameter(data, context,
                    "smtp_allow8bitmime"));
            param.put("smtp_send_partial", getPortletParameter(data, context,
                    "smtp_send_partial"));
            param.put("smtp_sasl_realm", getPortletParameter(data, context,
                    "smtp_sasl_realm"));
            param.put("smtp_quit_wait", getPortletParameter(data, context,
                    "smtp_quit_wait"));
            param.put("imap_port", getPortletParameter(data, context,
                    "imap_port"));
            param.put("imap_partial_fetch", getPortletParameter(data, context,
                    "imap_partial_fetch"));
            param.put("imap_fetch_size", getPortletParameter(data, context,
                    "imap_fetch_size"));
            param.put("imap_timeout", getPortletParameter(data, context,
                    "imap_timeout"));
            param.put("imap_host", getPortletParameter(data, context,
                    "imap_host"));

        } catch (Exception e)
        {
            log.error("Error in getPortletParameters()", e);
        }

        return param;
    }

}