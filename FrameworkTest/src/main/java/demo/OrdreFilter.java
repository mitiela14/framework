package com.demo;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class OrdreFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[ORDRE 2/3] Filter : init()");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("[Filter] la requete " + req.getMethod() + " " + req.getRequestURI() + " passe");
        chain.doFilter(request, response);   // laisser continuer vers le servlet
    }

    @Override
    public void destroy() {}
}
