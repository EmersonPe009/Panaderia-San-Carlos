
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
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Productos | San Carlos</title>

    <link rel="stylesheet" href="../css/estilos.css">
    <link rel="stylesheet" href="../css/productos.css">

</head>

<body>

    <!-- ENCABEZADO -->
    <header class="encabezado">
        <div class="marca">
            <h1>SAN CARLOS</h1>
            <p>PANADERÍA · SISTEMA DE GESTIÓN</p>
        </div>

        <div class="usuario">
            <span>Panel Administrativo</span>
        </div>
    </header>

    
<!-- MENÚ LATERAL ADMINISTRATIVO -->
<aside class="sidebar-productos">

    <div class="sidebar-titulo">
        <span class="sidebar-etiqueta">ADMINISTRACIÓN</span>
    </div>

    <nav class="sidebar-menu"
         aria-label="Navegación administrativa">

        <a href="dashboard.jsp">
            <span>▦</span>
            Dashboard
        </a>

        <a href="productos.jsp"
           class="activo"
           aria-current="page">
            <span>▤</span>
            Productos
        </a>

        <a href="#" class="pendiente">
            <span>▣</span>
            Sucursales
        </a>

        <a href="#" class="pendiente">
            <span>▧</span>
            Recetas
        </a>

        <a href="#" class="pendiente">
            <span>▥</span>
            Pedidos
        </a>

        <a href="#" class="pendiente">
            <span>▨</span>
            Producción
        </a>

    </nav>

    <div class="sidebar-pie">
        <span>SAN CARLOS</span>
        <small>Sistema de gestión · 2026</small>
    </div>

</aside>


    <!-- CONTENIDO -->
    <main class="contenido">

        <section class="bienvenida">
            <p class="subtitulo">
                ADMINISTRACIÓN / PRODUCTOS
            </p>

            <h2>Gestión de Productos</h2>

            <p class="descripcion">
                Consulta los productos e ingredientes
                registrados en Panadería San Carlos.
            </p>
        </section>

        
<!-- ESTADÍSTICAS DEL INVENTARIO -->
<section class="estadisticas-productos">

    <article class="estadistica-tarjeta">
        <span class="estadistica-icono">▦</span>
        <h3 id="statTotalProductos">—</h3>
        <p>Productos registrados</p>
        <small>Total en el catálogo</small>
    </article>

    <article class="estadistica-tarjeta">
        <span class="estadistica-icono">✔</span>
        <h3 id="statConExistencia">—</h3>
        <p>Con existencias</p>
        <small>Stock disponible</small>
    </article>

    <article 
        class="estadistica-tarjeta"
         id="tarjetaExistenciaBaja"
         role="link"
         tabindex="0">
        <span class="estadistica-icono">⚠</span>
        <h3 id="statExistenciaBaja">—</h3>
        <p>Existencias bajas</p>
        <small>En el mínimo o por debajo</small>
        
    </article>

    <article class="estadistica-tarjeta">
        <span class="estadistica-icono">Q</span>
        <h3 id="statValorInventario">—</h3>
        <p>Valor del inventario</p>
        <small>Valor estimado registrado</small>
    </article>
    
</section>


<!-- GRÁFICAS DE INVENTARIO -->
<section class="graficas-inventario">

    <article class="grafica-tarjeta">

        <div class="grafica-encabezado">
            <h3>Productos con mayor existencia</h3>
            <p>Comparación de cantidades disponibles</p>
        </div>

        <div class="grafica-contenedor">
            <canvas id="graficaExistencias"></canvas>
        </div>

    </article>

    <article class="grafica-tarjeta">

        <div class="grafica-encabezado">
            <h3>Distribución del catálogo</h3>
            <p>Ingredientes y productos terminados</p>
        </div>

        <div class="grafica-contenedor">
            <canvas id="graficaTipos"></canvas>
        </div>

    </article>

</section>




        
        
        
        
        
        
        <!-- LISTADO DE PRODUCTOS -->
        <section class="modulos">

            
<h2>Listado de Productos</h2>


<div class="acciones-productos">

    
        <button type="button"
         id="btnNuevoProducto"
          class="button-86">
             Nuevo producto
        </button>
    <button type="button" id="btnIngresarExistencias" class="button-86">+ Ingresar existencias</button>

</div>


<div class="barra-herramientas">

    <div class="filtros-productos">
        <button type="button"
                class="filtro activo"
                data-tipo="TODOS">
            Todos
        </button>

        <button type="button"
                class="filtro"
                data-tipo="I">
            Ingredientes
        </button>

        <button type="button"
                class="filtro"
                data-tipo="P">
            Productos terminados
        </button>
    </div>

    <input
        type="search"
        id="buscarProducto"
        placeholder="Buscar por nombre o código..."
        aria-label="Buscar productos"
    >

</div>


<div class="tabla-contenedor">


                <table id="tablaProductos">

                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Código</th>
                            <th>Tipo</th>
                            <th>Unidad</th>
                            <th>Precio</th>
                            <th>Existencia</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody id="cuerpoProductos">
                        <tr>
                            <td colspan="9">
                                Los productos se cargarán aquí.
                            </td>
                        </tr>
                    </tbody>

                </table>

            </div>
           


<!-- Formulario de registro de productos -->
<div id="modalProducto" class="modal-producto" hidden>

    <div class="modal-contenido">

        <div class="modal-encabezado">
            <h2>Registrar producto</h2>
            <button type="button"
                    id="btnCerrarProducto"
                    class="btn-cerrar"
                    aria-label="Cerrar formulario">
                &times;
            </button>
        </div>

        <form id="formProducto">

            <div class="campo-formulario">
                <label for="nombreProducto">Nombre</label>
                <input type="text"
                       id="nombreProducto"
                       required>
            </div>

            <div class="fila-formulario">

                <div class="campo-formulario">
                    <label for="tipoProducto">Tipo de producto</label>
                    <select id="tipoProducto" required>
                        <option value="I">Ingrediente</option>
                        <option value="P">Producto terminado</option>
                    </select>
                </div>

                <div class="campo-formulario">
                    <label for="codigoProducto">Código</label>
                    <input type="text"
                           id="codigoProducto"
                           placeholder="Se sugerirá automáticamente"
                           required>
                </div>

            </div>

            <div class="fila-formulario">

                <div class="campo-formulario">
                    <label for="unidadProducto">Unidad de medida</label>
                    <select id="unidadProducto" required>
                        <option value="UNIDAD">Unidad</option>
                        <option value="LIBRA">Libra</option>
                        <option value="KILOGRAMO">Kilogramo</option>
                        <option value="LITRO">Litro</option>
                    </select>
                </div>

                <div class="campo-formulario">
                    <label for="precioProducto">Precio (Q)</label>
                    <input type="number"
                           id="precioProducto"
                           min="0"
                           step="0.01"
                           value="0"
                           required>
                </div>

            </div>

            <div class="campo-formulario">
                <label for="minimoProducto">Existencia mínima</label>
                <input type="number"
                       id="minimoProducto"
                       min="0"
                       value="0"
                       required>
            </div>

            <p class="nota-formulario">
                La existencia inicial será 0.
                Los movimientos de inventario se gestionarán
                mediante los procesos correspondientes.
            </p>

            <div class="acciones-formulario">
                <button type="button"
                        id="btnCancelarProducto"
                        class="btn-secundario">
                    Cancelar
                </button>

                <button type="submit"
                        class="btn-principal">
                    Guardar producto
                </button>
            </div>

        </form>

    </div>
</div>


<!-- =========================================
     MODAL DE CONFIGURACIÓN DE RECETAS
========================================= -->



<div id="modalReceta" class="modal-producto" hidden>

    <div class="modal-contenido modal-contenido-receta">

        <div class="modal-encabezado">
            <h2>Configurar receta</h2>

            <button type="button"
                    id="btnCerrarReceta"
                    class="btn-cerrar"
                    aria-label="Cerrar formulario de receta">
                &times;
            </button>
        </div>

        <p class="nota-formulario">
            Configura los ingredientes necesarios para
            elaborar el producto terminado.
        </p>

        <form id="formReceta">

            <!-- Producto terminado asociado -->
            <input type="hidden" id="idProductoReceta">

            <div class="campo-formulario">
                <label for="nombreReceta">
                    Nombre de la receta
                </label>

                <input type="text"
                       id="nombreReceta"
                       placeholder="Ej. Receta de Pan Francés"
                       required>
            </div>

            <div class="campo-formulario">
                <label for="rendimientoReceta">
                    Rendimiento de la receta
                </label>

                <input type="number"
                       id="rendimientoReceta"
                       min="0.01"
                       step="any"
                       placeholder="Ej. 20"
                       required>

                <small>
                    Cantidad de producto terminado obtenida
                    con esta receta, según su unidad de medida.
                </small>
            </div>

            <h3>Ingredientes de la receta</h3>

            <p class="nota-formulario">
                Selecciona los ingredientes previamente
                registrados e indica sus cantidades.
            </p>

            <!-- Aquí agregaremos las filas dinámicamente -->
            <div id="contenedorIngredientesReceta"></div>

            <button type="button"
                    id="btnAgregarIngrediente"
                    class="button-89">
                + Agregar ingrediente
            </button>

            <div class="acciones-formulario">

                <button type="button"
                        id="btnCancelarReceta"
                        class="button-89">
                    Cancelar
                </button>

                <button type="submit"
                        id="btnGuardarReceta"
                        class="button-89">
                    Guardar receta
                </button>

            </div>

        </form>

    </div>

</div>

 
<!-- =====================================
     MODAL PARA CONSULTAR RECETAS
===================================== -->

<div id="modalVerReceta" class="modal-producto" hidden>

    <div class="modal-contenido modal-contenido-receta">

        <div class="modal-encabezado">

            <h2 id="tituloVerReceta">Detalle de receta</h2>

            <button
                type="button"
                id="btnCerrarVerReceta"
                class="btn-cerrar">
                &times;
            </button>

        </div>

        <div class="detalle-receta-info">

            <p>
                <strong>Producto:</strong>
                <span id="productoVerReceta">-</span>
            </p>

            <p>
                <strong>Rendimiento:</strong>
                <span id="rendimientoVerReceta">-</span>
            </p>

            <p>
                <strong>Estado:</strong>
                <span id="estadoVerReceta">-</span>
            </p>

        </div>

        <h3>Ingredientes de la receta</h3>

        <div class="tabla-receta-contenedor">

            <table class="tabla-detalle-receta">

                <thead>
                    <tr>
                        <th>Ingrediente</th>
                        <th>Cantidad</th>
                        <th>Unidad</th>
                    </tr>
                </thead>

                <tbody id="cuerpoDetalleReceta">
                </tbody>

            </table>

        </div>

        <div class="acciones-receta">
               
<button
    type="button"
    id="btnEditarDesdeDetalle"
    class="btn-editar-receta">
    ✏ Editar receta
</button>

            <button
                type="button"
                id="btnCerrarDetalleReceta"
                class="button-89">
                Cerrar
            </button>

        </div>

    </div>

</div>
       

        </section>

    </main>

    <!-- PIE DE PÁGINA -->
    <footer class="pie-pagina">
        <p>SAN CARLOS © 2026 | SISTEMA DE GESTIÓN</p>
    </footer>



<!-- MODAL INGRESAR EXISTENCIAS -->
<div id="modalIngresarExistencias" class="modal-producto" hidden>

    <div class="modal-contenido">

        <div class="modal-encabezado">
            <h2>Ingresar existencias</h2>

            <button type="button"
                    id="btnCerrarIngreso"
                    class="btn-cerrar">
                &times;
            </button>
        </div>

        <form id="formIngresarExistencias">

            <div class="campo-formulario">
                <label for="insumoIngreso">Ingrediente</label>

                <select id="insumoIngreso" required>
                    <option value="">Seleccionar ingrediente</option>
                </select>
            </div>

            <div class="campo-formulario">
                <label for="cantidadIngreso">
                    Cantidad recibida
                </label>

                <input type="number"
                       id="cantidadIngreso"
                       min="0.0001"
                       step="any"
                       placeholder="Ej. 25"
                       required>
            </div>

            <div class="campo-formulario">
                <p>
                    <strong>Existencia actual:</strong>
                    <span id="existenciaActualIngreso">—</span>
                </p>

                <p>
                    <strong>Nueva existencia:</strong>
                    <span id="existenciaNuevaIngreso">—</span>
                </p>
            </div>

            <div class="acciones-formulario">

                <button type="button"
                        id="btnCancelarIngreso"
                        class="btn-secundario">
                    Cancelar
                </button>

                <button type="submit"
                        id="btnGuardarIngreso"
                        class="btn-principal">
                    Guardar ingreso
                </button>

            </div>
        </form>
    </div>
</div>
    
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.8/dist/chart.umd.min.js"></script>
<script src="../js/productos.js?v=7"></script>


</body>
</html>
