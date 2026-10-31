
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Object idUsuario = session.getAttribute("ID_USUARIO");
    Object rol = session.getAttribute("ROL");
    Object idSucursal = session.getAttribute("ID_SUCURSAL");

    if (idUsuario == null) {
        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );
        return;
    }

    if (!"SUCURSAL".equalsIgnoreCase(String.valueOf(rol))
            || idSucursal == null) {
        response.sendError(403, "Acceso no autorizado.");
        return;
    }

    String nombreEmpleado = String.valueOf(
            session.getAttribute("NOMBRE_COMPLETO")
    );

    String nombreSucursal = String.valueOf(
            session.getAttribute("NOMBRE_SUCURSAL")
    );
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Panel de Sucursal | Panadería San Carlos</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/css/empleado.css">
    </head>

    <body>

        <header class="encabezado">
            <div>
                <span class="etiqueta">PANADERÍA SAN CARLOS</span>
                <h1>Panel de sucursal</h1>
                <p>Bienvenido, <%= nombreEmpleado%></p>
            </div>

            <form action="${pageContext.request.contextPath}/CerrarSesion"
                  method="POST">
                <button class="salir" type="submit">
                    Cerrar sesión
                </button>
            </form>
        </header>

        <main class="contenedor">

            <section class="bienvenida">
                <div>
                    <span class="etiqueta">SUCURSAL ASIGNADA</span>
                    <h2><%= nombreSucursal%></h2>
                    <p>
                        Administra los pedidos y consulta
                        los despachos de tu sucursal.
                    </p>
                </div>

                <div class="identificador">
                    <span>ID SUCURSAL</span>
                    <strong><%= idSucursal%></strong>
                </div>
            </section>

            <section class="estadisticas">
                <article class="estadistica">
                    <span>Total de pedidos</span>
                    <strong id="totalPedidos">0</strong>
                </article>

                <article class="estadistica">
                    <span>Pedidos pendientes</span>
                    <strong id="pedidosPendientes">0</strong>
                </article>

                <article class="estadistica">
                    <span>Pedidos despachados</span>
                    <strong id="pedidosDespachados">0</strong>
                </article>
            </section>

           
<h2 class="titulo-seccion">Operaciones de sucursal</h2>

<section class="opciones">

    <article class="opcion">
        <div class="simbolo">+</div>
        <h3>Nuevo pedido</h3>
        <p>
            Registra un pedido de productos
            terminados para tu sucursal.
        </p>
        <button type="button"
                onclick="abrirPedidos('nuevo')">
            Registrar pedido
        </button>
    </article>

    <article class="opcion">
        <div class="simbolo">≡</div>
        <h3>Mis pedidos</h3>
        <p>
            Consulta el historial y el estado
            de los pedidos de tu sucursal.
        </p>
        <button type="button"
                onclick="abrirPedidos('pedidos')">
            Ver mis pedidos
        </button>
    </article>

    <article class="opcion">
        <div class="simbolo">✓</div>
        <h3>Mis despachos</h3>
        <p>
            Revisa los pedidos que ya fueron
            despachados.
        </p>
        <button type="button"
                onclick="abrirPedidos('despachos')">
            Consultar despachos
        </button>
    </article>

</section>


            <section class="panel-recientes">
                <div class="panel-titulo">
                    <h2>Pedidos recientes</h2>
                    <span>Solo de tu sucursal</span>
                </div>

                <div id="listaPedidos" class="lista-pedidos">
                    Cargando pedidos...
                </div>
            </section>

        </main>

    
<script>
    window.APP_CONTEXT =
        "${pageContext.request.contextPath}";

    window.ID_SUCURSAL = <%= idSucursal %>;

    window.ROL_USUARIO = "SUCURSAL";
</script>

<script src="${pageContext.request.contextPath}/js/empleado.js"></script>

</body>
</html>
