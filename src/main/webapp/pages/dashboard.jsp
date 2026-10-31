
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    javax.servlet.http.HttpSession sesionActual =
            request.getSession(false);

    if (sesionActual == null
            || sesionActual.getAttribute("ID_USUARIO") == null) {

        response.sendRedirect(
            request.getContextPath() + "/login.jsp"
        );
        return;
    }

    String nombreUsuario = String.valueOf(
            sesionActual.getAttribute("NOMBRE_COMPLETO") != null
            ? sesionActual.getAttribute("NOMBRE_COMPLETO")
            : "Usuario"
    );

    String rolUsuario = String.valueOf(
            sesionActual.getAttribute("ROL") != null
            ? sesionActual.getAttribute("ROL")
            : "Sin rol"
    );
    
if (!"ADMINISTRADOR".equalsIgnoreCase(rolUsuario)) {
    response.sendError(403, "Acceso no autorizado");
    return;
}

    String nombreSeguro = nombreUsuario
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String rolSeguro = rolUsuario
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
%>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>San Carlos | Sistema de Gestión</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<body>

    <!-- ENCABEZADO PRINCIPAL -->
    <header class="encabezado">

        <div class="marca">
            <h1>SAN CARLOS</h1>
            <p>PANADERÍA · SISTEMA DE GESTIÓN</p>
        </div>

        <div class="usuario-sesion">
            <span>
                Bienvenido,
                <strong><%= nombreSeguro %></strong>
            </span>

            <small>
                Rol: <%= rolSeguro %>
            </small>
        </div>

        <div class="usuario">

    <form action="${pageContext.request.contextPath}/CerrarSesion"
          method="POST">

        <button type="submit" class="btn-cerrar-sesion">
            CERRAR SESIÓN
        </button>

    </form>

</div>
    </header>

    <!-- MENÚ PRINCIPAL -->
    <nav class="menu-principal">

        <a href="${pageContext.request.contextPath}/dashboard.jsp">
            PANEL PRINCIPAL
        </a>

        <a href="${pageContext.request.contextPath}/pages/productos.jsp">
            PRODUCTOS
        </a>

       
        <a href="${pageContext.request.contextPath}/pages/pedidos.jsp">
            PEDIDOS
        </a>

        <a href="${pageContext.request.contextPath}/pages/produccion.jsp">
            PRODUCCIÓN
        </a>

    </nav>

    <!-- CONTENIDO PRINCIPAL -->
   
<main class="contenido">

    <section class="bienvenida">
        <p class="subtitulo">
            PANEL PRINCIPAL / ESTADÍSTICAS
        </p>

        <h2>Resumen general de San Carlos</h2>

        <p class="descripcion">
            Consulta el desempeño de las sucursales,
            pedidos, producción e inventario de la panadería.
        </p>
    </section>

    <!-- INDICADORES GENERALES -->
    <section class="dashboard-seccion">

        <h2>Indicadores generales</h2>

        <div class="dashboard-indicadores">

            <div class="indicador">
                <span>Pedidos registrados</span>
                <strong id="estadisticaPedidos">—</strong>
            </div>

            <div class="indicador">
                <span>Pedidos pendientes</span>
                <strong id="estadisticaPendientes">—</strong>
            </div>

            <div class="indicador">
                <span>Pedidos despachados</span>
                <strong id="estadisticaDespachados">—</strong>
            </div>

            <div class="indicador">
                <span>Producciones finalizadas</span>
                <strong id="estadisticaProduccion">—</strong>
            </div>

        </div>

    </section>

<!-- INDICADORES DE VENTAS REALES -->
<section class="dashboard-seccion">

    <h2>Indicadores de ventas reales</h2>

    <p class="subtitulo">
        Estadísticas calculadas únicamente con los
        sobrantes (RESTO) confirmados por las sucursales.
    </p>

    <div class="dashboard-indicadores">

        <div class="indicador">
            <span>Unidades vendidas</span>
            <strong id="estadisticaVendidas">—</strong>
        </div>

        <div class="indicador">
            <span>Unidades sobrantes</span>
            <strong id="estadisticaSobrantes">—</strong>
        </div>

        <div class="indicador">
            <span>Pedidos pendientes de RESTO</span>
            <strong id="estadisticaPendientesResto">—</strong>
        </div>

        <div class="indicador">
            <span>Producto más vendido</span>
            <strong id="estadisticaProductoMasVendido">—</strong>
        </div>

    </div>

</section>

    <!-- RANKING DE SUCURSALES -->
    <section class="dashboard-seccion">

        <div class="dashboard-titulo">
            <div>
                <h2>Rendimiento de sucursales</h2>
                <p class="subtitulo">
    Clasificación de sucursales según unidades realmente vendidas,
    calculadas a partir de los sobrantes (RESTO) reportados.
</p>
            </div>
        </div>

        <div class="tabla-contenedor">
            <table>
                <thead>
                    <tr>
                        <th>Posición</th>
                        <th>Sucursal</th>
                        <th>Pedidos reportados</th>
                        <th>Unidades Vendidas</th>
                    </tr>
                </thead>

                <tbody id="tablaRankingSucursales">
                    <tr>
                        <td colspan="4">
                            Cargando estadísticas...
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

    </section>

    <!-- INVENTARIO -->
    <section class="dashboard-seccion">

        <h2>Alertas de inventario</h2>

        <div class="tabla-contenedor">
            <table>
                <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Existencia actual</th>
                        <th>Existencia mínima</th>
                    </tr>
                </thead>

                <tbody id="tablaInventarioBajo">
                    <tr>
                        <td colspan="3">
                            Cargando inventario...
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

    </section>

    <!-- ACCESOS RÁPIDOS -->
    <section class="modulos dashboard-seccion">

        <h2>Áreas de gestión</h2>

        <div class="contenedor-tarjetas">

            <a class="tarjeta"
               href="${pageContext.request.contextPath}/pages/productos.jsp">

                <span class="numero">01</span>
                <h3>Productos e Inventario</h3>

                <p>
                    Administra productos, ingredientes
                    y recetas de la panadería.
                </p>

                <span class="flecha">→</span>
            </a>

            <a class="tarjeta"
               href="${pageContext.request.contextPath}/pages/pedidos.jsp">

                <span class="numero">02</span>
                <h3>Pedidos</h3>

                <p>
                    Consulta los pedidos registrados
                    por las sucursales.
                </p>

                <span class="flecha">→</span>
            </a>

            <a class="tarjeta"
               href="${pageContext.request.contextPath}/pages/produccion.jsp">

                <span class="numero">03</span>
                <h3>Producción</h3>

                <p>
                    Gestiona la elaboración y finalización
                    de productos.
                </p>

                <span class="flecha">→</span>
            </a>

            <a class="tarjeta"
               href="${pageContext.request.contextPath}/pages/pedidos.jsp?vista=despachos">

                <span class="numero">04</span>
                <h3>Despachos</h3>

                <p>
                    Consulta y gestiona los pedidos
                    enviados a las sucursales.
                </p>

                <span class="flecha">→</span>
            </a>

        </div>

    </section>

</main>


    <!-- PIE DE PÁGINA -->
    <footer class="pie-pagina">

        <p>
            SAN CARLOS © 2026 | SISTEMA DE GESTIÓN
        </p>

    </footer>


<script>
    window.CONTEXTO_APP = "${pageContext.request.contextPath}";
</script>

<script src="${pageContext.request.contextPath}/js/dashboard.js?v=4"></script>

</body>
</html>
