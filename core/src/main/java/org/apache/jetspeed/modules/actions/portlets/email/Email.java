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

//JavaMail

import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.AuthenticationFailedException;
import javax.mail.NoSuchProviderException;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.Address;
import javax.mail.Multipart;
import javax.mail.Flags;
import javax.mail.Part;
import javax.mail.Header;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;

import com.sun.mail.imap.IMAPFolder;

import org.apache.turbine.util.upload.FileItem;
import org.apache.turbine.services.servlet.TurbineServlet;

import java.io.File;

//util
import java.util.List;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.Hashtable;

//tdk2.2 version
import org.apache.torque.util.Criteria;

import org.apache.jetspeed.om.apps.email.EmailInboxPeer;
import org.apache.jetspeed.om.apps.email.EmailInbox;

//for logging
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Email
{

    private static Log log = LogFactory.getLog(Email.class);

    private Properties props;

    private Session session;

    private Store store;

    private Folder folder;

    private Hashtable parameters;

    /**
     * Email Action
     * 
     * @author <a href="mailto:jlim@gluecode.com">Jonas Lim </a>
     * @version $Id: Email.java,v 1.2 2004/03/22 22:26:58 taylor Exp $
     */
    public Email(String user, String pass, Hashtable param)
            throws AuthenticationFailedException, NoSuchProviderException,
            Exception
    {

        parameters = param;
        String host = (String) parameters.get("hostname");
        String protocol = (String) parameters.get("protocol");
        String smtpUser = (String) parameters.get("smtp_user");
        String smtpPort = (String) parameters.get("smtp_port");
        String smtpFrom = (String) parameters.get("smtp_from");
        String smtpConnTimeout = (String) parameters.get("smtp_conn_timeout");
        String smptTimeout = (String) parameters.get("smtp_timeout");
        String smtpLocalhost = (String) parameters.get("smtp_localhost");
        String smtpEhlo = (String) parameters.get("smtp_ehlo");
        String smtpAuth = (String) parameters.get("smtp_auth");
        String smtpDSNNotify = (String) parameters.get("smtp_dsn_notify");
        String smtpDSNRet = (String) parameters.get("smtp_dsn_ret");
        String smtpallow8bitmime = (String) parameters
                .get("smtp_allow8bitmime");
        String smtpsendPartial = (String) parameters.get("smtp_send_partial");
        String smtpSaslrealm = (String) parameters.get("smtp_sasl_realm");
        String smtpquitWait = (String) parameters.get("smtp_quit_wait");
        String imapPort = null;
        String imapPartialfetch = null;
        String imapFetchsize = null;
        String imapTimeout = null;
        String imapHost = null;

        if (protocol.equals("imap"))
        {
            imapPort = (String) parameters.get("imap_port");
            imapPartialfetch = (String) parameters.get("imap_partial_fetch");
            imapFetchsize = (String) parameters.get("imap_fetch_size");
            imapTimeout = (String) parameters.get("imap_timeout");
            imapHost = (String) parameters.get("imap_host");
        }

        props = new Properties();
        props.put("mail.smtp.host", host);
        if (!smtpUser.equals(""))
        {
            props.put("mail.smtp.user", smtpUser);
        }
        if (!smtpPort.equals(""))
        {
            props.put("mail.smtp.port", smtpPort);
        }
        if (!smtpFrom.equals(""))
        {
            props.put("mail.smtp.from", smtpFrom);
        }
        if (!smtpConnTimeout.equals(""))
        {
            props.put("mail.smtp.connectiontimeout", smtpConnTimeout);
        }
        if (!smptTimeout.equals(""))
        {
            props.put("mail.smtp.timeout", smptTimeout);
        }
        if (!smtpLocalhost.equals(""))
        {
            props.put("mail.smtp.localhost", smtpLocalhost);
        }
        if (!smtpEhlo.equals(""))
        {
            props.put("mail.smtp.ehlo", smtpEhlo);
        }
        if (!smtpAuth.equals(""))
        {
            props.put("mail.smtp.auth", smtpAuth);
        }
        if (!smtpDSNNotify.equals(""))
        {
            props.put("mail.smtp.dsn.notify", smtpDSNNotify);
        }
        if (!smtpDSNRet.equals(""))
        {
            props.put("mail.smtp.dsn.ret", smtpDSNRet);
        }
        if (!smtpallow8bitmime.equals(""))
        {
            props.put("mail.smtp.allow8bitmime", smtpallow8bitmime);
        }
        if (!smtpsendPartial.equals(""))
        {
            props.put("mail.smtp.sendpartial", smtpsendPartial);
        }
        if (!smtpSaslrealm.equals(""))
        {
            props.put("mail.smtp.saslrealm", smtpSaslrealm);
        }
        if (!smtpquitWait.equals(""))
        {
            props.put("mail.smtp.quitwait", smtpquitWait);
        }

        if (protocol.equals("imap"))
        {
            if ((imapPort != null) && (!imapPort.equals("")))
            {
                props.put("mail.imap.port", imapPort);
            }
            if ((imapPartialfetch != null) && (!imapPartialfetch.equals("")))
            {
                props.put("mail.imap.partialfetch", imapPartialfetch);
            }
            if ((imapFetchsize != null) && (!imapFetchsize.equals("")))
            {
                props.put("mail.imap.fetchsize", imapFetchsize);
            }
            if ((imapTimeout != null) && (!imapTimeout.equals("")))
            {
                props.put("mail.imap.timeout", imapTimeout);
            }
            if ((imapHost != null) && (!imapHost.equals("")))
            {
                props.put("mail.imap.host", imapHost);
            }
        }
        // Get session
        session = Session.getDefaultInstance(props, null);
        // Get the store
        //store = session.getStore("imap");
        store = session.getStore(protocol);
        

        store.connect(host, user, pass);
       //  boolean b = store.isConnected();
        folder = store.getFolder("INBOX");

    }

    public void authenticateUser(String user, String pass)
            throws AuthenticationFailedException, NoSuchProviderException,
            Exception
    {
        String protocol = (String) parameters.get("protocol");
        String host = (String) parameters.get("hostname");
        // Create empty properties
        Properties props = new Properties();
        props.put("mail.smtp.host", host);

        // Get session
        Session session = Session.getDefaultInstance(props, null);
        // Get the store
        //Stofalsere store = session.getStore("imap");
        Store store = session.getStore(protocol);
        store.connect(host, user, pass);

       
    }

    public void doSendEmail(String addressTo, String addressFrom,
            String subject, String msg, FileItem file) throws Exception
    {

        Session session = Session.getDefaultInstance(props, null);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(addressFrom));

        checkRecipients(addressTo, message);

        // if no subject, default as none
        if (subject.equals(""))
        {
            subject = "none";
        }

        message.setSubject(subject);

        // Part two is attachment
        // check if there's an attachment to be sent
        if (file != null && !file.equals(""))
        {
            sendAttachment(message, file, msg);
        } else
        {
            log.info("file null or space " + file);
            message.setText(msg);
        }

        Transport.send(message);

        if (file != null)
        { // delete the file after being uploaded
            //deleteUploadedfile(file);
        }
    }

    public void uploadAttachment(FileItem fileItem) throws Exception
    {

        log.info("upload attachment");
        //String contentType = fileItem.getContentType();
        java.io.File file1 = new java.io.File(fileItem.getFileName());

        String filePath = file1.getAbsolutePath();
        int d = filePath.lastIndexOf(File.separator);
        String b = filePath.substring(d + 1);
        String filename = b;

        fileItem.write(getAttachmentsFolder() + File.separator + filename);

    }

    public void deleteUploadedfile(FileItem file) throws Exception
    {
        {
            log.info("delete uploaded file");
            java.io.File fn = new java.io.File(file.getFileName());
            String filename = fn.getName();
            String realPath = getAttachmentsFolder();

            File fDelete = new File(realPath + File.separator + filename);
            System.out.println("deleted file : " + fDelete);
            fDelete.delete();
        }

    }

    //check if it's a single/multiple recipients
    public void checkRecipients(String addressTo, Message message)
            throws Exception
    {
        String recipient = null;
        int startIndex = 0;
        int semicolonIndex = 0;
        int lastsemicolonIndex = 0;

        try
        {
            if (addressTo.indexOf(";", 0) == -1)
            {
                log.info("addr" + addressTo.indexOf(";", 0));
                message.setRecipients(Message.RecipientType.TO, InternetAddress
                        .parse(addressTo, false));
            } else
            {
                while ((semicolonIndex = addressTo.indexOf(";", startIndex)) != -1)
                {
                    recipient = addressTo.substring(startIndex, semicolonIndex);
                    startIndex = semicolonIndex + 1;
                    lastsemicolonIndex = semicolonIndex;
                    message.addRecipient(Message.RecipientType.TO,
                            new InternetAddress(recipient));

                }
                recipient = addressTo.substring(lastsemicolonIndex + 1);
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(recipient));
                log.info("recipient" + recipient);
            }
            Address a[] = message.getAllRecipients();
            for (int j = 0; j < a.length; j++)
            {
                log.info("address" + a[j]);
            }

        } catch (Exception e)
        {
            log.error("Error in checkRecepients()",e);
        }
    }

    public void sendAttachment(Message message, FileItem file, String msg)
            throws Exception
    {

        log.info("file not null or space " + file);

        uploadAttachment(file);

        java.io.File fn = new java.io.File(file.getFileName());
        String filename = fn.getName();

        //set the message
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        //messageBodyPart.setText(msg);
        messageBodyPart.setContent(msg, "text/html");

        MimeBodyPart messageBodyPart2 = new MimeBodyPart();

        DataSource source = new FileDataSource(getAttachmentsFolder()
                + File.separator + filename);

        // Set the data handler to the attachment
        messageBodyPart2.setDataHandler(new DataHandler(source));

        // Set the filename
        messageBodyPart2.setFileName(filename.toString());

        // Add the message part and attachment
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(messageBodyPart2);

        // Put parts in message
        message.setContent(multipart);

    }

    public void reply(String from, String addressTo, String msgecontent,
            String subject, FileItem file, Message msge) throws Exception
    {

        //since the folder in getMessage method (getting the exact message) is
        // opened,re-opening the folder may cause an error
        //folder.open(Folder.READ_ONLY);

        //Message cmessage[] = folder.getMessages();
        //MimeMessage message = (MimeMessage)msge[current_index].reply(false);

        MimeMessage message = (MimeMessage) msge.reply(false);

        message.setFrom(new InternetAddress(from));

        checkRecipients(addressTo, message);

        message.setSubject(subject);

        message.setContent(msgecontent, "text/html");

        if (file != null && !file.equals(""))
        {
            log.info("reply with attachment ******");
            sendAttachment(message, file, msgecontent);
        }

        Transport.send(message);

        if (file != null)
        { // delete the file after being uploaded
            deleteUploadedfile(file);
        }
        //folder.close(true);
        //store.close();

    }

    public void forward(String to, String from, String subject, String content,
            Message message) throws Exception
    {

        //since folder in getMessage method (getting the exact message) is
        // opened, re-opening the folder may cause an error.
        //folder.open(Folder.READ_ONLY);

        //Message message[] = folder.getMessages();

        Message forward = new MimeMessage(session);

        forward.setFrom(new InternetAddress(from));
        forward.setSubject(subject);

        checkRecipients(to, forward);

        MimeMessage orig = (MimeMessage) message;
        if (orig.isMimeType("text/plain"))
        {
            log.info("text/plain forward");
            forward.setText(content.toString());
        } else
        {
            log.info("forward html *******************");
            forward.setContent(content, "text/html");
        }

        Transport.send(forward);
        //folder.close(true);
        //store.close();
    }

    // delete specific message
    public void contentDelete(int current_index) throws Exception
    {
        if(!folder.isOpen())
        {
            folder.open(Folder.READ_WRITE); 
        }
        
        // Get directory
        Message message[] = folder.getMessages();
        message[current_index].setFlag(Flags.Flag.DELETED, true);
        log.info("index" + current_index);
    }

    //delete specific message
    public void contentDelete(int current_index, String foldername,
            String protocol) throws Exception
    {

        Folder folder_name = store.getFolder(foldername);
        folder_name.open(Folder.READ_WRITE);
        // Get directory
        Message fmsge[] = folder_name.getMessages();
        fmsge[current_index].setFlag(Flags.Flag.DELETED, true);
        if (protocol.equals("imap"))
        {
            folder_name.expunge();
        }

    }

    //multiple delete
    public void checkboxDelete(String foldername, String[] checkboxes,
            String protocol) throws Exception
    {
        Folder folder_name = store.getFolder(foldername);
        folder_name.open(Folder.READ_WRITE);
        // Get directory
        Message message[] = folder_name.getMessages();

        for (int i = 0; i < checkboxes.length; i++)
        {
            int ind = Integer.parseInt(checkboxes[i]);
            message[ind].setFlag(Flags.Flag.DELETED, true);
            if (protocol.equals("imap"))
            {
                folder_name.expunge();
            }
        }
    }

    //multiple delete
    public void checkboxDelete(String[] checkboxes) throws Exception
    {
        if(!folder.isOpen())
        {
            folder.open(Folder.READ_WRITE);
        }
        
        // Get directory
        Message message[] = folder.getMessages();

        for (int i = 0; i < checkboxes.length; i++)
        {
            int ind = Integer.parseInt(checkboxes[i]);
            message[ind].setFlag(Flags.Flag.DELETED, true);

        }
    }

    //get the total no. of messages received
    public int getNo_of_messages() throws Exception
    {

        if(!folder.isOpen())
        {
            folder.open(Folder.READ_WRITE);    
        }
        
        Vector vMessages = new Vector();
        Message message[] = folder.getMessages();

        int msgectr = message.length;
        return msgectr;

    }

    // get the total number of new messages
    public int num_Newmessages() throws Exception
    {

        if(!folder.isOpen())
        {
            folder.open(Folder.READ_ONLY);    
        }
        
        
        Message message[] = folder.getMessages();
        Vector vNewmessages = new Vector();

        int length = message.length;
        for (int i = 0; i < length; i++)
        {

            folder.close(true);
            Message current_message = getMessage(i);
            int newMessage = checkNewmessage(current_message);
            if (newMessage == 1)
            {
                vNewmessages.add(String.valueOf(current_message
                        .getMessageNumber()));
                log.info("@@@@@@@@@@@@@@@@@@@@@@@@@ new!!!");
            } else
                log.info("@@@@@@@@@@@@@@@@@@@@@@@@@ old");
        }
        int num_newMessages = vNewmessages.size();
        return num_newMessages;
    }

    // this method simply close the inbox folder and store connection...
    // this is important especially when using pop3 protocol since
    // pop3 can't re-connect when mailbox is currently used.

    public void close() throws Exception
    {
       
        if(folder.isOpen() && folder != null)
        {
            folder.close(true);    
        }
        
        if(store.isConnected() && folder != null)
        {
            store.close();
        }
    }

    public void storeClose() throws Exception
    {
        if(store.isConnected() && store != null)
        {
            store.close();
        }
        
    }

    public void close(Folder foldername) throws Exception
    {
        
        if(folder.isOpen() && folder != null)
        {
            folder.close(true);    
        }
        
        if(store.isConnected() && store != null)
        {
            store.close();
        }
    }

    public Vector openInbox(String protocol) throws Exception
    {

        if(!folder.isOpen()) 
        {
            folder.open(Folder.READ_ONLY); 
        }
        
        Vector vAscmessages = new Vector();
        Message message[] = folder.getMessages();

        for (int i = 0; i < message.length; i++)
        {

            Message msge = message[i];

            boolean withAttachment = checkAttachment(msge);
            Hashtable ht = new Hashtable();
            ht.put("From", msge.getFrom()[0]);
            if (msge.getSubject() == null)
            {
                ht.put("Subject", "none"); // set subject to "none" if subject
                                           // is null
            } else
            {
                ht.put("Subject", msge.getSubject());
            }
            ht.put("index", String.valueOf(i));

            // POP3 does not provide a "received date", so the getReceivedDate
            // method will
            // return null. It may be possible to examine other message headers
            // (e.g., the "Received" headers) to estimate the received date,
            // but these techniques are error-prone at best.

            //check protocol... if protocol is pop3, received date is empty
            if (protocol.equals("imap"))
            {
                ht.put("ReceivedDate", msge.getReceivedDate());
            } else
            {
                log.info("empty date!!!!!!!");
                ht.put("ReceivedDate", "");
            }

            ht.put("size", String.valueOf(msge.getSize()));
            ht.put("message", msge);
            if (withAttachment == true)
            {
                ht.put("hasAttachment", "Attachment");
            } else
            {
                ht.put("hasAttachment", "");
            }
            // check if it's a new message,
            int status = checkNewmessage(msge);
            log.info("status " + status);

            if (status == 1)
            {//new
                ht.put("status", "new");
            } else
            {
                ht.put("status", "");
            }
            vAscmessages.add(ht);
        }
        //folder.close(true); //1-28
        //store.close(); //1-28
        return vAscmessages;
    }

    //get exact message
    public Message getMessage(int current_index) throws Exception
    {

        if(!folder.isOpen())
        {
            folder.open(Folder.READ_ONLY);
        }
        
        Message[] messages = folder.getMessages();
        Message message = messages[current_index];

        return message;

    }

    //get the exact message
    public Message getMessage(int current_index, String foldername)
            throws Exception
    {

        Folder folder_name = store.getFolder(foldername);
        folder_name.open(Folder.READ_ONLY);
        Message[] messages = folder_name.getMessages();
        Message message = messages[current_index];

        return message;

    }

    public String getSubject(Message message) throws Exception
    {
        //get subject
        String subject = message.getSubject();
        return subject;
    }

    public String getFrom(Message message) throws Exception
    {
        String from = message.getFrom()[0].toString();
        return from;
    }

    public String getReceivedDate(Message message) throws Exception
    {
        String receivedDate = message.getReceivedDate().toString();
        return receivedDate;

    }

    //get recipients
    public Vector getTo(Message message) throws Exception
    {
        Vector vAddr = new Vector();

        for (int ctr = 0; ctr < message.getRecipients(Message.RecipientType.TO).length; ctr++)
        {// hcontent.put("To",message[current_index].getRecipients(Message.RecipientType.TO)[ctr]);
            log.info("allRecipients in showcontent"
                    + message.getRecipients(Message.RecipientType.TO)[ctr]);
            vAddr.add(message.getRecipients(Message.RecipientType.TO)[ctr]);
            log.info("*** all recipients " + vAddr.size());
        }
        return vAddr;
    }

    public String getMessageContent(Message message) throws Exception
    {
        String cmessage = null;

        if (message.isMimeType("text/plain"))
        {
            //cmessage = convertMessage(message.getContent().toString());
            cmessage = message.getContent().toString();
        } else if (message.isMimeType("text/html"))
        {
            cmessage = convertMessage(message.getContent().toString());
        } else
        {
            Object obj = message.getContent();
            Multipart mpart = (Multipart) obj;
            for (int i = 0, n = mpart.getCount(); i < n; i++)
            {
                Vector vAttachments = new Vector();

                Part part = mpart.getBodyPart(i);
                //get attachment
                String disposition = part.getDisposition();
                if ((disposition != null)
                        && ((disposition.equalsIgnoreCase(Part.ATTACHMENT)) || (disposition
                                .equals(Part.INLINE))))
                {
                    if (disposition.equals(Part.INLINE))
                    {
                        if (part.isMimeType("text/plain"))
                        {
                            cmessage = (String) part.getContent().toString();
                        } else
                        {// if content-type is Image/gif, get the filename and
                         // add to the vector that holds all attachments
                            getAttachments(message);
                        }
                    }

                    if (disposition.equalsIgnoreCase(Part.ATTACHMENT))
                    {
                        log.info("*** Attachment name: " + part.getFileName());
                        getAttachments(message);
                    }
                } else if (disposition == null)
                {
                    if (part.getContent() instanceof MimeMultipart)
                    { // multipart with attachment
                        MimeMultipart mm = (MimeMultipart) part.getContent();
                        cmessage = (mm.getBodyPart(1)).getContent().toString();
                    } else
                    {//multipart - w/o attachment
                        cmessage = (String) part.getContent().toString();
                    }
                }
            }//for
        }//else
        return cmessage;
    }

    public String getAttachmentname(Message message) throws Exception
    {
        Object obj = message.getContent();
        Multipart mpart = (Multipart) obj;
        String name = null;
        for (int i = 0, n = mpart.getCount(); i < n; i++)
        {
            Part part = mpart.getBodyPart(i);
            if (part.getFileName() != null) name = part.getFileName();
        }
        return name;
    }

    public Vector getAttachments(Message message) throws Exception
    {

        Object obj = message.getContent();
        Multipart mpart = (Multipart) obj;

        Vector vAtt = new Vector();
        String name = null;
        String id = null;

        for (int i = 0, n = mpart.getCount(); i < n; i++)
        {
            Part part = mpart.getBodyPart(i);
            name = part.getFileName();

            if (part.getFileName() != null) vAtt.add(part.getFileName());
            //attachmentIDs(message);
        }
        return vAtt;
    }

    public boolean checkAttachment(Message message) throws Exception
    {
        boolean hasattachment = false;
        try
        {
            if ((message.isMimeType("text/plain"))
                    || ((message.isMimeType("text/html"))))
            {
                hasattachment = false;
            }

            else
            { //multipart
                Object obj = message.getContent();
                Multipart mpart = (Multipart) obj;

                for (int i = 0, n = mpart.getCount(); i < n; i++)
                {
                    Part part = mpart.getBodyPart(i);
                    //get attachment
                    String disposition = part.getDisposition();

                    if ((disposition != null)
                            && ((disposition.equalsIgnoreCase(Part.ATTACHMENT)) || (disposition
                                    .equals(Part.INLINE))))
                    {
                        hasattachment = true;
                    } else if (disposition == null)
                    {
                        if (part.getContent() instanceof MimeMultipart)
                        {
                            hasattachment = true;
                        }
                    } else
                        hasattachment = false;
                }

                return hasattachment;
            }
        } catch (Exception e)
        {
            log.error("Error in checkAttachment",e);
        }
        return false;
    }

    //check if it's a new message
    public int checkNewmessage(Message message) throws Exception
    {
        log.info("### check new message in Email");

        //get message Id to know if it's a new message
        String msgeId = getMessageId(message);

        Criteria cr = new Criteria();
        cr.add(EmailInboxPeer.MESSAGE_ID, msgeId);
        //   EmailInbox email =
        // (EmailInbox)EmailInboxPeer.doSelect(cr).elementAt(0);
        //Vector vMsge = EmailInboxPeer.doSelect(cr);
        List vMsge = EmailInboxPeer.doSelect(cr);//tdk2.2 version
        if (vMsge.isEmpty())
        { // message id not found in db
            return 1; // new message
        } else
        { // message found in db, flag is set to 0
            //EmailInbox email =
            // (EmailInbox)EmailInboxPeer.doSelect(cr).elementAt(0);
            EmailInbox email = (EmailInbox) EmailInboxPeer.doSelect(cr).get(0);//tdk2.2
                                                                               // version
            if (email.getReadflag() == 0)
            {
                return 1; // unread message
            } else
                return 0;
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
            log.error("Error in getMessageId()",e);
        }
        return messageid;
        // end Retrieve
    }

    public String convertMessage(String msg) throws Exception
    {
        log.info("convert message");

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < msg.length(); i++)
        {
            char c = msg.charAt(i);
            if (c == '\n')
            {
                log.info("new line");
                sb.append("<br>");
            } else
            {
                sb.append(c);
            }

        }
        String returnString = sb.toString();
        return returnString;
    }

    public void doCreatefolder(String folder_name) throws Exception
    {
        Folder dfolder = store.getDefaultFolder();
        //Folder dfolder = store.getFolder("myfolder");

        IMAPFolder newfolder = (IMAPFolder) dfolder.getFolder(folder_name);

        if (newfolder == null)
        {

            //if the target folder not exist just create it
            //note:when you create a subfolder you can assign its
            // attribute.Details,please read the javamail document.
        }

        if (!newfolder.exists())
        {
            //newfolder.create(Folder.HOLDS_FOLDERS);
            newfolder.create(Folder.HOLDS_MESSAGES);
        }
        //allFolders();
    }

    public Vector allFolders()
    {
        Vector vFolders = new Vector();
        try
        {
            Folder allfolders = store.getDefaultFolder();
            //Folder allfolders = store.getFolder("myfolder");
            Folder[] f = allfolders.list();

            for (int i = 0; i < f.length; i++)
            {
                //String fn = f[i].getFullName();
                String fn = f[i].getName();

                vFolders.add(fn);
            }
            //data.getUser().setTemp("vFolders",vFolders);
            // data.getSession().setAttribute("vFolders",vFolders);
            return vFolders;
        } catch (Exception e)
        {
            log.error("Error in allFolders()",e);
            return null;
        }
    }

    public void moveMessage(String fromFolder, String toFolder,
            String[] checkboxes)
    {
        try
        {
            //get folder destination

            //Folder to_folder = store.getFolder("myfolder/"+toFolder);
            Folder to_folder = store.getFolder(toFolder);
            Folder from_folder = store.getFolder(fromFolder);

            from_folder.open(Folder.READ_WRITE);

            int tempStart = checkboxes.length;
            int startvalue = Integer.parseInt(checkboxes[tempStart - 1]);
            int start = startvalue + 1;

            int tempEnd = Integer.parseInt(checkboxes[0]);
            int end = tempEnd + 1;

            to_folder.open(Folder.READ_WRITE);
            // get the message
            Message msge[] = from_folder.getMessages(start, end);
            from_folder.copyMessages(msge, to_folder);
            from_folder.close(true);

            //delete the message from inbox folder
            String protocol = (String) parameters.get("protocol");

            checkboxDelete(fromFolder, checkboxes, protocol);

        } catch (Exception e)
        {
            log.error("Error in moveMessage()",e);
        }
    }

    public void moveMessage(String fromFolder, String toFolder,
            int current_index)
    {
        try
        {
            //get folder destination
            //Folder dfolder = store.getFolder("myfolder/"+toFolder);
            Folder dfolder = store.getFolder(toFolder);
            Folder from_folder = store.getFolder(fromFolder);

            from_folder.open(Folder.READ_WRITE);
            dfolder.open(Folder.READ_WRITE);

            // get the message
            Message msge[] = from_folder.getMessages(current_index + 1,
                    current_index + 1);
            from_folder.copyMessages(msge, dfolder);
            //from_folder.close(true);

            //delete the message from inbox folder
            String protocol = (String) parameters.get("protocol");

            contentDelete(current_index, fromFolder, protocol);

        } catch (Exception e)
        {
            log.error("Error in Movemessage()",e);
        }

    }

    public Vector openMyfolder(String foldername, String protocol)
            throws Exception
    {
        Vector message_folder = new Vector();

        try
        {
            Folder fname = store.getFolder(foldername);
            fname.open(Folder.READ_ONLY);

            Message message[] = fname.getMessages();
            for (int i = 0; i < message.length; i++)
            {
                Message msge = message[i];

                boolean withAttachment = checkAttachment(msge);
                Hashtable ht = new Hashtable();
                ht.put("From", msge.getFrom()[0]);

                if (msge.getSubject() == null)
                {
                    ht.put("Subject", "none"); // set subject to "none" if
                                               // subject is null
                } else
                {
                    ht.put("Subject", msge.getSubject());
                }
                ht.put("index", String.valueOf(i));

                // POP3 does not provide a "received date", so the
                // getReceivedDate method will
                // return null. It may be possible to examine other message
                // headers
                // (e.g., the "Received" headers) to estimate the received
                // date,
                // but these techniques are error-prone at best.

                //check protocol... if protocol is pop3, received date is
                // empty

                if (protocol.equals("imap"))
                {
                    ht.put("ReceivedDate", msge.getReceivedDate());
                } else
                {
                    log.info("empty date!!!!!!!");
                    ht.put("ReceivedDate", "");
                }

                ht.put("size", String.valueOf(msge.getSize()));
                ht.put("message", msge);
                if (withAttachment == true)
                {
                    ht.put("hasAttachment", "Attachment");
                } else
                {
                    ht.put("hasAttachment", "");
                }
                // check if it's a new message,
                int status = checkNewmessage(msge);
                log.info("status " + status);

                if (status == 1)
                {//new
                    ht.put("status", "new");
                } else
                {
                    ht.put("status", "");
                }
                message_folder.add(ht);
            }
            //folder.close(true); //1-28
            //store.close(); //1-28
            return message_folder;

        } catch (Exception e)
        {
            log.error("Error in openMyFolder()",e);
            return message_folder;
        }

    }

    public void folderDelete(String folder_name)
    {
        try
        {
            //Folder current_folder =
            // store.getFolder("myfolder/"+folder_name);
            Folder current_folder = store.getFolder(folder_name);
            //current_folder.open(Folder.READ_WRITE);
            current_folder.delete(true);

        } catch (Exception e)
        {
            log.error("Error in folderDelete()",e);
        }

    }

    public Folder getFolder(String folder_name)
    {
        try
        {
            Folder current_folder = store.getFolder(folder_name);
            return current_folder;
        } catch (Exception e)
        {
            log.error("Error in getFolder()",e);
            return null;
        }
    }
    
    public String getAttachmentsFolder()
    {
        String path = TurbineServlet.getRealPath(File.separator)
                + "attachments";
        File aFolder = new File(path);
        if (!aFolder.exists())
        {
            aFolder.mkdir();
        }

        return path;

    }    

}