<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Object usuario = session.getAttribute("ID_USUARIO");
    String rol = String.valueOf(session.getAttribute("ROL"));
    Object sucursal = session.getAttribute("ID_SUCURSAL");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    boolean admin = "ADMINISTRADOR".equalsIgnoreCase(rol);
    boolean empleado = "SUCURSAL".equalsIgnoreCase(rol);
    if ((!admin && !empleado) || (empleado && sucursal == null)) {
        response.sendError(403, "Acceso no autorizado");
        return;
    }
    int idSucursal = empleado ? Integer.parseInt(sucursal.toString()) : 0;
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Pedidos | Panadería San Carlos</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pedidos.css">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    </head>
    <body>
        <div class="contenedor">
            <header class="encabezado">
                <div>
                    <a href="${pageContext.request.contextPath}/pages/<%= empleado ? "empleado.jsp" : "dashboard.jsp"%>" style="color:inherit">← Volver al panel</a>
                    <h1><%= empleado ? "Pedidos de mi sucursal" : "Gestión de Pedidos"%></h1>
                    <p>Panadería San Carlos | <%= empleado ? "Sucursal " + String.valueOf(session.getAttribute("NOMBRE_SUCURSAL")) : "Pedidos de sucursales"%></p>
                </div>
                <button class="btn btn-principal" id="btnNuevoPedido">+ Nuevo pedido</button>
            </header>
            <section class="estadisticas">
                <div class="tarjeta"><span>Total de pedidos</span><strong id="totalPedidos">0</strong></div>
                <div class="tarjeta"><span>Pendientes</span><strong id="totalPendientes">0</strong></div>
                <div class="tarjeta"><span>Despachados</span><strong id="totalDespachados">0</strong></div>
                <div class="tarjeta"><span>Unidades pendientes</span><strong id="totalUnidadesPendientes">0</strong></div>
            </section>
            <section class="panel">
                <div class="panel-cabecera"><div><h2>Siguiente pedido en turno</h2><p class="subtitulo">Pedido pendiente más antiguo.</p></div></div>
                <div id="siguientePedido">Consultando pedidos...</div>
            </section>
            <section class="panel">
                <div class="panel-cabecera">
                    
<h2>Historial de pedidos</h2>

<div class="filtros">

    <!-- Buscar pedido por ID -->
    <input
        type="search"
        id="buscarPedido"
        placeholder="Buscar por ID...">

    <!-- Filtrar por estado -->
    <select id="filtroEstado">
        <option value="">Todos los estados</option>
        <option value="PENDIENTE">Pendientes</option>
        <option value="DESPACHADO">Despachados</option>
        <option value="CANCELADO">Cancelados</option>
    </select>

    <!-- Filtrar por turno -->
    <select id="filtroTurno">
        <option value="">Todos los turnos</option>
        <option value="MAÑANA">Mañana</option>
        <option value="TARDE">Tarde</option>
    </select>

    <!-- Actualizar pedidos -->
    <button
        class="btn btn-secundario"
        id="btnActualizar">
        Actualizar
    </button>

</div>

                </div>
                <div class="tabla-contenedor">
                    <table><thead>
                            <tr>
                                <th>ID</th>
                                <th>Sucursal</th>
                                <th>Fecha</th>
                                <th>Turno</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr><
                            </thead>
                        <tbody id="tablaPedidos"><tr><td colspan="6">Cargando pedidos...</td></tr></tbody>
                    </table>
                </div>
            </section>
        </div>
        <div class="modal" id="modalPedido" hidden>
            <div class="modal-contenido">
                <div class="modal-cabecera"><h2>Nuevo pedido</h2><button type="button" class="btn-cerrar" id="cerrarModalPedido">&times;</button></div>
                <form id="formPedido">
                    <label for="sucursalPedido">Sucursal</label>
                    <select id="sucursalPedido" required><option value="">Seleccione una sucursal</option></select>
                    <label for="fechaPedido">Fecha del pedido</label>
                    <input type="date" id="fechaPedido" required>

                    <div class="campo">
                        <label for="turnoPedido">Turno de entrega</label>

                        <select id="turnoPedido" required>
                            <option value="MAÑANA">Mañana</option>
                            <option value="TARDE">Tarde</option>
                        </select>
                    </div>

                    <div class="seccion-titulo"><h3>Productos solicitados</h3><button type="button" class="btn btn-secundario" id="btnAgregarProducto">+ Agregar producto</button></div>
                    <div id="listaProductosPedido"></div>
                    <div class="modal-acciones">
                        <button type="button" class="btn btn-secundario" id="btnCancelarPedido">Cancelar</button>
                        <button type="submit" class="btn btn-principal" id="btnGuardarPedido">Registrar pedido</button>
                    </div>
                </form>
            </div>
        </div>
        <div class="modal" id="modalDetallePedido" hidden>
            <div class="modal-contenido">
                <div class="modal-cabecera"><h2>Detalle del pedido</h2><button type="button" class="btn-cerrar" id="cerrarModalDetalle">&times;</button></div>
                <div class="resumen-detalle">
                    <p><strong>ID:</strong> <span id="detalleIdPedido"></span></p>
                    <p><strong>Sucursal:</strong> <span id="detalleSucursal"></span></p>
                    <p><strong>Fecha:</strong> <span id="detalleFecha"></span></p>
                    <p><strong>Estado:</strong> <span id="detalleEstado"></span></p>
                    <p><strong>Fecha de despacho:</strong> <span id="detalleDespacho"></span></p>
                </div>
                <div class="tabla-contenedor"><table><thead><tr><th>Producto</th><th>Cantidad</th></tr></thead><tbody id="tablaDetallePedido"></tbody></table></div>
                <div class="modal-acciones"><button type="button" class="btn btn-secundario" id="btnCerrarDetalle">Cerrar</button></div>
            </div>
        </div>
        
<!-- MODAL REGISTRO DE RESTO -->
<div class="modal" id="modalResto" hidden>

    <div class="modal-contenido">

        <div class="modal-cabecera">
            <h2>Registrar RESTO</h2>

            <button
                type="button"
                class="btn-cerrar"
                id="cerrarModalResto">
                &times;
            </button>
        </div>

        <p id="informacionPedidoResto"></p>

        <p class="subtitulo">
            Ingresa las unidades que sobraron.
            Si no sobró ninguna, escribe 0.
        </p>

        <form id="formResto">

            <div class="tabla-contenedor">
                <table>
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Despachado</th>
                            <th>RESTO</th>
                            <th>Vendido</th>
                        </tr>
                    </thead>

                    <tbody id="tablaRegistroResto"></tbody>
                </table>
            </div>

            <div class="modal-acciones">
                <button
                    type="button"
                    class="btn btn-secundario"
                    id="btnCancelarResto">
                    Cancelar
                </button>

                <button
                    type="submit"
                    class="btn btn-principal"
                    id="btnGuardarResto">
                    Guardar RESTO
                </button>
            </div>

        </form>
    </div>
</div>

        <script>
            window.ROL_USUARIO =
                    "<%= session.getAttribute("ROL")%>";

            window.ID_SUCURSAL =
                    "<%= session.getAttribute("ID_SUCURSAL")%>";
        </script>
        <script src="${pageContext.request.contextPath}/js/pedidos.js"></script>
    </body>
</html>
