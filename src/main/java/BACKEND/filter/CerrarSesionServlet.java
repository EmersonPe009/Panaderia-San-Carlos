
package BACKEND.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/CerrarSesion")
public class CerrarSesionServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion != null) {
            sesion.invalidate();
        }

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );
    }
}
