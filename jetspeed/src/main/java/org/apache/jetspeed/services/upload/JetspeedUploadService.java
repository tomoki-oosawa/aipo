package org.apache.jetspeed.services.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.upload.FileItem;
import org.apache.turbine.services.upload.TurbineUploadService;
import org.apache.turbine.services.upload.TurbineUpload;

import org.apache.commons.fileupload.MultipartStream;

import org.apache.jetspeed.om.registry.MediaTypeEntry;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.CapabilityMapFactory;
import org.apache.jetspeed.services.resources.JetspeedResources;


/**
 * <p> This class is an implementation of {@link 
 * org.apache.turbine.services.upload.UploadService}.
 *
 * <p> Files will be stored in temporary disk storage on in memory,
 * depending on request size, and will be available from the {@link
 * org.apache.turbine.util.ParameterParser} as {@link
 * org.apache.turbine.util.upload.FileItem}s.
 *
 * <p>This implementation of {@link 
 * org.apache.turbine.services.upload.UploadService} handles multiple
 * files per single html widget, sent using multipar/mixed encoding
 * type, as specified by RFC 1867.  Use {@link
 * org.apache.turbine.util.ParameterParser#getFileItems(String)} to
 * acquire an array of {@link
 * org.apache.turbine.util.upload.FileItem}s associated with given
 * html widget.
 *
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke SUGAYA</a>
 */
public class JetspeedUploadService
    extends TurbineUploadService
{
    /**
     * <p> Processes an <a href="http://rf.cx/rfc1867.html">RFC
     * 1867</a> compliant <code>multipart/form-data</code> stream.
     *
     * @param req The servlet request to be parsed.
     * @param params The ParameterParser instance to insert form
     * fields into.
     * @param path The location where the files should be stored.
     * @exception TurbineException If there are problems reading/parsing
     * the request or storing files.
     */
    public void parseRequest( HttpServletRequest req,
                              ParameterParser params,
                              String path )
        throws TurbineException
    {
        String contentType = req.getHeader(CONTENT_TYPE);
        if(!contentType.startsWith(MULTIPART_FORM_DATA))
        {
            throw new TurbineException("the request doesn't contain a " +
                MULTIPART_FORM_DATA + " stream");
        }
        int requestSize = req.getContentLength();
        if(requestSize == -1)
        {
            throw new TurbineException("the request was rejected because " +
                "it's size is unknown");
        }
        if(requestSize > TurbineUpload.getSizeMax())
        {
            throw new TurbineException("the request was rejected because " +
                "it's size exceeds allowed range");
        }

        // get encoding info
        String encoding = JetspeedResources.getString(JetspeedResources.CONTENT_ENCODING_KEY,"US-ASCII");
        CapabilityMap cm = CapabilityMapFactory.getCapabilityMap( 
        req.getHeader("User-Agent") );
        String mimeCode = cm.getPreferredType().getCode();
        if ( mimeCode != null )
        {
            MediaTypeEntry media = (MediaTypeEntry)Registry.getEntry(Registry.MEDIA_TYPE, mimeCode);
            if ( media != null && media.getCharacterSet() != null)
            {
                encoding = media.getCharacterSet();
            }
        }

        try
        {
            byte[] boundary = contentType.substring(
                                contentType.indexOf("boundary=")+9).getBytes();
            InputStream input = (InputStream)req.getInputStream();

            MultipartStream multi = new MultipartStream(input, boundary);
            multi.setHeaderEncoding(encoding);
            boolean nextPart = multi.skipPreamble();
            while(nextPart)
            {
                Map headers = parseHeaders(multi.readHeaders());
                String fieldName = getFieldName(headers);
                if (fieldName != null)
                {
                    String subContentType = getHeader(headers, CONTENT_TYPE);
                    if (subContentType != null && subContentType
                                                .startsWith(MULTIPART_MIXED))
                    {
                        // Multiple files.
                        byte[] subBoundary =
                            subContentType.substring(
                                subContentType
                                .indexOf("boundary=")+9).getBytes();
                        multi.setBoundary(subBoundary);
                        boolean nextSubPart = multi.skipPreamble();
                        while (nextSubPart)
                        {
                            headers = parseHeaders(multi.readHeaders());
                            if (getFileName(headers) != null)
                            {
                                FileItem item = createItem(path, headers,
                                                           requestSize);
                                OutputStream os = item.getOutputStream();
                                try
                                {
                                    multi.readBodyData(os);
                                }
                                finally
                                {
                                    os.close();
                                }
                                params.append(getFieldName(headers), item);
                            }
                            else
                            {
                                // Ignore anything but files inside
                                // multipart/mixed.
                                multi.discardBodyData();
                            }
                            nextSubPart = multi.readBoundary();
                        }
                        multi.setBoundary(boundary);
                    }
                    else
                    {
                        if (getFileName(headers) != null)
                        {
                            // A single file.
                            FileItem item = createItem(path, headers,
                                                       requestSize);
                            OutputStream os = item.getOutputStream();
                            try
                            {
                                multi.readBodyData(os);
                            }
                            finally
                            {
                                os.close();
                            }
                            params.append(getFieldName(headers), item);
                        }
                        else
                        {
                            // A form field.
                            FileItem item = createItem(path, headers,
                                                       requestSize);
                            OutputStream os = item.getOutputStream();
                            try
                            {
                                multi.readBodyData(os);
                            }
                            finally
                            {
                                os.close();
                            }
                            params.append(getFieldName(headers),
                                          new String(item.get()));
                        }
                    }
                }
                else
                {
                    // Skip this part.
                    multi.discardBodyData();
                }
                nextPart = multi.readBoundary();
            }
        }
        catch(IOException e)
        {
            throw new TurbineException("Processing of " + MULTIPART_FORM_DATA
                                       + " request failed", e);
        }

    }

}
