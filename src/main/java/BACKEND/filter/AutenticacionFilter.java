
package BACKEND.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebFilter(
    urlPatterns = {
        "/dashboard.jsp",
        "/empleado.jsp",
        "/inventario-sucursal.jsp",
        "/pages/*",
        "/webapi/*"
    }
)

public class AutenticacionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req =
                (HttpServletRequest) request;

        HttpServletResponse res =
                (HttpServletResponse) response;

        String ruta = req.getRequestURI();
        String contexto = req.getContextPath();

        // Servicios que no necesitan una sesión
        if (ruta.equals(contexto + "/webapi/Usuarios/Login")
                || ruta.equals(contexto + "/webapi/myresource")
                || ruta.equals(contexto + "/webapi/myresource/json")) {

            chain.doFilter(request, response);
            return;
        }

        HttpSession sesion = req.getSession(false);

        boolean autenticado =
                sesion != null
                && sesion.getAttribute("ID_USUARIO") != null;

        boolean servicioRest =
                req.getServletPath().startsWith("/webapi/");

        if (!autenticado) {

            if (servicioRest) {
                res.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
                );
                res.setContentType(
                    "application/json;charset=UTF-8"
                );
                res.getWriter().write(
                    "{\"error\":\"Sesion no valida o expirada\"}"
                );
            } else {
                res.sendRedirect(
                    req.getContextPath() + "/login.jsp"
                );
            }

            return;
        }

        res.setHeader(
            "Cache-Control",
            "no-cache, no-store, must-revalidate"
        );
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
