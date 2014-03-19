package com.webbricks.cms;

import java.io.InputStream;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import com.webbricks.datautility.WBCloudFile;
import com.webbricks.datautility.WBCloudFileInfo;
import com.webbricks.datautility.WBCloudFileStorage;
import com.webbricks.datautility.WBCloudFileStorageFactory;
import com.webbricks.exception.WBIOException;

public class LocalCloudFileContentBuilder {

	public static final String LOCAL_FILE_SERVE_URL = "/__wblocalfile/";
	
	private WBCloudFileStorage cloudFileStorage;
	public LocalCloudFileContentBuilder()
	{
		cloudFileStorage = WBCloudFileStorageFactory.getInstance();
	}
	public void serveFile(HttpServletRequest request, HttpServletResponse response, String uri) throws WBIOException
	{
		if (! uri.startsWith(LOCAL_FILE_SERVE_URL))
		{
			return;
		}
		String fullFilePath = uri.substring(LOCAL_FILE_SERVE_URL.length());
		int pos = fullFilePath.indexOf('/');
		String bucket = fullFilePath.substring(0, pos);
		String file = fullFilePath.substring(pos+1);
		file = new String(DatatypeConverter.parseBase64Binary(file));
		WBCloudFile cloudFile = new WBCloudFile(bucket, file);
		InputStream is = null;
		try
		{
			is = cloudFileStorage.getFileContent(cloudFile);
			IOUtils.copy(is, response.getOutputStream());
			WBCloudFileInfo fileInfo = cloudFileStorage.getFileInfo(cloudFile);
			response.setContentType(fileInfo.getContentType());
			
			// do not close the response outputstream here
		} catch (Exception e)
		{
			throw new WBIOException("cannot serve file", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}	
	}
}