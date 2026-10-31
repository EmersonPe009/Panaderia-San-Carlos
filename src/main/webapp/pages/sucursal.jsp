
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="javax.servlet.http.HttpSession"%>
<%
    HttpSession sesionActual = request.getSession(false);

    if (sesionActual == null
            || sesionActual.getAttribute("ID_USUARIO") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String rol = String.valueOf(
            sesionActual.getAttribute("ROL"));

    if (!"SUCURSAL".equalsIgnoreCase(rol)) {
        response.sendError(403, "Acceso denegado");
        return;
    }

    Object idSucursal =
            sesionActual.getAttribute("ID_SUCURSAL");

    if (idSucursal == null) {
        response.sendError(403, "Sucursal no asignada");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Panel de sucursal - Panadería San Carlos</title>

    <link rel="stylesheet" href="../css/sucursal.css">

    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>

<body>

<header class="cabecera">
    <div>
        <h1>Panadería San Carlos</h1>
        <p>Panel de pedidos de sucursal</p>
    </div>

    <span class="sucursal-identificador">
        Sucursal #<%= idSucursal %>
    </span>
</header>

<main class="contenedor">

    <section class="estadisticas">

        <article class="tarjeta">
            <h3>Total de pedidos</h3>
            <strong id="totalPedidos">0</strong>
        </article>

        <article class="tarjeta">
            <h3>Pendientes</h3>
            <strong id="pedidosPendientes">0</strong>
        </article>

        <article class="tarjeta">
            <h3>Despachados</h3>
            <strong id="pedidosDespachados">0</strong>
        </article>

    </section>

    <section class="panel">

        <div class="panel-cabecera">
            <div>
                <h2>Mis pedidos</h2>
                <p>Consulta los pedidos de tu sucursal.</p>
            </div>

            <button id="btnActualizar"
                    type="button"
                    class="btn btn-secundario">
                Actualizar
            </button>
        </div>

        <div class="tabla-contenedor">
            <table>
                <thead>
                    <tr>
                        <th>Pedido</th>
                        <th>Fecha</th>
                        <th>Turno</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody id="tablaPedidos">
                    <tr>
                        <td colspan="5">Cargando pedidos...</td>
                    </tr>
                </tbody>
            </table>
        </div>

    </section>

    <section class="panel">

        <h2>Registrar nuevo pedido</h2>

        <form id="formNuevoPedido">

            <div class="campos">
                <label>
                    Fecha del pedido
                    <input type="date"
                           id="fechaPedido"
                           required>
                </label>

                <label>
                    Turno
                    <select id="turnoPedido" required>
                        <option value="">Seleccionar</option>
                        <option value="MAÑANA">Mañana</option>
                        <option value="TARDE">Tarde</option>
                    </select>
                </label>
            </div>

            <div class="campos">
                <label>
                    Producto terminado
                    <select id="productoPedido">
                        <option value="">Seleccionar producto</option>
                    </select>
                </label>

                <label>
                    Cantidad
                    <input type="number"
                           id="cantidadProducto"
                           min="1"
                           step="1"
                           value="1">
                </label>
            </div>

            <button type="button"
                    id="btnAgregarProducto"
                    class="btn btn-secundario">
                Agregar producto
            </button>

            <div class="tabla-contenedor">
                <table>
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Cantidad</th>
                            <th>Acción</th>
                        </tr>
                    </thead>

                    <tbody id="tablaProductosPedido">
                        <tr>
                            <td colspan="3">
                                Sin productos agregados
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <button type="submit"
                    id="btnGuardarPedido"
                    class="btn btn-primario">
                Registrar pedido
            </button>

        </form>

    </section>

</main>

<script src="../js/sucursal.js"></script>

</body>
</html>
