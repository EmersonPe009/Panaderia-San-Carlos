
const API_PEDIDOS = "../webapi/Pedidos";
const API_PRODUCTOS = "../webapi/Productos/CargarProductos";

let pedidosSucursal = [];
let productosDisponibles = [];
let productosSeleccionados = [];
let guardandoPedido = false;

document.addEventListener("DOMContentLoaded", async () => {

    document.getElementById("fechaPedido").value =
        new Date().toLocaleDateString("en-CA");

    document.getElementById("btnActualizar")
        .addEventListener("click", cargarPedidos);

    document.getElementById("btnAgregarProducto")
        .addEventListener("click", agregarProducto);

    document.getElementById("formNuevoPedido")
        .addEventListener("submit", guardarPedido);

    document.getElementById("tablaPedidos")
        .addEventListener("click", async (event) => {
            const boton = event.target.closest("[data-detalle]");

            if (boton) {
                await verDetallePedido(
                    Number(boton.dataset.detalle)
                );
            }
        });

    document.getElementById("tablaProductosPedido")
        .addEventListener("click", (event) => {
            const boton = event.target.closest("[data-eliminar]");

            if (boton) {
                eliminarProducto(
                    Number(boton.dataset.eliminar)
                );
            }
        });

    await Promise.all([
        cargarPedidos(),
        cargarProductos()
    ]);
});

function escaparHTML(valor) {
    return String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

async function solicitarJSON(url, opciones = {}) {

    const respuesta = await fetch(url, {
        credentials: "same-origin",
        ...opciones
    });

    const texto = await respuesta.text();
    let datos;

    try {
        datos = texto ? JSON.parse(texto) : null;
    } catch (e) {
        throw new Error("El servidor no devolvió JSON válido.");
    }

    if (!respuesta.ok) {
        throw new Error(
            datos?.MENSAJE ||
            "Error HTTP " + respuesta.status
        );
    }

    return datos;
}

async function cargarPedidos() {

    try {

        pedidosSucursal = await solicitarJSON(
            API_PEDIDOS + "/CargarPedidos"
        );

        mostrarPedidos();

    } catch (error) {

        Swal.fire("Error", error.message, "error");
    }
}

function mostrarPedidos() {

    const tabla = document.getElementById("tablaPedidos");

    document.getElementById("totalPedidos").textContent =
        pedidosSucursal.length;

    document.getElementById("pedidosPendientes").textContent =
        pedidosSucursal.filter(p =>
            p.ESTADO === "PENDIENTE"
        ).length;

    document.getElementById("pedidosDespachados").textContent =
        pedidosSucursal.filter(p =>
            p.ESTADO === "DESPACHADO"
        ).length;

    if (pedidosSucursal.length === 0) {
        tabla.innerHTML =
            '<tr><td colspan="5">No hay pedidos registrados.</td></tr>';
        return;
    }

    tabla.innerHTML = [...pedidosSucursal]
        .reverse()
        .map(p => `
            <tr>
                <td>#${escaparHTML(p.ID_PEDIDO)}</td>
                <td>${escaparHTML(p.FECHA_PEDIDO)}</td>
                <td>${escaparHTML(p.TURNO)}</td>
                <td>${escaparHTML(p.ESTADO)}</td>
                <td>
                    <button type="button"
                            class="btn btn-secundario"
                            data-detalle="${escaparHTML(p.ID_PEDIDO)}">
                        Ver detalle
                    </button>
                </td>
            </tr>
        `).join("");
}

async function cargarProductos() {

    try {

        const datos = await solicitarJSON(API_PRODUCTOS);

        productosDisponibles = datos.filter(p =>
            String(p.TIPO_PRODUCTO).toUpperCase() === "P" &&
            String(p.ESTADO) === "1"
        );

        const select = document.getElementById("productoPedido");

        select.innerHTML =
            '<option value="">Seleccionar producto</option>' +
            productosDisponibles.map(p => `
                <option value="${escaparHTML(p.ID_PRODUCTO)}">
                    ${escaparHTML(p.NOMBRE)}
                </option>
            `).join("");

    } catch (error) {

        Swal.fire("Error", error.message, "error");
    }
}

function agregarProducto() {

    const id = Number(
        document.getElementById("productoPedido").value
    );

    const cantidad = Number(
        document.getElementById("cantidadProducto").value
    );

    if (!id || !Number.isSafeInteger(cantidad) || cantidad <= 0) {
        Swal.fire(
            "Datos inválidos",
            "Selecciona un producto y una cantidad entera positiva.",
            "warning"
        );
        return;
    }

    const producto = productosDisponibles.find(p =>
        Number(p.ID_PRODUCTO) === id
    );

    if (!producto) {
        return;
    }

    const existente = productosSeleccionados.find(p =>
        p.id_producto === id
    );

    if (existente) {
        existente.cantidad += cantidad;
    } else {
        productosSeleccionados.push({
            id_producto: id,
            nombre: producto.NOMBRE,
            cantidad: cantidad
        });
    }

    mostrarProductosSeleccionados();
}

function mostrarProductosSeleccionados() {

    const tabla = document.getElementById("tablaProductosPedido");

    if (productosSeleccionados.length === 0) {
        tabla.innerHTML =
            '<tr><td colspan="3">Sin productos agregados</td></tr>';
        return;
    }

    tabla.innerHTML = productosSeleccionados.map(p => `
        <tr>
            <td>${escaparHTML(p.nombre)}</td>
            <td>${p.cantidad}</td>
            <td>
                <button type="button"
                        class="btn-eliminar"
                        data-eliminar="${p.id_producto}">
                    Eliminar
                </button>
            </td>
        </tr>
    `).join("");
}

function eliminarProducto(id) {

    productosSeleccionados =
        productosSeleccionados.filter(p =>
            p.id_producto !== id
        );

    mostrarProductosSeleccionados();
}

async function guardarPedido(event) {

    event.preventDefault();

    if (guardandoPedido) {
        return;
    }

    const fecha =
        document.getElementById("fechaPedido").value;

    const turno =
        document.getElementById("turnoPedido").value;

    if (!fecha || !turno || productosSeleccionados.length === 0) {
        Swal.fire(
            "Datos incompletos",
            "Selecciona fecha, turno y al menos un producto.",
            "warning"
        );
        return;
    }

    const confirmacion = await Swal.fire({
        title: "Registrar pedido",
        text: "¿Deseas guardar este pedido?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Guardar",
        cancelButtonText: "Cancelar"
    });

    if (!confirmacion.isConfirmed) {
        return;
    }

    const boton = document.getElementById("btnGuardarPedido");

    try {

        guardandoPedido = true;
        boton.disabled = true;

        const respuesta = await solicitarJSON(
            API_PEDIDOS + "/InsPedidoCompleto",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    fecha_pedido: fecha,
                    turno: turno,
                    productos: productosSeleccionados.map(p => ({
                        id_producto: p.id_producto,
                        cantidad: p.cantidad
                    }))
                })
            }
        );

        if (!respuesta.EXITO) {
            throw new Error(respuesta.MENSAJE);
        }

        await Swal.fire(
            "Pedido registrado",
            "Se creó el pedido #" + respuesta.ID_PEDIDO,
            "success"
        );

        productosSeleccionados = [];
        mostrarProductosSeleccionados();

        await cargarPedidos();

    } catch (error) {

        Swal.fire("Error", error.message, "error");

    } finally {

        guardandoPedido = false;
        boton.disabled = false;
    }
}

async function verDetallePedido(id) {

    try {

        const detalles = await solicitarJSON(
            API_PEDIDOS +
            "/CargarDetallesPedido?id_pedido=" +
            encodeURIComponent(id)
        );

        const filas = detalles.map(d => {

            const producto = productosDisponibles.find(p =>
                Number(p.ID_PRODUCTO) === Number(d.ID_PRODUCTO)
            );

            return `
                <tr>
                    <td>${escaparHTML(
                        producto?.NOMBRE || d.ID_PRODUCTO
                    )}</td>
                    <td>${escaparHTML(d.CANTIDAD)}</td>
                    <td>${escaparHTML(
                        d.CANTIDAD_RESTO ?? "-"
                    )}</td>
                </tr>
            `;
        }).join("");

        await Swal.fire({
            title: "Detalle del pedido #" + id,
            width: 650,
            html: `
                <div style="overflow-x:auto">
                    <table>
                        <thead>
                            <tr>
                                <th>Producto</th>
                                <th>Cantidad</th>
                                <th>Resto</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${filas || '<tr><td colspan="3">Sin productos</td></tr>'}
                        </tbody>
                    </table>
                </div>
            `
        });

    } catch (error) {

        Swal.fire("Error", error.message, "error");
    }
}
