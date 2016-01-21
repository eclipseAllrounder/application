package net.application.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;



public class MyFilter implements Filter {

	FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
	}
	@Override
	public  void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException { 
		try
		 { chain.doFilter(request, response); }
		catch
		 (javax.enterprise.context.NonexistentConversationException e) { response.setContentType("text/plain"); response.getWriter().print("NonexistentConversationException"); } 
		}
	
	
	
}
