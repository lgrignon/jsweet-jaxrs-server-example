package org.jsweet.ionicexercise.webapi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.jsweet.ionicexercise.Logger;

@WebFilter(filterName = "AddHeaderFilter", urlPatterns = { "/api/*" })
public class CrossOriginResourceSharingWebFilter implements Filter {

	private final static Logger logger = Logger.getLogger(CrossOriginResourceSharingWebFilter.class);

	@Override
	public void init(FilterConfig c) throws ServletException {
	}
	
	@Override
	public void destroy() {
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (response instanceof HttpServletResponse) {
			logger.info("adding headers through web filter");
			HttpServletResponse http = (HttpServletResponse) response;
			http.addHeader("Access-Control-Allow-Origin", "*");
			http.addHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Content-Length, content-length");
			http.addHeader("Access-Control-Allow-Credentials", "true");
			http.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		}
		chain.doFilter(request, response);
	}
}