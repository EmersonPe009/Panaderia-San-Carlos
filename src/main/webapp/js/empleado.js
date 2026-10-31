
document.addEventListener("DOMContentLoaded", function () {
    cargarPedidosSucursal();
});

function abrirPedidos(vista = "pedidos") {
    window.location.href =
        window.APP_CONTEXT +
        "/pages/pedidos.jsp?vista=" +
        encodeURIComponent(vista);
}

async function cargarPedidosSucursal() {

    const contenedor = document.getElementById("listaPedidos");

    try {
        const respuesta = await fetch(
            window.APP_CONTEXT + "/webapi/Pedidos/CargarPedidos",
            { credentials: "same-origin" }
        );

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar los pedidos.");
        }

        const datos = await respuesta.json();

        // Esta respuesta debe venir filtrada por sucursal
        // desde el backend.
        const pedidos = Array.isArray(datos) ? datos : [];

        const pendientes = pedidos.filter(
            p => p.ESTADO === "PENDIENTE"
        );

        const despachados = pedidos.filter(
            p => p.ESTADO === "DESPACHADO"
        );

        document.getElementById("totalPedidos")
            .textContent = pedidos.length;

        document.getElementById("pedidosPendientes")
            .textContent = pendientes.length;

        document.getElementById("pedidosDespachados")
            .textContent = despachados.length;

        if (pedidos.length === 0) {
            contenedor.textContent =
                "No hay pedidos registrados para esta sucursal.";
            return;
        }

        contenedor.innerHTML = "";

        [...pedidos]
    .sort((a, b) => Number(b.ID_PEDIDO) - Number(a.ID_PEDIDO))
    .slice(0, 6)
    .forEach(pedido => {

            const fila = document.createElement("div");
            fila.className = "pedido";

            const informacion = document.createElement("div");

            const titulo = document.createElement("strong");
            titulo.textContent = "Pedido #" + pedido.ID_PEDIDO;

            const fecha = document.createElement("small");
            fecha.textContent = pedido.FECHA_PEDIDO || "";

            const estado = document.createElement("span");
            estado.className = "estado";
            estado.textContent = pedido.ESTADO || "";

            informacion.appendChild(titulo);
            informacion.appendChild(fecha);

            fila.appendChild(informacion);
            fila.appendChild(estado);

            contenedor.appendChild(fila);
        });

    } catch (error) {
        console.error(error);
        contenedor.textContent =
            "No se pudieron cargar los pedidos.";
    }
}
