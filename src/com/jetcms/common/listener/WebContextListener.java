package com.jetcms.common.listener; 
import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext; 

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
	
	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		 
		return super.initWebApplicationContext(servletContext);
	}
}
