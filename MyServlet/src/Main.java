import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/hello")
public class Main extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException {
        /*PrintWriter out = response.getWriter();
        out.println("Hello World AAAA");*/
        /*out.println("<h1 style=\"color:green\">Hello World</h1>");*/

        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String method = request.getMethod();
        String pathInfo = request.getPathInfo();
        String remoteUser = request.getRemoteUser();

        System.out.println("Servlet Path :"+servletPath);
        System.out.println("RequestURI :"+requestURI);
        System.out.println("ContextPath :"+contextPath);
        System.out.println("Method :"+method);
        System.out.println("PathInfo :"+pathInfo);
        System.out.println("RemoteUser :"+remoteUser);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("doPost Method is Invoked");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("doPut Method is Invoked");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("doDelete Method is Invoked");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("doOption Method is Invoked");
    }
}