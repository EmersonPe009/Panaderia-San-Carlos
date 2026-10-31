

const API_ESTADISTICAS =
    window.CONTEXTO_APP + "/webapi/Estadisticas";


document.addEventListener("DOMContentLoaded", () => {
    cargarDashboard();
});

async function consultarAPI(ruta) {
    const respuesta = await fetch(
        API_ESTADISTICAS + "/" + ruta,
        { credentials: "same-origin" }
    );

    if (!respuesta.ok) {
        throw new Error(
            "Error al consultar " + ruta +
            ": HTTP " + respuesta.status
        );
    }

    return await respuesta.json();
}

async function cargarDashboard() {

    const resultados = await Promise.allSettled([
        cargarResumenGeneral(),
        cargarRankingSucursales(),
        cargarInventarioBajo(),
        cargarResumenVentas()
    ]);

    resultados.forEach((resultado, indice) => {
        if (resultado.status === "rejected") {
            console.error(
                "Error en sección " + indice,
                resultado.reason
            );
        }
    });
}
// =====================================
// RESUMEN GENERAL
// =====================================
async function cargarResumenGeneral() {

    const datos = await consultarAPI("ResumenGeneral");

    if (!Array.isArray(datos) || datos.length === 0) {
        throw new Error("Resumen general vacío");
    }

    const resumen = datos[0];

    document.getElementById("estadisticaPedidos")
        .textContent = resumen.TOTAL_PEDIDOS ?? 0;

    document.getElementById("estadisticaPendientes")
        .textContent = resumen.PEDIDOS_PENDIENTES ?? 0;

    document.getElementById("estadisticaDespachados")
        .textContent = resumen.PEDIDOS_DESPACHADOS ?? 0;

    document.getElementById("estadisticaProduccion")
        .textContent = resumen.PRODUCCIONES_FINALIZADAS ?? 0;
}

// =====================================
// RANKING DE SUCURSALES
// =====================================

async function cargarRankingSucursales() {

    const datos = await consultarAPI("RankingSucursales");

    const tabla = document.getElementById(
        "tablaRankingSucursales"
    );

    // Sucursales oficiales
    const sucursalesOficiales = [1, 2, 3, 4, 5];

    // Ordenar por unidades realmente vendidas
    const ranking = datos
        .filter(s =>
            sucursalesOficiales.includes(
                Number(s.ID_SUCURSAL)
            )
        )
        .sort((a, b) =>
            Number(b.UNIDADES_VENDIDAS || 0) -
            Number(a.UNIDADES_VENDIDAS || 0)
        );

    if (ranking.length === 0) {
        tabla.innerHTML =
            '<tr><td colspan="4">Sin información.</td></tr>';
        return;
    }

    tabla.innerHTML = "";

    ranking.forEach((sucursal, indice) => {

        const fila = document.createElement("tr");

        const valores = [
            indice + 1,
            sucursal.SUCURSAL,
            sucursal.PEDIDOS_CON_REPORTE ?? 0,
            sucursal.UNIDADES_VENDIDAS ?? 0
        ];

        valores.forEach(valor => {

            const celda = document.createElement("td");

            celda.textContent = valor ?? "";

            fila.appendChild(celda);
        });

        tabla.appendChild(fila);
    });
}

// =====================================
// INVENTARIO BAJO
// =====================================
async function cargarInventarioBajo() {

    const datos = await consultarAPI("InventarioBajo");

    const tabla = document.getElementById(
        "tablaInventarioBajo"
    );

    if (!Array.isArray(datos) || datos.length === 0) {
        tabla.innerHTML =
            '<tr><td colspan="3">Sin alertas de inventario.</td></tr>';
        return;
    }

    tabla.innerHTML = "";

    datos.forEach(producto => {

        const fila = document.createElement("tr");

        const valores = [
            producto.NOMBRE,
            producto.EXISTENCIA,
            producto.EXISTENCIA_MINIMA
        ];

        valores.forEach(valor => {
            const celda = document.createElement("td");
            celda.textContent = valor ?? "";
            fila.appendChild(celda);
        });

        tabla.appendChild(fila);
    });
    
    
    // =====================================
// RESUMEN DE VENTAS REALES
// =====================================
// =====================================
// RESUMEN DE VENTAS REALES
// =====================================


// =====================================
// RESUMEN DE VENTAS REALES
// =====================================

}

async function cargarResumenVentas() {

    console.log("Consultando resumen de ventas...");

    const datos = await consultarAPI("ResumenVentas");

    console.log("Datos de ventas recibidos:", datos);

    if (!Array.isArray(datos) || datos.length === 0) {
        throw new Error("Resumen de ventas vacío");
    }

    const ventas = datos[0];

    document.getElementById("estadisticaVendidas")
        .textContent = ventas.UNIDADES_VENDIDAS ?? 0;

    document.getElementById("estadisticaSobrantes")
        .textContent = ventas.UNIDADES_SOBRANTES ?? 0;

    document.getElementById("estadisticaPendientesResto")
        .textContent = ventas.PEDIDOS_PENDIENTES_RESTO ?? 0;

    document.getElementById("estadisticaProductoMasVendido")
        .textContent = ventas.PRODUCTO_MAS_VENDIDO || "Sin ventas";
}