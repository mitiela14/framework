package com.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.annotation.*;

@AnnotationController
public class EmpContr {

    @UrlMapping(value = "/emp/save", method = "GET")
    public String form() {
        return "heloo";
    }

    @UrlMapping(value = "/emp/save", method = "POST")
    public void submit() {
        // ...
    }

    @UrlMapping(value = "/emp/form", method = "GET")
    public void showForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getRequestDispatcher("/WEB-INF/Views/FormEmp.jsp").forward(request, response);
    }

    @UrlMapping(value = "/emp", method = "GET")
    public void commit() {
        // ...
    }

}