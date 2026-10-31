
document.addEventListener("DOMContentLoaded", cargarInventario);

async function cargarInventario() {

    const tabla = document.getElementById("tablaInventario");

    try {
        const respuesta = await fetch(
            window.APP_CONTEXT +
            "/webapi/InventarioSucursal/MiInventario",
            {
                method: "GET",
                credentials: "same-origin",
                headers: {
                    "Accept": "application/json"
                }
            }
        );

        if (respuesta.status === 401) {
            window.location.href =
                window.APP_CONTEXT + "/login.jsp";
            return;
        }

        if (!respuesta.ok) {
            throw new Error(
                "Error HTTP " + respuesta.status
            );
        }

        const datos = await respuesta.json();

        tabla.innerHTML = "";

        if (!Array.isArray(datos.PRODUCTOS) ||
            datos.PRODUCTOS.length === 0) {

            const fila = tabla.insertRow();
            const celda = fila.insertCell();
            celda.colSpan = 4;
            celda.textContent = "No hay productos registrados.";
            return;
        }

        datos.PRODUCTOS.forEach(producto => {

            const fila = tabla.insertRow();

            const nombre = fila.insertCell();
            nombre.textContent = producto.NOMBRE || "";

            const codigo = fila.insertCell();
            codigo.textContent = producto.CODIGO || "";

            const unidad = fila.insertCell();
            unidad.textContent = producto.UNIDAD_MEDIDA || "";

            const existencia = fila.insertCell();
            const cantidad = Number(producto.EXISTENCIA || 0);

            existencia.textContent = cantidad.toLocaleString("es-GT", {
                maximumFractionDigits: 3
            });

            existencia.className =
                cantidad > 0
                    ? "existencia con-stock"
                    : "existencia sin-stock";
        });

    } catch (error) {

        console.error("Error al cargar inventario:", error);

        tabla.innerHTML = "";

        const fila = tabla.insertRow();
        const celda = fila.insertCell();
        celda.colSpan = 4;
        celda.textContent =
            "No fue posible cargar el inventario.";
    }
}
