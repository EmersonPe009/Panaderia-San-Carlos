
const API_PRODUCCION = "../webapi/Produccion";
const API_PRODUCTOS = "../webapi/Productos/CargarProductos";

let producciones = [];
let productos = [];
let guardandoProduccion = false;
let demandaProduccion = [];

document.addEventListener("DOMContentLoaded", async () => {
    
document.getElementById("btnGenerarProduccionConsolidada")
    ?.addEventListener("click", generarProduccionConsolidada);

    document.getElementById("btnNuevaProduccion")
        .addEventListener("click", abrirModalProduccion);

    document.getElementById("cerrarModalProduccion")
        .addEventListener("click", cerrarModalProduccion);

    document.getElementById("btnCancelarProduccion")
        .addEventListener("click", cerrarModalProduccion);

    document.getElementById("btnAgregarProducto")
        .addEventListener("click", () => agregarFilaProducto());

    document.getElementById("formProduccion")
        .addEventListener("submit", guardarProduccion);

    document.getElementById("btnActualizar")
        .addEventListener("click", cargarProducciones);

    document.getElementById("buscarProduccion")
        .addEventListener("input", mostrarProducciones);

    document.getElementById("filtroEstado")
        .addEventListener("change", mostrarProducciones);

document.getElementById("filtroFechaDemanda")
    .addEventListener("change", mostrarDemandaProduccion);

document.getElementById("filtroTurnoDemanda")
    .addEventListener("change", mostrarDemandaProduccion);

document.getElementById("btnActualizarDemanda")
    .addEventListener("click", cargarDemandaProduccion);


    document.getElementById("cerrarModalDetalle")
        .addEventListener("click", cerrarModalDetalle);

    document.getElementById("btnCerrarDetalle")
        .addEventListener("click", cerrarModalDetalle);

    document.getElementById("tablaProducciones")
        .addEventListener("click", manejarAccionesProduccion);

    document.getElementById("listaProductosProduccion")
        .addEventListener("click", evento => {

            const boton = evento.target.closest(".btn-eliminar");

            if (boton) {
                boton.closest(".fila-producto").remove();
            }
        });

   
await Promise.all([
    cargarProductos(),
    cargarProducciones(),
    cargarDemandaProduccion()
]);

});

async function obtenerJSON(url, opciones = {}) {

    const respuesta = await fetch(url, opciones);
    const texto = await respuesta.text();

    let datos;

    try {
        datos = texto ? JSON.parse(texto) : null;
    } catch (error) {
        throw new Error(
            "El servidor devolvió una respuesta no válida."
        );
    }

    if (!respuesta.ok) {
        throw new Error(
            datos?.MENSAJE ||
            "Error HTTP " + respuesta.status
        );
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

// PRODUCTOS

async function cargarProductos() {

    try {

        const datos = await obtenerJSON(API_PRODUCTOS);

        productos = Array.isArray(datos) ? datos : [];
        mostrarRecomendacionesProduccion();

    } catch (error) {

        productos = [];

        await notificar(
            "error",
            "Error al cargar productos",
            error.message
        );
    }
}

function productosTerminados() {

    return productos.filter(producto =>
        String(producto.TIPO_PRODUCTO).toUpperCase() === "P" &&
        String(producto.ESTADO) === "1"
    );
}

function nombreProducto(id) {

    const producto = productos.find(p =>
        Number(p.ID_PRODUCTO) === Number(id)
    );

    return producto
        ? producto.NOMBRE
        : "Producto #" + id;
}

// PRODUCCIONES

async function cargarProducciones() {

    try {

        const datos = await obtenerJSON(
            API_PRODUCCION + "/CargarProducciones"
        );

        producciones = Array.isArray(datos) ? datos : [];

        actualizarEstadisticas();
        mostrarProducciones();
        mostrarSiguienteProduccion();

    } catch (error) {

        await notificar(
            "error",
            "Error al cargar producciones",
            error.message
        );
    }
}

function actualizarEstadisticas() {

    document.getElementById("totalProducciones")
        .textContent = producciones.length;

    document.getElementById("totalPendientes")
        .textContent = producciones.filter(p =>
            p.ESTADO === "PENDIENTE"
        ).length;

    document.getElementById("totalProcesando")
        .textContent = producciones.filter(p =>
            p.ESTADO === "PROCESANDO"
        ).length;

    document.getElementById("totalFinalizadas")
        .textContent = producciones.filter(p =>
            p.ESTADO === "FINALIZADA"
        ).length;
}

function formatearFechaProduccion(fecha) {

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

function mostrarProducciones() {

    const buscar = document.getElementById(
        "buscarProduccion"
    ).value.trim();

    const estado = document.getElementById(
        "filtroEstado"
    ).value;

    const filtradas = producciones.filter(p => {

        const coincideID =
            String(p.ID_PRODUCCION).includes(buscar);

        const coincideEstado =
            !estado || p.ESTADO === estado;

        return coincideID && coincideEstado;
    });

    const tabla = document.getElementById(
        "tablaProducciones"
    );

    if (filtradas.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="4">
                    No se encontraron producciones.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML = filtradas
        .sort((a, b) =>
            Number(b.ID_PRODUCCION) -
            Number(a.ID_PRODUCCION)
        )
        .map(p => {

            const id = Number(p.ID_PRODUCCION);
            const estado = String(p.ESTADO || "");

            const puedeIniciar = estado === "PENDIENTE";
            const puedeFinalizar = estado === "PROCESANDO";

            return `
                <tr>
                    <td>#${id}</td>

                    <td>
                       ${escaparHTML(formatearFechaProduccion(p.FECHA_PRODUCCION))}
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
                                data-accion="ver"
                                data-id="${id}">
                                Ver detalles
                            </button>
                                
${puedeIniciar ? `
    <button
        class="btn btn-principal"
        data-accion="iniciar"
        data-id="${id}">
        Iniciar
    </button>
` : ""}

                            ${puedeFinalizar ? `
                                <button
                                    class="btn btn-finalizar"
                                    data-accion="finalizar"
                                    data-id="${id}">
                                    Finalizar
                                </button>
                            ` : ""}

                        </div>
                    </td>
                </tr>
            `;
        }).join("");
}

// NUEVA PRODUCCIÓN

function fechaLocal() {

    const hoy = new Date();

    const anio = hoy.getFullYear();
    const mes = String(hoy.getMonth() + 1).padStart(2, "0");
    const dia = String(hoy.getDate()).padStart(2, "0");

    return `${anio}-${mes}-${dia}`;
}

async function abrirModalProduccion() {

    if (productos.length === 0) {
        await cargarProductos();
    }

    if (productosTerminados().length === 0) {

        await notificar(
            "warning",
            "Sin productos disponibles",
            "No hay productos terminados activos."
        );

        return;
    }

    document.getElementById("formProduccion").reset();

    document.getElementById("fechaProduccion")
        .value = fechaLocal();

    document.getElementById("listaProductosProduccion")
        .innerHTML = "";

    agregarFilaProducto();

    document.getElementById("modalProduccion").hidden = false;
}

function cerrarModalProduccion() {
    document.getElementById("modalProduccion").hidden = true;
}

function agregarFilaProducto() {

    const contenedor = document.getElementById(
        "listaProductosProduccion"
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

async function guardarProduccion(evento) {

    evento.preventDefault();

    if (guardandoProduccion) return;

    const fecha = document.getElementById(
        "fechaProduccion"
    ).value;

    const filas = document.querySelectorAll(
        ".fila-producto"
    );

    const seleccionados = new Map();

    for (const fila of filas) {

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
                "Datos incompletos",
                "Selecciona un producto y una cantidad entera positiva."
            );

            return;
        }

        seleccionados.set(
            id,
            (seleccionados.get(id) || 0) + cantidad
        );
    }

    if (!fecha || seleccionados.size === 0) {

        await notificar(
            "warning",
            "Datos incompletos",
            "Debes ingresar fecha y productos."
        );

        return;
    }

    const productosEnviar = Array.from(
        seleccionados.entries()
    ).map(([id, cantidad]) => ({
        id_producto: id,
        cantidad: cantidad
    }));

    const confirmacion = await Swal.fire({
        icon: "question",
        title: "Registrar producción",
        text: "La producción quedará pendiente. El inventario se actualizará al finalizarla.",
        showCancelButton: true,
        confirmButtonText: "Registrar",
        cancelButtonText: "Cancelar"
    });

    if (!confirmacion.isConfirmed) return;

    guardandoProduccion = true;

    const boton = document.getElementById(
        "btnGuardarProduccion"
    );

    boton.disabled = true;

    try {

        const resultado = await obtenerJSON(
            API_PRODUCCION + "/InsProduccionCompleta",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    fecha_produccion: fecha,
                    productos: productosEnviar
                })
            }
        );

        if (!resultado.EXITO) {
            throw new Error(resultado.MENSAJE);
        }

        cerrarModalProduccion();

        await notificar(
            "success",
            "Producción registrada",
            "ID: " + resultado.ID_PRODUCCION
        );

        await cargarProducciones();

    } catch (error) {

        await notificar(
            "error",
            "No se pudo registrar",
            error.message
        );

    } finally {

        guardandoProduccion = false;
        boton.disabled = false;
    }
}

// ACCIONES

async function manejarAccionesProduccion(evento) {

    const boton = evento.target.closest(
        "button[data-accion]"
    );

    if (!boton) return;

    const id = Number(boton.dataset.id);

    if (!Number.isInteger(id) || id <= 0) return;

    if (boton.dataset.accion === "ver") {
        await verDetalleProduccion(id);
    }
    if (boton.dataset.accion === "iniciar") {
    await iniciarProduccion(id);
    return;
}
    if (boton.dataset.accion === "finalizar") {
        await finalizarProduccion(id);
    }
}

// DETALLES

async function verDetalleProduccion(id) {

    try {

        const [cabecera, detalles] = await Promise.all([
            obtenerJSON(
                API_PRODUCCION +
                "/GetProduccion?id_produccion=" + id
            ),
            obtenerJSON(
                API_PRODUCCION +
                "/CargarDetallesProduccion?id_produccion=" + id
            )
        ]);

        if (!Array.isArray(cabecera) ||
            cabecera.length === 0) {

            throw new Error("Producción no encontrada.");
        }

        const produccion = cabecera[0];

        document.getElementById(
            "detalleIdProduccion"
        ).textContent = produccion.ID_PRODUCCION;

        document.getElementById(
            "detalleFechaProduccion"
        ).textContent = formatearFechaProduccion(
    produccion.FECHA_PRODUCCION
);

        document.getElementById(
            "detalleEstadoProduccion"
        ).textContent = produccion.ESTADO;

        const tabla = document.getElementById(
            "tablaDetalleProduccion"
        );

        tabla.innerHTML = detalles.length
            ? detalles.map(d => `
                <tr>
                    <td>
                        ${escaparHTML(nombreProducto(d.ID_PRODUCTO))}
                    </td>
                    <td>
                        ${escaparHTML(d.CANTIDAD)}
                    </td>
                </tr>
            `).join("")
            : `<tr>
                   <td colspan="2">
                       Sin productos registrados.
                   </td>
               </tr>`;

        document.getElementById(
            "modalDetalleProduccion"
        ).hidden = false;

    } catch (error) {

        await notificar(
            "error",
            "Error al consultar producción",
            error.message
        );
    }
}

function cerrarModalDetalle() {

    document.getElementById(
        "modalDetalleProduccion"
    ).hidden = true;
}

// FINALIZAR

async function finalizarProduccion(id) {

    const confirmacion = await Swal.fire({
        icon: "warning",
        title: "¿Finalizar producción #" + id + "?",
        text: "Se descontarán los ingredientes y se aumentará el inventario de productos terminados. Esta operación no se puede revertir desde este módulo.",
        showCancelButton: true,
        confirmButtonText: "Sí, finalizar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#16a34a"
    });

    if (!confirmacion.isConfirmed) return;

    try {

        const resultado = await obtenerJSON(
            API_PRODUCCION + "/FinalizarProduccion",
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    id_produccion: id
                })
            }
        );

        if (!resultado.EXITO) {
            throw new Error(resultado.MENSAJE);
        }

        await notificar(
            "success",
            "Producción finalizada",
            resultado.MENSAJE
        );

        await Promise.all([
            cargarProducciones(),
            cargarProductos()
        ]);

    } catch (error) {

        await notificar(
            "error",
            "No se pudo finalizar",
            error.message
        );
    }
}



function mostrarRecomendacionesProduccion() {

    const contenedor = document.getElementById(
        "listaRecomendaciones"
    );

    if (!contenedor) return;

    const bajos = productos.filter(p => {

        const existencia = Number(p.EXISTENCIA);
        const minimo = Number(p.EXISTENCIA_MINIMA);

        return String(p.TIPO_PRODUCTO).toUpperCase() === "P"
            && String(p.ESTADO) === "1"
            && Number.isFinite(existencia)
            && Number.isFinite(minimo)
            && minimo > 0
            && existencia < minimo;
    });

const totalProductosBajos = bajos.length

document.getElementById(
    "contadorRecomendaciones"
).textContent =
    totalProductosBajos === 1
        ? "1 producto con existencia baja"
        : totalProductosBajos +
          " productos con existencia baja";


const totalUnidadesRecomendadas = bajos.reduce(
    (acumulado, producto) => {

        const existencia = Number(producto.EXISTENCIA);
        const minimo = Number(producto.EXISTENCIA_MINIMA);

        const faltante = Math.ceil(
            Math.max(0, minimo - existencia)
        );

        return acumulado + faltante;

    }, 0
);


const indicadorProductosBajos =
    document.getElementById("totalProductosBajos");

const indicadorUnidades =
    document.getElementById("totalUnidadesRecomendadas");

if (indicadorProductosBajos) {
    indicadorProductosBajos.textContent = totalProductosBajos;
}

if (indicadorUnidades) {
    indicadorUnidades.textContent = totalUnidadesRecomendadas;
}


    bajos.sort((a, b) => {
        const faltanteA =
            Number(a.EXISTENCIA_MINIMA) - Number(a.EXISTENCIA);
        const faltanteB =
            Number(b.EXISTENCIA_MINIMA) - Number(b.EXISTENCIA);

        return faltanteB - faltanteA;
    });

    if (bajos.length === 0) {

        contenedor.innerHTML = `
            <p class="mensaje-vacio">
                No hay productos terminados por debajo
                de su existencia mínima.
            </p>
        `;

        return;
    }

    
contenedor.innerHTML = bajos.map(p => {

    const existencia = Number(p.EXISTENCIA);
    const minimo = Number(p.EXISTENCIA_MINIMA);

    const recomendado = Math.ceil(
        Math.max(0, minimo - existencia)
    );

    const idProducto = Number(p.ID_PRODUCTO);

    return `
        <div class="item-recomendacion">

            <div>
                <strong>${escaparHTML(p.NOMBRE)}</strong>

                <small>
                    Existencia: ${existencia}
                    | Mínimo: ${minimo}
                </small>
            </div>

            <div class="recomendacion-derecha">

                <div class="cantidad-recomendada">
                    +${recomendado}
                    <br>
                    <small>unidades</small>
                </div>

                <button
                    type="button"
                    class="btn-producir-recomendado"
                    data-recomendado-id="${idProducto}"
                    data-recomendado-cantidad="${recomendado}">
                    + Producir recomendado
                </button>

            </div>

        </div>
    `;

}).join("");

}

function obtenerSiguienteProduccion() {

    const pendientes = producciones.filter(p =>
        p.ESTADO === "PENDIENTE" ||
        p.ESTADO === "PROCESANDO"
    );

    // La fecha de producción es la fecha programada.
    // Como no existe fecha de registro, usamos el ID
    // más bajo como aproximación al orden de llegada.
    pendientes.sort((a, b) =>
        Number(a.ID_PRODUCCION) -
        Number(b.ID_PRODUCCION)
    );

    return pendientes.length ? pendientes[0] : null;
}

function mostrarSiguienteProduccion() {

    const contenedor = document.getElementById(
        "siguienteProduccion"
    );

    if (!contenedor) return;

    const produccion = obtenerSiguienteProduccion();

    if (!produccion) {

        contenedor.innerHTML = `
            <p class="mensaje-vacio">
                No hay producciones pendientes ni en proceso.
            </p>
        `;

        return;
    }

    const id = Number(produccion.ID_PRODUCCION);
    const estado = String(produccion.ESTADO);
    const fecha = formatearFechaProduccion(
        produccion.FECHA_PRODUCCION
    );

    contenedor.innerHTML = `
        <div class="turno-produccion">

            <h3>Producción #${id}</h3>

            <p>
                <strong>Fecha:</strong>
                ${escaparHTML(fecha)}
            </p>

            <p>
                <strong>Estado:</strong>
                <span class="estado estado-${escaparHTML(estado)}">
                    ${escaparHTML(estado)}
                </span>
            </p>

            <div class="turno-acciones">

                <button
                    class="btn btn-ver"
                    data-turno-accion="ver"
                    data-id="${id}">
                    Ver detalles
                </button>

                ${estado === "PENDIENTE" ? `
                    <button
                        class="btn btn-principal"
                        data-turno-accion="iniciar"
                        data-id="${id}">
                        Iniciar producción
                    </button>
                ` : ""}

                ${estado === "PROCESANDO" ? `
                    <button
                        class="btn btn-finalizar"
                        data-turno-accion="finalizar"
                        data-id="${id}">
                        Finalizar producción
                    </button>
                ` : ""}

            </div>

        </div>
    `;
}



let cambiandoEstadoProduccion = false;

async function iniciarProduccion(id) {

    if (cambiandoEstadoProduccion) return;

    const confirmacion = await Swal.fire({
        icon: "question",
        title: "¿Iniciar producción #" + id + "?",
        text: "La producción pasará al estado PROCESANDO.",
        showCancelButton: true,
        confirmButtonText: "Sí, iniciar",
        cancelButtonText: "Cancelar"
    });

    if (!confirmacion.isConfirmed) return;

    cambiandoEstadoProduccion = true;

    try {

        const resultado = await obtenerJSON(
            API_PRODUCCION + "/IniciarProduccion",
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    id_produccion: id
                })
            }
        );

        if (!resultado.EXITO) {
            throw new Error(resultado.MENSAJE);
        }

        await cargarProducciones();

        await notificar(
            "success",
            "Producción iniciada",
            "La producción #" + id +
            " ahora está PROCESANDO."
        );

    } catch (error) {

        await notificar(
            "error",
            "No se pudo iniciar",
            error.message
        );

    } finally {
        cambiandoEstadoProduccion = false;
    }
}

// Acciones del cuadro de siguiente producción
document.addEventListener("click", async evento => {

    const boton = evento.target.closest(
        "button[data-turno-accion]"
    );

    if (!boton) return;

    const id = Number(boton.dataset.id);

    if (!Number.isInteger(id) || id <= 0) return;

    const accion = boton.dataset.turnoAccion;

    if (accion === "ver") {
        await verDetalleProduccion(id);
    }

    if (accion === "iniciar") {
        await iniciarProduccion(id);
    }

    if (accion === "finalizar") {
        await finalizarProduccion(id);
    }
});



document.addEventListener("click", async evento => {

    const boton = evento.target.closest(
        "button[data-recomendado-id]"
    );

    if (!boton) return;

    const idProducto = Number(
        boton.dataset.recomendadoId
    );

    const cantidad = Number(
        boton.dataset.recomendadoCantidad
    );

    if (!Number.isInteger(idProducto) ||
        idProducto <= 0 ||
        !Number.isSafeInteger(cantidad) ||
        cantidad <= 0) {
        return;
    }

    // Abrir el formulario existente de nueva producción
    await abrirModalProduccion();

    const modal = document.getElementById(
        "modalProduccion"
    );

    if (modal.hidden) return;

    // Obtener la fila que ya creó abrirModalProduccion()
    const fila = document.querySelector(
        "#listaProductosProduccion .fila-producto"
    );

    if (!fila) return;

    // Seleccionar el producto recomendado
    const selector = fila.querySelector(
        ".producto-seleccionado"
    );

    // Colocar la cantidad recomendada
    const inputCantidad = fila.querySelector(
        ".cantidad-producto"
    );

    selector.value = String(idProducto);
    inputCantidad.value = String(cantidad);

    // Permitir que el usuario revise o modifique los datos
    selector.focus();
});



/* ============================================
   DEMANDA CONSOLIDADA DE SUCURSALES
============================================ */

async function cargarDemandaProduccion() {

    const tabla = document.getElementById(
        "tablaDemandaProduccion"
    );

    if (!tabla) return;

    tabla.innerHTML = `
        <tr>
            <td colspan="6">
                Cargando demanda de sucursales...
            </td>
        </tr>
    `;

    try {

        const datos = await obtenerJSON(
            API_PRODUCCION + "/CargarDemandaProduccion"
        );

        demandaProduccion = Array.isArray(datos)
            ? datos
            : [];

        mostrarDemandaProduccion();

    } catch (error) {

        demandaProduccion = [];

        tabla.innerHTML = `
            <tr>
                <td colspan="6">
                    No fue posible consultar la demanda.
                </td>
            </tr>
        `;

        await notificar(
            "error",
            "Error al cargar demanda",
            error.message
        );
    }
}


function mostrarDemandaProduccion() {

    const tabla = document.getElementById(
        "tablaDemandaProduccion"
    );

    if (!tabla) return;

    const fechaSeleccionada = document.getElementById(
        "filtroFechaDemanda"
    ).value;

    const turnoSeleccionado = document.getElementById(
        "filtroTurnoDemanda"
    ).value;

    const filtradas = demandaProduccion.filter(d => {

        const fechaRegistro = String(
            d.FECHA || ""
        ).substring(0, 10);

        const turnoRegistro = String(
            d.TURNO || ""
        ).toUpperCase();

        const coincideFecha =
            !fechaSeleccionada ||
            fechaRegistro === fechaSeleccionada;

        const coincideTurno =
            !turnoSeleccionado ||
            turnoRegistro === turnoSeleccionado;

        return coincideFecha && coincideTurno;
    });

    if (filtradas.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="6">
                    No hay demanda pendiente
                    para los filtros seleccionados.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML = filtradas.map(d => {

        const fecha = formatearFechaProduccion(
            d.FECHA
        );

        const turno = String(d.TURNO || "");

        const producto = String(d.PRODUCTO || "");

        const cantidad = Number(
            d.TOTAL_SOLICITADO || 0
        );

        const pedidos = Number(
            d.PEDIDOS || 0
        );

        const sucursales = Number(
            d.SUCURSALES || 0
        );

        return `
            <tr>
                <td>
                    ${escaparHTML(fecha)}
                </td>

                <td>
                    ${escaparHTML(turno)}
                </td>

                <td>
                    ${escaparHTML(producto)}
                </td>

                <td>
                    <strong>${cantidad}</strong>
                </td>

                <td>
                    ${pedidos}
                </td>

                <td>
                    ${sucursales}
                </td>
            </tr>
        `;

    }).join("");
}


async function generarProduccionConsolidada() {

    const fecha = document.getElementById("filtroFechaDemanda").value;
    const turno = document.getElementById("filtroTurnoDemanda").value;

    if (!fecha) {
        await notificar(
            "warning",
            "Fecha requerida",
            "Selecciona la fecha de los pedidos."
        );
        return;
    }

    if (!turno) {
        await notificar(
            "warning",
            "Turno requerido",
            "Selecciona MAÑANA o TARDE."
        );
        return;
    }

    const registros = demandaProduccion.filter(d =>
        String(d.FECHA || "").substring(0, 10) === fecha &&
        String(d.TURNO || "").toUpperCase() === turno
    );

    if (registros.length === 0) {
        await notificar(
            "warning",
            "Sin demanda",
            "No hay productos pendientes para esta fecha y turno."
        );
        return;
    }

    const totalUnidades = registros.reduce(
        (total, d) => total + Number(d.TOTAL_SOLICITADO || 0),
        0
    );

    const confirmacion = await Swal.fire({
        title: "Generar producción consolidada",
        html: `
            <p><strong>Fecha:</strong> ${escaparHTML(fecha)}</p>
            <p><strong>Turno:</strong> ${escaparHTML(turno)}</p>
            <p><strong>Productos:</strong> ${registros.length}</p>
            <p><strong>Unidades:</strong> ${totalUnidades}</p>
            <p>¿Deseas generar esta orden de producción?</p>
        `,
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, generar",
        cancelButtonText: "Cancelar"
    });

    if (!confirmacion.isConfirmed) {
        return;
    }

    const boton = document.getElementById(
        "btnGenerarProduccionConsolidada"
    );

    try {

        boton.disabled = true;
        boton.textContent = "Generando producción...";

        const respuesta = await obtenerJSON(
            API_PRODUCCION + "/GenerarProduccionConsolidada",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    fecha: fecha,
                    turno: turno
                })
            }
        );

        if (!respuesta.EXITO) {
            throw new Error(
                respuesta.MENSAJE ||
                "No fue posible generar la producción."
            );
        }

        await notificar(
            "success",
            "Producción generada",
            "Se creó la producción #" +
            respuesta.ID_PRODUCCION
        );

        await Promise.all([
            cargarDemandaProduccion(),
            cargarProducciones()
        ]);

    } catch (error) {

        await notificar(
            "error",
            "Error al generar producción",
            error.message
        );

    } finally {

        boton.disabled = false;
        boton.textContent = "Generar producción consolidada";

    }
    
    
}

