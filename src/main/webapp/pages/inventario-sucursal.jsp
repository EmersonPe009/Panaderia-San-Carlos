
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

    <title>Mi inventario | Panadería San Carlos</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/empleado.css">

    <style>
        .inventario-panel {
            background: white;
            border-radius: 16px;
            padding: 25px;
            margin-top: 25px;
        }

        .inventario-tabla {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .inventario-tabla th,
        .inventario-tabla td {
            padding: 14px;
            text-align: left;
            border-bottom: 1px solid #e5e5e5;
        }

        .inventario-tabla th {
            background: #f4eee8;
        }

        .inventario-tabla td {
            color: #333;
        }

        .existencia {
            font-weight: bold;
        }

        .sin-stock {
            color: #c0392b;
        }

        .con-stock {
            color: #27824d;
        }

        .volver {
            display: inline-block;
            margin-bottom: 20px;
            color: #765039;
            text-decoration: none;
            font-weight: bold;
        }

        .tabla-contenedor {
            overflow-x: auto;
        }
    </style>
</head>

<body>

<header class="encabezado">
    <div>
        <span class="etiqueta">
            PANADERÍA SAN CARLOS
        </span>
        <h1>Inventario de sucursal</h1>
        <p>Sucursal: <%= nombreSucursal %></p>
    </div>
</header>

<main class="contenedor">

    <a class="volver"
   href="${pageContext.request.contextPath}/pages/empleado.jsp">
    ← Volver al panel
</a>

    <section class="inventario-panel">

        <h2>Productos disponibles</h2>

        <p>
            Consulta las existencias registradas
            de los productos de tu sucursal.
        </p>

        <div class="tabla-contenedor">
            <table class="inventario-tabla">
                <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Código</th>
                        <th>Unidad</th>
                        <th>Existencia</th>
                        <th>Acción</th>
                    </tr>
                </thead>

                <tbody id="tablaInventario">
                    <tr>
                        <td colspan="4">
                            Cargando inventario...
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

<div id="panelActualizarResto"
     class="inventario-panel"
     hidden>

    <h3>Actualizar RESTO</h3>

    <p id="nombreProductoResto"></p>

    <form id="formActualizarResto">

        <input type="hidden"
               id="idProductoResto">

        <label for="existenciaActualResto">
            Existencia actual
        </label>

        <input type="number"
               id="existenciaActualResto"
               readonly>

        <label for="nuevaExistenciaResto">
            ¿Cuántos productos quedan?
        </label>

        <input type="number"
               id="nuevaExistenciaResto"
               min="0"
               step="0.001"
               required>

        <div class="acciones">
            <button type="submit"
                    id="btnGuardarResto">
                Guardar RESTO
            </button>

            <button type="button"
                    id="btnCancelarActualizacionResto">
                Cancelar
            </button>
        </div>
    </form>
</div>

    </section>

</main>

<script>
    window.APP_CONTEXT =
        "${pageContext.request.contextPath}";
</script>

<script src="${pageContext.request.contextPath}/js/inventario-sucursal.js"></script>

</body>
</html>
