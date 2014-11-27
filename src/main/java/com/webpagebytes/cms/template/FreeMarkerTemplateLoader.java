package com.webpagebytes.cms.template;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBWebPageModulesCache;
import com.webpagebytes.cms.cache.WPBWebPagesCache;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.cmsdata.WBWebPageModule;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.template.FreeMarkerTemplateObject.TemplateType;

import freemarker.cache.TemplateLoader;

public class FreeMarkerTemplateLoader implements TemplateLoader {
	
	private String webPagesPathPrefix;
	private String webModulesPathPrefix;

	private WPBCacheInstances cacheInstances;
	
	public FreeMarkerTemplateLoader()
	{
		
	}
	FreeMarkerTemplateLoader(WPBCacheInstances wbCacheInstances)
	{
		setWebModulesPathPrefix(WPBTemplateEngine.WEBMODULES_PATH_PREFIX);
		setWebPagesPathPrefix(WPBTemplateEngine.WEBPAGES_PATH_PREFIX);

		this.cacheInstances = wbCacheInstances; 
	}
	
	private Object findTemplateWebPageSource(String name)
	{
		try
		{
			WBWebPage wbWebPage = cacheInstances.getWBWebPageCache().get(name);
			if (null != wbWebPage)
			{
				return new FreeMarkerTemplateObject(name, FreeMarkerTemplateObject.TemplateType.TEMPLATE_PAGE, wbWebPage.getLastModified().getTime());
			}
		} catch (WPBIOException e)
		{
			//define logging here
		}
		return null;
	}
	private Object findTemplateWebModuleSource(String name)
	{
		try
		{
			WBWebPageModule wbWebPageModule = cacheInstances.getWBWebPageModuleCache().get(name);
			if (null != wbWebPageModule)
			{
				return new FreeMarkerTemplateObject(name, FreeMarkerTemplateObject.TemplateType.TEMPLATE_MODULE, wbWebPageModule.getLastModified().getTime());
			}
		} catch (WPBIOException e)
		{
			//define logging here
		}
		return null;
	}
	
	public Object findTemplateSource(java.lang.String name) throws IOException
    {
		if (name.startsWith(webPagesPathPrefix))
		{
			return findTemplateWebPageSource(name.substring(webPagesPathPrefix.length()));
		} else
		if (name.startsWith(webModulesPathPrefix)){
			return findTemplateWebModuleSource(name.substring(webModulesPathPrefix.length()));
		} else
		return null;
    }
	
	public long getLastModified(Object templateSource)
	{
		if (templateSource == null)
		{
			return 0L;
		}
		FreeMarkerTemplateObject templateObject = (FreeMarkerTemplateObject)templateSource;
		TemplateType templateObjectType = templateObject.getType();
		if (templateObjectType == FreeMarkerTemplateObject.TemplateType.TEMPLATE_PAGE)
		{
			try
			{
				WBWebPage wbWebPage = cacheInstances.getWBWebPageCache().get(templateObject.getName());
				if (null != wbWebPage)
				{
					return wbWebPage.getLastModified().getTime();
				}
			} catch (WPBIOException e)
			{
				return 0L;
			}
		} 
		if (templateObjectType == FreeMarkerTemplateObject.TemplateType.TEMPLATE_MODULE)
		{
			try
			{
				WBWebPageModule wbWebPageModule = cacheInstances.getWBWebPageModuleCache().get(templateObject.getName());
				if (null != wbWebPageModule)
				{
					return wbWebPageModule.getLastModified().getTime();
				}
			} catch (WPBIOException e)
			{
				return 0L;
			}
		}
			
		return 0L;
	}
	
	public Reader getReader(Object templateSource,
            String encoding) throws IOException
    {
		if (templateSource == null)
		{
			throw new IOException ("No reader for null templateSource");
		}
		FreeMarkerTemplateObject templateObject = (FreeMarkerTemplateObject)templateSource;
		TemplateType templateObjectType = templateObject.getType();
		
		if (templateObjectType == FreeMarkerTemplateObject.TemplateType.TEMPLATE_PAGE)
		{
			try
			{
				WBWebPage wbWebPage = cacheInstances.getWBWebPageCache().get(templateObject.getName());
				if (null != wbWebPage)
				{
					return new StringReader(wbWebPage.getHtmlSource());
				}
			} catch (WPBIOException e)
			{
				throw new IOException(e);
			}
		}
		if (templateObjectType == FreeMarkerTemplateObject.TemplateType.TEMPLATE_MODULE)
		{
			try
			{
				WBWebPageModule wbWebPageModule = cacheInstances.getWBWebPageModuleCache().get(templateObject.getName());
				if (null != wbWebPageModule)
				{
					return new StringReader(wbWebPageModule.getHtmlSource());
				}
			} catch (WPBIOException e)
			{
				throw new IOException(e);
			}
		}
		throw new IOException ("Could not find any template reader");
    }
	
	public void closeTemplateSource(Object templateSource) throws IOException
    {
		
    }
	public String getWebPagesPathPrefix() {
		return webPagesPathPrefix;
	}
	public void setWebPagesPathPrefix(String webPagesPathPrefix) {
		this.webPagesPathPrefix = webPagesPathPrefix;
	}
	public String getWebModulesPathPrefix() {
		return webModulesPathPrefix;
	}
	public void setWebModulesPathPrefix(String webModulesPathPrefix) {
		this.webModulesPathPrefix = webModulesPathPrefix;
	}
	
}