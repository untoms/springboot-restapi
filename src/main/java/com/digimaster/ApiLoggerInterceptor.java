package com.digimaster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class ApiLoggerInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(ApiLoggerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        logger.info("[preHandle][" + request + "]" + "[" + request.getMethod() + "]" + request.getRequestURI() + getParameters(request));


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long)request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        logger.info("[postHandle][" + ""/*request */ + "]"+"[" + request.getMethod() + "]" + request.getRequestURI() + getParameters(request)+" executeTime : " + executeTime + " ms...");

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null)
            ex.printStackTrace();
        String prefixUri = request.getRequestURI();

        logger.info("[afterCompletion][" + request + "][exception: " + ex + "]");

    }

    private String getParameters(final HttpServletRequest request) {
        final StringBuffer posted = new StringBuffer();
        final Enumeration<?> e = request.getParameterNames();
        if (e != null)
            posted.append("?");
        while (e != null && e.hasMoreElements()) {
            if (posted.length() > 1)
                posted.append("&");
            final String curr = (String) e.nextElement();
            posted.append(curr).append("=");
            if (curr.contains("password") || curr.contains("answer") || curr.contains("pwd")) {
                posted.append("*****");
            } else {
                posted.append(request.getParameter(curr));
            }
        }

        final String ip = request.getHeader("X-FORWARDED-FOR");
        final String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
        if (ipAddr!= null && !ipAddr.isEmpty())
            posted.append("&_psip=" + ipAddr);
        return posted.toString();
    }

    private String getRemoteAddr(final HttpServletRequest request) {
        final String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && ipFromHeader.length() > 0) {
            logger.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }

}
