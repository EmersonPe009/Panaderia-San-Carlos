const ES_EMPLEADO = window.ROL_USUARIO === "SUCURSAL";
const ES_ADMIN = window.ROL_USUARIO === "ADMINISTRADOR";
const SUCURSAL_EMPLEADO = Number(window.ID_SUCURSAL || 0);


const API_PEDIDOS = "../webapi/Pedidos";
const API_SUCURSALES = "../webapi/Sucursales/CargarSucursales";
const API_PRODUCTOS = "../webapi/Productos/CargarProductos";

let pedidos = [];
let sucursales = [];
let productos = [];
let detallesPedidos = new Map();
let guardandoPedido = false;
let despachandoPedido = false;


document.addEventListener("DOMContentLoaded", async () => {

    // ========================================
    // BOTONES DEL FORMULARIO DE PEDIDOS
    // ========================================

    document.getElementById("btnNuevoPedido")
        .addEventListener("click", abrirModalPedido);

    document.getElementById("cerrarModalPedido")
        .addEventListener("click", cerrarModalPedido);

    document.getElementById("btnCancelarPedido")
        .addEventListener("click", cerrarModalPedido);

    document.getElementById("btnAgregarProducto")
        .addEventListener("click", agregarFilaProducto);

    document.getElementById("formPedido")
        .addEventListener("submit", guardarPedido);


    // ========================================
    // FILTROS Y ACTUALIZACIÓN
    // ========================================

    document.getElementById("btnActualizar")
        .addEventListener("click", actualizarTodo);

    document.getElementById("buscarPedido")
        .addEventListener("input", mostrarPedidos);

    document.getElementById("filtroEstado")
        .addEventListener("change", mostrarPedidos);
        
        document.getElementById("filtroTurno")
    .addEventListener("change", mostrarPedidos);

    // ========================================
    // MODAL DE DETALLES
    // ========================================

    document.getElementById("cerrarModalDetalle")
        .addEventListener("click", cerrarModalDetalle);

    document.getElementById("btnCerrarDetalle")
        .addEventListener("click", cerrarModalDetalle);
        
        
document.getElementById("cerrarModalResto")
    .addEventListener("click", cerrarModalResto);

document.getElementById("btnCancelarResto")
    .addEventListener("click", cerrarModalResto);

document.getElementById("formResto")
    .addEventListener("submit", guardarResto);

document.getElementById("tablaRegistroResto")
    .addEventListener("input", evento => {

        if (!evento.target.matches(".cantidad-resto")) {
            return;
        }

        const fila = evento.target.closest("tr");
        const cantidadDespachada = Number(
            evento.target.max
        );
        const texto = evento.target.value.trim();
        const resto = Number(texto);

        fila.querySelector(".unidades-vendidas")
            .textContent =
                texto !== "" &&
                Number.isSafeInteger(resto) &&
                resto >= 0 &&
                resto <= cantidadDespachada
                    ? cantidadDespachada - resto
                    : "—";
    });


    // ========================================
    // ELIMINAR PRODUCTOS DEL FORMULARIO
    // ========================================

    document.getElementById("listaProductosPedido")
        .addEventListener("click", evento => {

            const boton = evento.target.closest(".btn-eliminar");

            if (boton) {
                boton.closest(".fila-producto").remove();
            }

        });


    // ========================================
    // ACCIONES DE LOS PEDIDOS
    // ========================================

    document.addEventListener("click", async evento => {

        const boton = evento.target.closest(
            "button[data-pedido-accion]"
        );

        if (!boton) return;

        const id = Number(boton.dataset.id);

        if (!Number.isInteger(id) || id <= 0) {
            return;
        }
        
        if (boton.dataset.pedidoAccion === "ver") {
            await verDetallePedido(id);
        }

        if (boton.dataset.pedidoAccion === "despachar") {

            // Solo el administrador puede despachar
            if (window.ROL_USUARIO !== "ADMINISTRADOR") {
                await notificar(
                    "warning",
                    "Acceso denegado",
                    "No tienes permiso para despachar pedidos."
                );
                return;
            }

            await despacharPedido(id);
            
if (boton.dataset.pedidoAccion === "resto") {
    if (!ES_EMPLEADO) {
        return;
    }

    await abrirRegistroResto(id);
}

        }
        
        
if (boton.dataset.pedidoAccion === "resto") {

    console.log("Botón RESTO presionado. Pedido:", id);

    if (!ES_EMPLEADO) {
        await notificar(
            "warning",
            "Acceso denegado",
            "Solo los empleados pueden registrar RESTO."
        );
        return;
    }

    await abrirRegistroResto(id);
}

    });


    // ========================================
    // CONFIGURACIÓN SEGÚN EL ROL
    // ========================================

    const esEmpleado =
        window.ROL_USUARIO === "SUCURSAL";

    const selectorSucursal =
        document.getElementById("sucursalPedido");

    if (esEmpleado) {

        selectorSucursal.disabled = true;

    } else {

        selectorSucursal.disabled = false;

    }


    // ========================================
    // CARGAR DATOS INICIALES
    // ========================================

    await actualizarTodo();


    // ========================================
    // NAVEGACIÓN DESDE EL PANEL EMPLEADO
    // ========================================

    const parametros =
        new URLSearchParams(window.location.search);

    const vista = parametros.get("vista");

    if (vista === "nuevo") {

        // Abrir automáticamente nuevo pedido
        await abrirModalPedido();

    } else if (vista === "despachos") {

        // Mostrar solamente despachados
        document.getElementById("filtroEstado").value =
            "DESPACHADO";

        mostrarPedidos();

        document.getElementById("buscarPedido")
            .scrollIntoView({
                behavior: "smooth",
                block: "center"
            });

    } else if (vista === "pedidos") {

        // Mostrar historial completo
        document.getElementById("filtroEstado").value = "";

        mostrarPedidos();

        document.getElementById("buscarPedido")
            .scrollIntoView({
                behavior: "smooth",
                block: "center"
            });

    }

});


async function obtenerJSON(url, opciones = {}) {

    const respuesta = await fetch(url, opciones);
    const texto = await respuesta.text();

    let datos;

    try {
        datos = texto ? JSON.parse(texto) : null;
    } catch (error) {
        throw new Error("El servidor devolvió una respuesta no válida.");
    }

    if (!respuesta.ok) {
        throw new Error(datos?.MENSAJE || "Error HTTP " + respuesta.status);
    }

    return datos;
}

function escaparHTML(valor) {
    return String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

function notificar(icono, titulo, texto = "") {
    return Swal.fire({
        icon: icono,
        title: titulo,
        text: texto,
        confirmButtonColor: "#2563eb"
    });
}

function fechaLocal() {
    const hoy = new Date();
    const anio = hoy.getFullYear();
    const mes = String(hoy.getMonth() + 1).padStart(2, "0");
    const dia = String(hoy.getDate()).padStart(2, "0");
    return `${anio}-${mes}-${dia}`;
}

function formatearFecha(fecha) {

    if (!fecha) return "Sin fecha";

    const texto = String(fecha);
    const partes = texto.match(
        /^(\d{4})-(\d{2})-(\d{2})(?:[ T](\d{2}):(\d{2}))?/
    );

    if (!partes) return texto;

    const [, anio, mes, dia, hora, minuto] = partes;

    return `${dia}/${mes}/${anio}` +
        (hora ? ` ${hora}:${minuto}` : "");
}

function productosTerminados() {
    return productos.filter(p =>
        String(p.TIPO_PRODUCTO).toUpperCase() === "P" &&
        String(p.ESTADO) === "1"
    );
}

function sucursalesActivas() {
    return sucursales.filter(s =>
        String(s.ESTADO) === "1"
    );
}

function nombreSucursal(id) {
    const sucursal = sucursales.find(s =>
        Number(s.ID_SUCURSAL) === Number(id)
    );

    return sucursal
        ? sucursal.NOMBRE
        : "Sucursal #" + id;
}

function nombreProducto(id) {
    const producto = productos.find(p =>
        Number(p.ID_PRODUCTO) === Number(id)
    );

    return producto
        ? producto.NOMBRE
        : "Producto #" + id;
}

async function actualizarTodo() {

    try {

        const [datosPedidos, datosSucursales, datosProductos] =
            await Promise.all([
                obtenerJSON(API_PEDIDOS + "/CargarPedidos"),
                obtenerJSON(API_SUCURSALES),
                obtenerJSON(API_PRODUCTOS)
            ]);

        pedidos = Array.isArray(datosPedidos) ? datosPedidos : [];
        // El servidor ya limita los pedidos según la sesión.
        sucursales = Array.isArray(datosSucursales)
            ? datosSucursales : [];
        productos = Array.isArray(datosProductos)
            ? datosProductos : [];

        detallesPedidos.clear();

        mostrarPedidos();
        mostrarSiguientePedido();

        await actualizarEstadisticas();

    } catch (error) {
        await notificar(
            "error",
            "Error al cargar pedidos",
            error.message
        );
    }
}

async function cargarDetalles(id) {

    if (detallesPedidos.has(id)) {
        return detallesPedidos.get(id);
    }

    const datos = await obtenerJSON(
        API_PEDIDOS +
        "/CargarDetallesPedido?id_pedido=" + id
    );

    const detalles = Array.isArray(datos) ? datos : [];
    detallesPedidos.set(id, detalles);

    return detalles;
}

async function actualizarEstadisticas() {

    document.getElementById("totalPedidos")
        .textContent = pedidos.length;

    document.getElementById("totalPendientes")
        .textContent = pedidos.filter(p =>
            p.ESTADO === "PENDIENTE"
        ).length;

    document.getElementById("totalDespachados")
        .textContent = pedidos.filter(p =>
            p.ESTADO === "DESPACHADO"
        ).length;

    const pendientes = pedidos.filter(p =>
        p.ESTADO === "PENDIENTE"
    );

    const resultados = await Promise.all(
        pendientes.map(p =>
            cargarDetalles(Number(p.ID_PEDIDO))
        )
    );

    const totalUnidades = resultados.reduce(
        (total, detalles) =>
            total + detalles.reduce(
                (suma, d) => suma + Number(d.CANTIDAD || 0),
                0
            ),
        0
    );

    document.getElementById("totalUnidadesPendientes")
        .textContent = totalUnidades;
}


function mostrarPedidos() {

    const buscar = document.getElementById(
        "buscarPedido"
    ).value.trim();

    const filtroEstado = document.getElementById(
        "filtroEstado"
    ).value;

    const filtroTurno = document.getElementById(
        "filtroTurno"
    ).value;

    const filtrados = pedidos.filter(p => {

        const coincideBusqueda =
            String(p.ID_PEDIDO).includes(buscar);

        const coincideEstado =
            !filtroEstado || p.ESTADO === filtroEstado;

        const coincideTurno =
            !filtroTurno || p.TURNO === filtroTurno;

        return coincideBusqueda &&
               coincideEstado &&
               coincideTurno;
    });

    const tabla = document.getElementById("tablaPedidos");

    if (filtrados.length === 0) {
        tabla.innerHTML = `
            <tr>
                <td colspan="6">
                    No se encontraron pedidos.
                </td>
            </tr>
        `;
        return;
    }

    tabla.innerHTML = filtrados
        .sort((a, b) =>
            Number(b.ID_PEDIDO) - Number(a.ID_PEDIDO)
        )
        .map(p => {

            const id = Number(p.ID_PEDIDO);
            const estado = String(p.ESTADO || "");
            const turno = String(p.TURNO || "MAÑANA");

            return `
                <tr>
                    <td>#${id}</td>

                    <td>
                        ${escaparHTML(
                            nombreSucursal(p.ID_SUCURSAL)
                        )}
                    </td>

                    <td>
                        ${escaparHTML(
                            formatearFecha(p.FECHA_PEDIDO)
                        )}
                    </td>

                    <td>
                        ${escaparHTML(turno)}
                    </td>

                    <td>
                        <span class="estado estado-${escaparHTML(estado)}">
                            ${escaparHTML(estado)}
                        </span>
                    </td>

                    <td>
                        <div class="acciones">
                            <button
                                class="btn btn-ver"
                                data-pedido-accion="ver"
                                data-id="${id}">
                                Ver detalles
                            </button>

                            ${ES_ADMIN && estado === "PENDIENTE" ? `
                                <button
                                    class="btn btn-despachar"
                                    data-pedido-accion="despachar"
                                    data-id="${id}">
                                    Despachar
                                </button>
                            ` : ""}
                            
${ES_EMPLEADO && estado === "DESPACHADO" ? `
    <button
        class="btn btn-ver"
        data-pedido-accion="resto"
        data-id="${id}">
        Registrar RESTO
    </button>
` : ""}

                        </div>
                    </td>
                </tr>
            `;
        }).join("");
}


function mostrarSiguientePedido() {

    const contenedor = document.getElementById(
        "siguientePedido"
    );

    const pendientes = pedidos
        .filter(p => p.ESTADO === "PENDIENTE")
        .sort((a, b) =>
            Number(a.ID_PEDIDO) - Number(b.ID_PEDIDO)
        );

    if (pendientes.length === 0) {
        contenedor.innerHTML = `
            <p class="mensaje-vacio">
                No hay pedidos pendientes.
            </p>
        `;
        return;
    }

    const pedido = pendientes[0];
    const id = Number(pedido.ID_PEDIDO);

    contenedor.innerHTML = `
        <div class="turno">
            <h3>Pedido #${id}</h3>

            <p>
                <strong>Sucursal:</strong>
                ${escaparHTML(nombreSucursal(pedido.ID_SUCURSAL))}
            </p>

            <p>
                <strong>Fecha:</strong>
                ${escaparHTML(formatearFecha(pedido.FECHA_PEDIDO))}
            </p>

            <p>
                <strong>Estado:</strong>
                <span class="estado estado-PENDIENTE">
                    PENDIENTE
                </span>
            </p>

            <div class="acciones">
                <button
                    class="btn btn-ver"
                    data-pedido-accion="ver"
                    data-id="${id}">
                    Ver detalles
                </button>

                ${ES_ADMIN ? `<button
                    class="btn btn-despachar"
                    data-pedido-accion="despachar"
                    data-id="${id}">
                    Despachar pedido
                </button>` : ""}
            </div>
        </div>
    `;
}

async function abrirModalPedido() {

    if (sucursales.length === 0 ||
        productos.length === 0) {
        await actualizarTodo();
    }

    if (ES_EMPLEADO && !SUCURSAL_EMPLEADO) {
        await notificar("error", "Sin sucursal", "Tu usuario no tiene sucursal asignada.");
        return;
    }
    if (ES_EMPLEADO && !sucursalesActivas().some(s => Number(s.ID_SUCURSAL) === SUCURSAL_EMPLEADO)) {
        await notificar("warning", "Sucursal no disponible", "Tu sucursal no está activa.");
        return;
    }
    if (sucursalesActivas().length === 0) {
        await notificar(
            "warning",
            "Sin sucursales activas",
            "Debes registrar una sucursal activa."
        );
        return;
    }

    if (productosTerminados().length === 0) {
        await notificar(
            "warning",
            "Sin productos terminados",
            "Debes registrar productos terminados activos."
        );
        return;
    }

    
document.getElementById("formPedido").reset();

document.getElementById("fechaPedido")
    .value = fechaLocal();

// Turno predeterminado
document.getElementById("turnoPedido")
    .value = "MAÑANA";

    const selector = document.getElementById(
        "sucursalPedido"
    );

    const opcionesSucursal = ES_EMPLEADO
        ? sucursalesActivas().filter(s => Number(s.ID_SUCURSAL) === SUCURSAL_EMPLEADO)
        : sucursalesActivas();
    selector.innerHTML = `
        <option value="">Seleccione una sucursal</option>
        ${opcionesSucursal.map(s => `
            <option value="${Number(s.ID_SUCURSAL)}">
                ${escaparHTML(s.NOMBRE)}
            </option>
        `).join("")}
    `;

    if (ES_EMPLEADO) {
        selector.value = String(SUCURSAL_EMPLEADO);
        selector.disabled = true;
    } else {
        selector.disabled = false;
    }
    document.getElementById("listaProductosPedido")
        .innerHTML = "";

    agregarFilaProducto();

    document.getElementById("modalPedido").hidden = false;
}

function cerrarModalPedido() {
    document.getElementById("modalPedido").hidden = true;
}

function agregarFilaProducto() {

    const contenedor = document.getElementById(
        "listaProductosPedido"
    );

    const fila = document.createElement("div");
    fila.className = "fila-producto";

    const opciones = productosTerminados()
        .map(p => `
            <option value="${Number(p.ID_PRODUCTO)}">
                ${escaparHTML(p.NOMBRE)}
            </option>
        `).join("");

    fila.innerHTML = `
        <select class="producto-seleccionado" required>
            <option value="">Seleccione un producto</option>
            ${opciones}
        </select>

        <input
            type="number"
            class="cantidad-producto"
            placeholder="Cantidad"
            min="1"
            step="1"
            required>

        <button
            type="button"
            class="btn btn-eliminar">
            Eliminar
        </button>
    `;

    contenedor.appendChild(fila);
}


async function guardarPedido(evento) {

    evento.preventDefault();

    if (guardandoPedido) return;

    // ========================================
    // DATOS GENERALES DEL PEDIDO
    // ========================================

    const idSucursal = ES_EMPLEADO
        ? SUCURSAL_EMPLEADO
        : Number(
            document.getElementById("sucursalPedido").value
        );

    const fecha = document.getElementById(
        "fechaPedido"
    ).value;

    const turno = document.getElementById(
        "turnoPedido"
    ).value;

    // ========================================
    // VALIDAR TURNO
    // ========================================

    if (turno !== "MAÑANA" && turno !== "TARDE") {

        await notificar(
            "warning",
            "Turno incorrecto",
            "Selecciona el turno de mañana o tarde."
        );

        return;
    }

    // ========================================
    // OBTENER PRODUCTOS SELECCIONADOS
    // ========================================

    const seleccionados = new Map();

    for (const fila of document.querySelectorAll(
        "#listaProductosPedido .fila-producto"
    )) {

        const id = Number(
            fila.querySelector(".producto-seleccionado").value
        );

        const cantidad = Number(
            fila.querySelector(".cantidad-producto").value
        );

        if (!Number.isInteger(id) || id <= 0 ||
            !Number.isSafeInteger(cantidad) || cantidad <= 0) {

            await notificar(
                "warning",
                "Datos incorrectos",
                "Selecciona productos y cantidades enteras positivas."
            );

            return;
        }

        seleccionados.set(
            id,
            (seleccionados.get(id) || 0) + cantidad
        );
    }

    // ========================================
    // VALIDAR DATOS OBLIGATORIOS
    // ========================================

    if (!Number.isInteger(idSucursal) ||
        idSucursal <= 0 ||
        !fecha ||
        seleccionados.size === 0) {

        await notificar(
            "warning",
            "Datos incompletos",
            "Selecciona sucursal, fecha, turno y productos."
        );

        return;
    }

    // ========================================
    // PREPARAR PRODUCTOS PARA EL BACKEND
    // ========================================

    const productosEnviar = Array.from(
        seleccionados.entries()
    ).map(([id, cantidad]) => ({
        id_producto: id,
        cantidad: cantidad
    }));

    // ========================================
    // CONFIRMAR PEDIDO
    // ========================================

    const confirmacion = await Swal.fire({
        icon: "question",
        title: "Registrar pedido",
        text: "Turno: " + turno +
              ". El pedido quedará PENDIENTE. " +
              "No se descontará inventario hasta despacharlo.",
        showCancelButton: true,
        confirmButtonText: "Registrar",
        cancelButtonText: "Cancelar"
    });

    if (!confirmacion.isConfirmed) return;

    guardandoPedido = true;

    const boton = document.getElementById(
        "btnGuardarPedido"
    );

    boton.disabled = true;

    // ========================================
    // ENVIAR PEDIDO A ORACLE
    // ========================================

    try {

        const resultado = await obtenerJSON(
            API_PEDIDOS + "/InsPedidoCompleto",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    id_sucursal: idSucursal,
                    fecha_pedido: fecha,
                    turno: turno,
                    productos: productosEnviar
                })
            }
        );

        if (!resultado.EXITO) {
            throw new Error(resultado.MENSAJE);
        }

        cerrarModalPedido();

        await notificar(
            "success",
            "Pedido registrado",
            "Pedido #" + resultado.ID_PEDIDO +
            " - Turno: " + turno
        );

        await actualizarTodo();

    } catch (error) {

        await notificar(
            "error",
            "No se pudo registrar el pedido",
            error.message
        );

    } finally {

        guardandoPedido = false;
        boton.disabled = false;
    }
}


async function verDetallePedido(id) {

    try {

        const [cabecera, detalles] = await Promise.all([
            obtenerJSON(
                API_PEDIDOS + "/GetPedido?id_pedido=" + id
            ),
            cargarDetalles(id)
        ]);

        if (!Array.isArray(cabecera) ||
            cabecera.length === 0) {
            throw new Error("Pedido no encontrado.");
        }

        const pedido = cabecera[0];

        document.getElementById("detalleIdPedido")
            .textContent = pedido.ID_PEDIDO;

        document.getElementById("detalleSucursal")
            .textContent = nombreSucursal(pedido.ID_SUCURSAL);

        document.getElementById("detalleFecha")
            .textContent = formatearFecha(pedido.FECHA_PEDIDO);

        document.getElementById("detalleEstado")
            .textContent = pedido.ESTADO;

        document.getElementById("detalleDespacho")
            .textContent = formatearFecha(pedido.FECHA_DESPACHO);

        document.getElementById("tablaDetallePedido")
            .innerHTML = detalles.length
                ? detalles.map(d => `
                    <tr>
                        <td>${escaparHTML(nombreProducto(d.ID_PRODUCTO))}</td>
                        <td>${escaparHTML(d.CANTIDAD)}</td>
                    </tr>
                `).join("")
                : `<tr>
                    <td colspan="2">Sin productos.</td>
                   </tr>`;

        document.getElementById("modalDetallePedido")
            .hidden = false;

    } catch (error) {
        await notificar(
            "error",
            "Error al consultar pedido",
            error.message
        );
    }
}

function cerrarModalDetalle() {
    document.getElementById("modalDetallePedido")
        .hidden = true;
}

async function despacharPedido(id) {
    if (!ES_ADMIN) return;

    if (despachandoPedido) return;

    const confirmacion = await Swal.fire({
        icon: "warning",
        title: "¿Despachar pedido #" + id + "?",
        text: "Se descontarán los productos terminados del inventario. Esta operación no se puede revertir desde este módulo.",
        showCancelButton: true,
        confirmButtonText: "Sí, despachar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#16a34a"
    });

    if (!confirmacion.isConfirmed) return;

    despachandoPedido = true;

    try {

        const resultado = await obtenerJSON(
            API_PEDIDOS + "/DespacharPedido",
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    id_pedido: id
                })
            }
        );

        if (!resultado.EXITO) {
            throw new Error(resultado.MENSAJE);
        }

        await notificar(
            "success",
            "Pedido despachado",
            resultado.MENSAJE
        );

        await actualizarTodo();

    } catch (error) {

        await notificar(
            "error",
            "No se pudo despachar",
            error.message
        );

    } finally {
        despachandoPedido = false;
    }
}



let pedidoRestoActual = null;
let guardandoResto = false;

async function abrirRegistroResto(idPedido) {

    try {
        const pedido = pedidos.find(p =>
            Number(p.ID_PEDIDO) === Number(idPedido)
        );

        if (!pedido || pedido.ESTADO !== "DESPACHADO") {
            throw new Error("El pedido no está despachado.");
        }

        if (!ES_EMPLEADO ||
            Number(pedido.ID_SUCURSAL) !== SUCURSAL_EMPLEADO) {
            throw new Error("No tienes acceso a este pedido.");
        }

        // Consultar nuevamente para obtener datos actuales.
        detallesPedidos.delete(idPedido);

        const detalles = await cargarDetalles(idPedido);
        
        const detallesPendientes = detalles.filter(
    d => Number(d.RESTO_REGISTRADO) !== 1
);

if (detallesPendientes.length === 0) {
    await notificar(
        "info",
        "RESTO ya registrado",
        "Todos los productos de este pedido ya tienen sus sobrantes reportados."
    );
    return;
}

        if (detalles.length === 0) {
            throw new Error("El pedido no tiene productos.");
        }

        pedidoRestoActual = idPedido;

        document.getElementById("informacionPedidoResto")
            .textContent = "Pedido #" + idPedido +
            " | " + nombreSucursal(pedido.ID_SUCURSAL);

        const tabla = document.getElementById(
            "tablaRegistroResto"
        );

        detallesPendientes.map(d => {
            const cantidad = Number(d.CANTIDAD);
            const idDetalle = Number(d.ID_DETALLE_PEDIDO);

            return `
                <tr data-id-detalle="${idDetalle}">
                    <td>${escaparHTML(nombreProducto(d.ID_PRODUCTO))}</td>
                    <td>${cantidad}</td>
                    <td>
                        <input
                            type="number"
                            class="cantidad-resto"
                            min="0"
                            max="${cantidad}"
                            step="1"
                            required
                            placeholder="0"
                            style="width:90px;padding:8px;">
                    </td>
                    <td class="unidades-vendidas">—</td>
                </tr>
            `;
        }).join("");

        document.getElementById("modalResto").hidden = false;

    } catch (error) {
        await notificar(
            "error",
            "No se pudo abrir RESTO",
            error.message
        );
    }
}

function cerrarModalResto() {
    if (guardandoResto) return;

    document.getElementById("modalResto").hidden = true;
    pedidoRestoActual = null;
}

async function guardarResto(evento) {

    evento.preventDefault();

    if (guardandoResto || pedidoRestoActual === null) {
        return;
    }

    const filas = Array.from(
        document.querySelectorAll(
            "#tablaRegistroResto tr[data-id-detalle]"
        )
    );

    const registros = [];

    for (const fila of filas) {

        const input = fila.querySelector(".cantidad-resto");
        const cantidad = Number(input.value);
        const maximo = Number(input.max);
        const idDetalle = Number(fila.dataset.idDetalle);

        if (
            input.value.trim() === "" ||
            !Number.isSafeInteger(cantidad) ||
            cantidad < 0 ||
            cantidad > maximo ||
            !Number.isSafeInteger(idDetalle) ||
            idDetalle <= 0
        ) {
            await notificar(
                "warning",
                "Cantidad incorrecta",
                "El RESTO debe ser un número entero entre 0 y la cantidad despachada."
            );
            input.focus();
            return;
        }

        registros.push({
            id_detalle_pedido: idDetalle,
            cantidad_resto: cantidad
        });
    }

    const confirmacion = await Swal.fire({
        icon: "question",
        title: "¿Guardar RESTO?",
        text: "Se registrarán los sobrantes de los productos del pedido.",
        showCancelButton: true,
        confirmButtonText: "Guardar",
        cancelButtonText: "Cancelar"
    });

    if (!confirmacion.isConfirmed) return;

    guardandoResto = true;

    const boton = document.getElementById("btnGuardarResto");
    boton.disabled = true;

    try {

        for (const registro of registros) {

            const resultado = await obtenerJSON(
                API_PEDIDOS + "/RegistrarResto",
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(registro)
                }
            );

            if (!resultado.EXITO) {
                throw new Error(resultado.MENSAJE);
            }
        }

        detallesPedidos.delete(pedidoRestoActual);

        await notificar(
            "success",
            "RESTO registrado",
            "Se guardaron los sobrantes del pedido."
        );

        document.getElementById("modalResto").hidden = true;
        pedidoRestoActual = null;

        await actualizarTodo();

    } catch (error) {

        await notificar(
            "error",
            "No se pudo completar el registro",
            "Algunos productos podrían haberse guardado. " +
            error.message
        );

    } finally {

        guardandoResto = false;
        boton.disabled = false;
    }
}
