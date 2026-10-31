
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Producción | Panadería San Carlos</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/produccion.css">

    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>

<body>

<div class="contenedor">

    <header class="encabezado">
        <div>
            <h1>Gestión de Producción</h1>
            <p>Panadería San Carlos</p>
        </div>

        <button class="btn btn-principal"
                id="btnNuevaProduccion">
            + Nueva producción
        </button>
    </header>

   
<section class="estadisticas">

    <div class="tarjeta">
        <span>Total de producciones</span>
        <strong id="totalProducciones">0</strong>
    </div>

    <div class="tarjeta">
        <span>Pendientes</span>
        <strong id="totalPendientes">0</strong>
    </div>

    <div class="tarjeta">
        <span>En proceso</span>
        <strong id="totalProcesando">0</strong>
    </div>

    <div class="tarjeta">
        <span>Finalizadas</span>
        <strong id="totalFinalizadas">0</strong>
    </div>

    <div class="tarjeta">
        <span>Productos con baja existencia</span>
        <strong id="totalProductosBajos">0</strong>
        <small>Productos terminados bajo el mínimo</small>
    </div>

    <div class="tarjeta">
        <span>Unidades recomendadas</span>
        <strong id="totalUnidadesRecomendadas">0</strong>
        <small>Unidades necesarias para alcanzar el mínimo</small>
    </div>

</section>

    
    
    
<section class="recomendaciones">

    <!-- RECOMENDACIONES DE INVENTARIO -->
    <div class="panel panel-recomendacion">

        <div class="recomendacion-titulo">
            <div>
               
<div class="titulo-recomendaciones">
    <h2>Producción recomendada</h2>

    <span class="contador-recomendaciones"
          id="contadorRecomendaciones">
        0 productos con existencia baja
    </span>
</div>

                <p>
                    Productos terminados con existencias
                    por debajo del mínimo.
                </p>
            </div>
            <span class="recomendacion-icono">📦</span>
        </div>

        <div id="listaRecomendaciones">
            Cargando recomendaciones...
        </div>

    </div>

    <!-- SIGUIENTE PRODUCCIÓN -->
    <div class="panel panel-recomendacion">

        <div class="recomendacion-titulo">
            <div>
                <h2>Siguiente producción en turno</h2>
                <p>
                    Producción más antigua sin finalizar.
                </p>
            </div>
            <span class="recomendacion-icono">📋</span>
        </div>

        <div id="siguienteProduccion">
            Consultando producciones...
        </div>

    </div>

</section>

    

<!-- DEMANDA CONSOLIDADA DE SUCURSALES -->
<section class="panel">

    <div class="panel-cabecera">
        <div>
            <h2>Demanda consolidada de sucursales</h2>
            <p>
                Productos solicitados por las sucursales
                que están pendientes de despacho.
            </p>
        </div>

        <div class="filtros">

            <input type="date"
                   id="filtroFechaDemanda">

            <select id="filtroTurnoDemanda">
                <option value="">Todos los turnos</option>
                <option value="MAÑANA">Mañana</option>
                <option value="TARDE">Tarde</option>
            </select>

            <button type="button"
                    class="btn btn-secundario"
                    id="btnActualizarDemanda">
                Actualizar demanda
            </button>
            
<button type="button"
        class="btn btn-primario"
        id="btnGenerarProduccionConsolidada">
    Generar producción consolidada
</button>


        </div>
    </div>

    <div class="tabla-contenedor">
        <table>
            <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Turno</th>
                    <th>Producto</th>
                    <th>Cantidad solicitada</th>
                    <th>Pedidos</th>
                    <th>Sucursales</th>
                </tr>
            </thead>

            <tbody id="tablaDemandaProduccion">
                <tr>
                    <td colspan="6">
                        Cargando demanda de sucursales...
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

</section>
<!-- FIN DEMANDA CONSOLIDADA -->

    <section class="panel">

        <div class="panel-cabecera">
            <h2>Historial de producciones</h2>

            <div class="filtros">
                <input type="search"
                       id="buscarProduccion"
                       placeholder="Buscar por ID...">

                <select id="filtroEstado">
                    <option value="">Todos los estados</option>
                    <option value="PENDIENTE">Pendiente</option>
                    <option value="PROCESANDO">Procesando</option>
                    <option value="FINALIZADA">Finalizada</option>
                    <option value="CANCELADA">Cancelada</option>
                </select>

                <button class="btn btn-secundario"
                        id="btnActualizar">
                    Actualizar
                </button>
            </div>
        </div>

        <div class="tabla-contenedor">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Fecha</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody id="tablaProducciones">
                    <tr>
                        <td colspan="4">
                            Cargando producciones...
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

    </section>

</div>

<!-- MODAL NUEVA PRODUCCIÓN -->

<div class="modal" id="modalProduccion" hidden>

    <div class="modal-contenido">

        <div class="modal-cabecera">
            <h2>Nueva producción</h2>

            <button class="btn-cerrar"
                    id="cerrarModalProduccion"
                    type="button">
                &times;
            </button>
        </div>

        <form id="formProduccion">

            <label for="fechaProduccion">
                Fecha de producción
            </label>

            <input type="date"
                   id="fechaProduccion"
                   required>

            <div class="seccion-titulo">
                <h3>Productos a fabricar</h3>

                <button type="button"
                        class="btn btn-secundario"
                        id="btnAgregarProducto">
                    + Agregar producto
                </button>
            </div>

            <div id="listaProductosProduccion"></div>

            <div class="modal-acciones">
                <button type="button"
                        class="btn btn-secundario"
                        id="btnCancelarProduccion">
                    Cancelar
                </button>

                <button type="submit"
                        class="btn btn-principal"
                        id="btnGuardarProduccion">
                    Guardar producción
                </button>
            </div>

        </form>

    </div>
</div>

<!-- MODAL DETALLES -->

<div class="modal" id="modalDetalleProduccion" hidden>

    <div class="modal-contenido">

        <div class="modal-cabecera">
            <h2>Detalle de producción</h2>

            <button class="btn-cerrar"
                    id="cerrarModalDetalle"
                    type="button">
                &times;
            </button>
        </div>

        <div class="resumen-detalle">
            <p><strong>ID:</strong>
                <span id="detalleIdProduccion"></span>
            </p>

            <p><strong>Fecha:</strong>
                <span id="detalleFechaProduccion"></span>
            </p>

            <p><strong>Estado:</strong>
                <span id="detalleEstadoProduccion"></span>
            </p>
        </div>

        <div class="tabla-contenedor">
            <table>
                <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Cantidad</th>
                    </tr>
                </thead>

                <tbody id="tablaDetalleProduccion"></tbody>
            </table>
        </div>

        <div class="modal-acciones">
            <button class="btn btn-secundario"
                    id="btnCerrarDetalle"
                    type="button">
                Cerrar
            </button>
        </div>

    </div>
</div>

<script src="${pageContext.request.contextPath}/js/produccion.js?v=4"></script>
</body>
</html>
