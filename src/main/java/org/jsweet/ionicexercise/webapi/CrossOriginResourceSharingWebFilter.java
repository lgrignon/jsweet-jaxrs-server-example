package org.jsweet.ionicexercise.webapi;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsweet.ionicexercise.Logger;

@WebFilter(filterName = "AddHeaderFilter", urlPatterns = { "/api/*" })
public class CrossOriginResourceSharingWebFilter implements Filter {

	private final static Logger logger = Logger.getLogger(CrossOriginResourceSharingWebFilter.class);

	private final static List<String> AUTHORIZED_ORIGINS = asList("http://localhost:8000", "http://localhost:8100");

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;

			String origin = request.getHeader("Origin");

			logger.info("adding headers through web filter Origin=" + origin);

			// TODO : this should be disabled in production
			// TODO : we should use Ionic CLI proxy or equivalent while
			// developing (http://blog.ionic.io/handling-cors-issues-in-ionic/)

			// http.addHeader("Access-Control-Allow-Origin", "*");

			if (AUTHORIZED_ORIGINS.contains(origin)) {
				response.addHeader("Access-Control-Allow-Origin", origin);
				response.addHeader("Access-Control-Allow-Headers",
						"X-Requested-With, Content-Type, Content-Length, content-length,PHYTS_TOKEN, PHYTS_USER");
				response.addHeader("Access-Control-Allow-Credentials", "true");
				response.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");

				response.addHeader("Access-Control-Expose-Headers", "Content-Length, Date, Server, Transfer-Encoding");
			} else {
				logger.info("HTTP Origin " + origin + " is not authorized in " + AUTHORIZED_ORIGINS);
			}
		}
		chain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void init(FilterConfig c) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}