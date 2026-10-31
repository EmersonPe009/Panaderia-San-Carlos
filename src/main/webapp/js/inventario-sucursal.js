
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
            celda.colSpan = 5;
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
                    
                    
const accion = fila.insertCell();

const boton = document.createElement("button");
boton.type = "button";
boton.textContent = "Actualizar RESTO";

boton.disabled = cantidad <= 0;

boton.addEventListener("click", function () {
    abrirActualizarResto(producto, cantidad);
});

accion.appendChild(boton);

        });

    } catch (error) {

        console.error("Error al cargar inventario:", error);

        tabla.innerHTML = "";

        const fila = tabla.insertRow();
        const celda = fila.insertCell();
        celda.colSpan = 5;
        celda.textContent =
            "No fue posible cargar el inventario.";
    }
    
}


function abrirActualizarResto(producto, cantidad) {

    document.getElementById("idProductoResto").value =
        producto.ID_PRODUCTO;

    document.getElementById("nombreProductoResto")
        .textContent = producto.NOMBRE;

    document.getElementById("existenciaActualResto")
        .value = cantidad;

    const nuevaExistencia =
        document.getElementById("nuevaExistenciaResto");

    nuevaExistencia.value = "";
    nuevaExistencia.max = cantidad;

    document.getElementById("panelActualizarResto")
        .hidden = false;

    nuevaExistencia.focus();
}

document.getElementById("btnCancelarActualizacionResto")
    .addEventListener("click", function () {

        document.getElementById("panelActualizarResto")
            .hidden = true;
    });

document.getElementById("formActualizarResto")
    .addEventListener("submit", async function (evento) {

        evento.preventDefault();

        const idProducto = Number(
            document.getElementById("idProductoResto").value
        );

        const campoNuevaExistencia =
    document.getElementById("nuevaExistenciaResto");

if (campoNuevaExistencia.value.trim() === "") {
    alert("Debes ingresar la cantidad de productos restantes.");
    campoNuevaExistencia.focus();
    return;
}

const nuevaExistencia = Number(campoNuevaExistencia.value);
        const existenciaActual = Number(
            document.getElementById("existenciaActualResto").value
        );

        if (!Number.isFinite(nuevaExistencia) ||
                nuevaExistencia < 0 ||
                nuevaExistencia > existenciaActual) {

            alert("El RESTO debe estar entre 0 y la existencia actual.");
            return;
        }
        if (nuevaExistencia === existenciaActual) {
    alert("La nueva existencia es igual a la actual. No hay cambios que guardar.");
    return;
}

        if (!confirm(
            "¿Confirmar la nueva existencia de este producto?"
        )) {
            return;
        }

        const boton = document.getElementById("btnGuardarResto");
        boton.disabled = true;

        try {

            const respuesta = await fetch(
                window.APP_CONTEXT +
                "/webapi/InventarioSucursal/ActualizarResto",
                {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                    body: JSON.stringify({
                        id_producto: idProducto,
                        nueva_existencia: nuevaExistencia
                    })
                }
            );

            const resultado = await respuesta.json();

            if (!respuesta.ok || !resultado.EXITO) {
                throw new Error(
                    resultado.MENSAJE ||
                    "No se pudo actualizar el inventario."
                );
            }

            alert(resultado.MENSAJE);

            document.getElementById("panelActualizarResto")
                .hidden = true;

            await cargarInventario();

        } catch (error) {
            alert(error.message);
        } finally {
            boton.disabled = false;
        }
    });

