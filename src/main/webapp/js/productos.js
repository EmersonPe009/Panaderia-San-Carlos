

let productoEnEdicion = null;


document.addEventListener("DOMContentLoaded", function () {

    cargarProductos();

    const modal = document.getElementById("modalProducto");
    const btnNuevo = document.getElementById("btnNuevoProducto");
    const btnCerrar = document.getElementById("btnCerrarProducto");
    const btnCancelar = document.getElementById("btnCancelarProducto");
    const formulario = document.getElementById("formProducto");

    function cerrarModal() {
        modal.hidden = true;
    }

    
const tipoProducto = document.getElementById("tipoProducto");
const codigoProducto = document.getElementById("codigoProducto");

async function sugerirCodigo() {

    const tipo = tipoProducto.value;
    const prefijo = tipo === "I" ? "INS-" : "PRO-";

    codigoProducto.value = "Consultando...";
    codigoProducto.readOnly = true;

    try {

        const respuesta = await fetch(
            "../webapi/Productos/CargarProductos"
        );

        if (!respuesta.ok) {
            throw new Error("HTTP " + respuesta.status);
        }

        const productos = await respuesta.json();

        if (!Array.isArray(productos)) {
            throw new Error("Respuesta inválida");
        }

        let numeroMayor = 0;

        productos.forEach(function (producto) {

            const codigo = String(producto.CODIGO || "");

            if (codigo.startsWith(prefijo)) {

                const numero = Number(codigo.substring(prefijo.length));

                if (Number.isInteger(numero) && numero > numeroMayor) {
                    numeroMayor = numero;
                }
            }
        });

        const siguiente = numeroMayor + 1;

        codigoProducto.value =
            prefijo + String(siguiente).padStart(3, "0");

    } catch (error) {

        console.error("Error al sugerir código:", error);

        codigoProducto.value = "";
        alert("No se pudo consultar el siguiente código.");

    } finally {
        codigoProducto.readOnly = false;
    }
}


btnNuevo.addEventListener("click", function () {

    productoEnEdicion = null;

    formulario.reset();

    document.querySelector(".modal-encabezado h2").textContent =
        "Registrar producto";

    formulario.querySelector('button[type="submit"]').textContent =
        "Guardar producto";

    modal.hidden = false;

    sugerirCodigo();
});


tipoProducto.addEventListener("change", function () {

    if (productoEnEdicion === null) {
        sugerirCodigo();
    }

});



    btnCerrar.addEventListener("click", cerrarModal);
    btnCancelar.addEventListener("click", cerrarModal);
    
    
window.abrirEditarProducto = function (producto) {

    productoEnEdicion = producto;

    document.getElementById("nombreProducto").value =
        producto.NOMBRE ?? "";

    document.getElementById("codigoProducto").value =
        producto.CODIGO ?? "";

    document.getElementById("tipoProducto").value =
        producto.TIPO_PRODUCTO ?? "I";

    document.getElementById("unidadProducto").value =
        producto.UNIDAD_MEDIDA ?? "UNIDAD";

    document.getElementById("precioProducto").value =
        producto.PRECIO ?? 0;

    document.getElementById("minimoProducto").value =
        producto.EXISTENCIA_MINIMA ?? 0;

    document.querySelector(".modal-encabezado h2").textContent =
        "Editar producto";

    formulario.querySelector('button[type="submit"]').textContent =
        "Actualizar producto";

    modal.hidden = false;
};


    

formulario.addEventListener("submit", async function (evento) {

    evento.preventDefault();

    const botonGuardar = formulario.querySelector(
        'button[type="submit"]'
    );

    const editando = productoEnEdicion !== null;

    const producto = {
        nombre: document.getElementById("nombreProducto").value.trim(),
        codigo: document.getElementById("codigoProducto").value.trim().toUpperCase(),
        tipo_producto: document.getElementById("tipoProducto").value,
        unidad_medida: document.getElementById("unidadProducto").value,
        precio: document.getElementById("precioProducto").value,
        existencia: editando
            ? String(productoEnEdicion.EXISTENCIA)
            : "0",
        existencia_minima: document.getElementById("minimoProducto").value
    };

    if (editando) {
        producto.id_producto = String(productoEnEdicion.ID_PRODUCTO);
        producto.estado = String(productoEnEdicion.ESTADO);
    }

    if (!producto.nombre || !producto.codigo) {
        alert("Completa el nombre y el código.");
        return;
    }

    const url = editando
        ? "../webapi/Productos/UpdProducto"
        : "../webapi/Productos/InsProducto";

    const metodo = editando ? "PUT" : "POST";

    botonGuardar.disabled = true;
    botonGuardar.textContent = editando
        ? "Actualizando..."
        : "Guardando...";

    try {

        const respuesta = await fetch(url, {
            method: metodo,
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(producto)
        });

        
const texto = await respuesta.text();

console.log("Respuesta del servidor:", texto);

if (!respuesta.ok) {
    throw new Error(
        "HTTP " + respuesta.status + ": " + texto
    );
}

let mensaje;
let idProductoCreado = null;
let exito = false;

if (editando) {

    // Mantener el funcionamiento actual de UpdProducto
    mensaje = texto.trim().replace(/^"|"$/g, "");

    exito =
        /actualizad[oa]|modificad[oa]\s+correctamente/i
        .test(mensaje);

} else {

    // InsProducto ahora devuelve un objeto JSON
    const datos = JSON.parse(texto);

    mensaje = datos.MENSAJE;
    idProductoCreado = datos.ID_PRODUCTO;
    exito = datos.EXITO === true;

}

if (!exito) {
    throw new Error(
        "El servidor respondió: " + mensaje
    );
}


await mostrarNotificacion(
    "success",
    editando
        ? "¡Producto actualizado!"
        : "¡Producto registrado!",
    mensaje
);



// Actualizar la tabla después de guardar
await cargarProductos();

// Cerrar el formulario de productos
cerrarModal();

// Limpiar el producto en edición
productoEnEdicion = null;

// Si registramos un producto terminado,
// abrir automáticamente su receta
if (!editando && producto.tipo_producto === "P") {

    if (!idProductoCreado) {
        throw new Error(
            "El producto fue creado, pero no se recibió su ID."
        );
    }

    abrirModalReceta(
        idProductoCreado,
        producto.nombre
    );
}


    } catch (error) {

        console.error("Error al guardar producto:", error);

        alert("No se pudo completar la operación. Revisa la consola.");

    } finally {

        botonGuardar.disabled = false;
        botonGuardar.textContent = editando
            ? "Actualizar producto"
            : "Guardar producto";
    }

});



});


/* =========================================
   CONSULTAR PRODUCTOS CON RECETA
========================================= */

async function obtenerProductosConReceta() {

    const respuesta = await fetch(
        "../webapi/Recetas/CargarRecetas"
    );

    if (!respuesta.ok) {
        throw new Error(
            "No se pudieron consultar las recetas. HTTP " +
            respuesta.status
        );
    }

    const recetas = await respuesta.json();

    if (!Array.isArray(recetas)) {
        throw new Error(
            "El servicio de recetas no devolvió una lista válida."
        );
    }

    console.log("Recetas registradas:", recetas);

    return recetas;
}


async function cargarProductos() {

    const cuerpo = document.getElementById("cuerpoProductos");

    cuerpo.innerHTML =
        '<tr><td colspan="9">Cargando productos...</td></tr>';

    try {

        const respuesta = await fetch(
            "../webapi/Productos/CargarProductos"
        );

        if (!respuesta.ok) {
            throw new Error("HTTP " + respuesta.status);
        }

        const productos = await respuesta.json();
        
// Consultar recetas activas
const recetas = await obtenerProductosConReceta();

// Identificar productos que ya tienen receta
const productosConReceta = new Set(
    recetas
        .filter(receta => String(receta.ESTADO) === "1")
        .map(receta => Number(receta.ID_PRODUCTO))
);
const recetasPorProducto = new Map(
    recetas
        .filter(receta => String(receta.ESTADO) === "1")
        .map(receta => [
            Number(receta.ID_PRODUCTO),
            Number(receta.ID_RECETA)
        ])
);

console.log("Productos con receta activa:", productosConReceta);

        

const buscador = document.getElementById("buscarProducto");
const botones = document.querySelectorAll(".filtro");

let tipoSeleccionado = "TODOS";

function aplicarFiltros() {

    const texto = buscador.value.toLowerCase().trim();

    const filas = cuerpo.querySelectorAll("tr");

    filas.forEach(function (fila) {

        const columnas = fila.querySelectorAll("td");

        if (columnas.length < 4) {
            return;
        }

        const nombre = columnas[1].textContent.toLowerCase();
        const codigo = columnas[2].textContent.toLowerCase();
        const tipo = columnas[3].textContent.trim();

        const coincideBusqueda =
            nombre.includes(texto) || codigo.includes(texto);

        const coincideTipo =
            tipoSeleccionado === "TODOS" ||
            (tipoSeleccionado === "I" && tipo === "Ingrediente") ||
            (tipoSeleccionado === "P" && tipo === "Producto terminado");

        fila.style.display =
            coincideBusqueda && coincideTipo ? "" : "none";
    });
}

// Buscar por nombre o código
buscador.addEventListener("input", aplicarFiltros);

// Filtrar por categoría
botones.forEach(function (boton) {

    boton.addEventListener("click", function () {

        tipoSeleccionado = boton.dataset.tipo;

        botones.forEach(function (b) {
            b.classList.remove("activo");
        });

        boton.classList.add("activo");

        aplicarFiltros();
    });
});


        if (!Array.isArray(productos)) {
            throw new Error("La respuesta no es un arreglo.");
        }

        // Limpiar la tabla
        cuerpo.innerHTML = "";

        if (productos.length === 0) {
            cuerpo.innerHTML =
                '<tr><td colspan="9">' +
                'No hay productos registrados.' +
                '</td></tr>';
            return;
        }

        // Recorrer los productos recibidos
        productos.forEach(function (producto) {

            const fila = document.createElement("tr");

            const tipo = producto.TIPO_PRODUCTO === "I"
                ? "Ingrediente"
                : producto.TIPO_PRODUCTO === "P"
                    ? "Producto terminado"
                    : "Desconocido";

            const estado = String(producto.ESTADO) === "1"
                ? "Activo"
                : "Inactivo";

            const valores = [
                producto.ID_PRODUCTO,
                producto.NOMBRE,
                producto.CODIGO,
                tipo,
                producto.UNIDAD_MEDIDA,
                "Q " + Number(producto.PRECIO).toFixed(2),
                producto.EXISTENCIA,
                estado
            ];

                
valores.forEach(function (valor) {
    const celda = document.createElement("td");
    celda.textContent = valor ?? "";
    fila.appendChild(celda);
});

// Columna de acciones
const celdaAcciones = document.createElement("td");

const btnEditar = document.createElement("button");
btnEditar.type = "button";
btnEditar.className = "btn-editar";
btnEditar.textContent = "Editar";


btnEditar.addEventListener("click", function () {

    abrirEditarProducto(producto);

});



celdaAcciones.appendChild(btnEditar);

/* =========================================
   BOTÓN VER RECETA
========================================= */

const esProductoTerminado =
    producto.TIPO_PRODUCTO === "P";

const productoActivo =
    String(producto.ESTADO) === "1";

const idReceta = recetasPorProducto.get(
    Number(producto.ID_PRODUCTO)
);


/* =========================================
   BOTÓN VER RECETA
========================================= */

if (esProductoTerminado && productoActivo && idReceta) {

    const btnVerReceta = document.createElement("button");

    btnVerReceta.type = "button";
    btnVerReceta.className = "btn-ver-receta";
    btnVerReceta.textContent = "Ver receta";

    btnVerReceta.addEventListener("click", function () {
        consultarReceta(idReceta);
    });

    celdaAcciones.appendChild(btnVerReceta);
}



// Mostrar el botón solo si es un producto terminado,
// está activo y todavía no tiene receta.

const necesitaReceta =
    producto.TIPO_PRODUCTO === "P" &&
    String(producto.ESTADO) === "1" &&
    !productosConReceta.has(Number(producto.ID_PRODUCTO));

if (necesitaReceta) {

    const btnReceta = document.createElement("button");

    btnReceta.type = "button";
    btnReceta.className = "btn-configurar-receta";
    btnReceta.textContent = "Configurar receta";

    btnReceta.addEventListener("click", function () {

        abrirModalReceta(
            producto.ID_PRODUCTO,
            producto.NOMBRE
        );

    });

    celdaAcciones.appendChild(btnReceta);
}


// Botón para activar o desactivar
const btnEstado = document.createElement("button");

const estaActivo = String(producto.ESTADO) === "1";

btnEstado.type = "button";
btnEstado.className = estaActivo
    ? "btn-desactivar"
    : "btn-activar";

btnEstado.textContent = estaActivo
    ? "Desactivar"
    : "Activar";

btnEstado.addEventListener("click", async function () {

    const nuevoEstado = estaActivo ? "0" : "1";

    const accion = estaActivo ? "desactivar" : "activar";

    const confirmar = confirm(
        "¿Deseas " + accion + " el producto " +
        producto.NOMBRE + "?"
    );

    if (!confirmar) {
        return;
    }

    const datos = {
        id_producto: String(producto.ID_PRODUCTO),
        nombre: String(producto.NOMBRE),
        codigo: String(producto.CODIGO),
        tipo_producto: String(producto.TIPO_PRODUCTO),
        unidad_medida: String(producto.UNIDAD_MEDIDA),
        precio: String(producto.PRECIO),
        existencia: String(producto.EXISTENCIA),
        existencia_minima: String(producto.EXISTENCIA_MINIMA),
        estado: nuevoEstado
    };

    btnEstado.disabled = true;
    btnEstado.textContent = "Procesando...";

    try {

        const respuesta = await fetch(
            "../webapi/Productos/UpdProducto",
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(datos)
            }
        );

        const mensaje = (await respuesta.text())
            .trim()
            .replace(/^"|"$/g, "");

        console.log("Respuesta cambio de estado:", mensaje);

        if (!respuesta.ok) {
            throw new Error("HTTP " + respuesta.status + ": " + mensaje);
        }

        if (!/actualizado correctamente/i.test(mensaje)) {
            throw new Error(mensaje);
        }

        alert(
            "Producto " +
            (nuevoEstado === "1" ? "activado" : "desactivado") +
            " correctamente."
        );

        await cargarProductos();

    } catch (error) {

        console.error("Error al cambiar estado:", error);

        alert("No se pudo cambiar el estado. Revisa la consola.");

        btnEstado.disabled = false;
        btnEstado.textContent = estaActivo
            ? "Desactivar"
            : "Activar";
    }

});

celdaAcciones.appendChild(btnEstado);

fila.appendChild(celdaAcciones);


cuerpo.appendChild(fila);

        }
                
                );

    } catch (error) {

        console.error("Error al cargar productos:", error);

        cuerpo.innerHTML =
            '<tr><td colspan="9">' +
            'Error al consultar los productos.' +
            '</td></tr>';
    }
    
    
    
    
/* =========================================
   ESTADÍSTICAS DEL INVENTARIO
========================================= */

async function cargarEstadisticasProductos() {

    const elementos = {
        total: document.getElementById("statTotalProductos"),
        existencia: document.getElementById("statConExistencia"),
        baja: document.getElementById("statExistenciaBaja"),
        valor: document.getElementById("statValorInventario")
    };

    // Verificar que las tarjetas existan
    if (Object.values(elementos).some(el => !el)) {
        return;
    }

    try {

        const respuesta = await fetch(
            "../webapi/Productos/GetEstadisticas",
            {
                method: "GET",
                credentials: "same-origin",
                headers: {
                    "Accept": "application/json"
                }
            }
        );

        if (!respuesta.ok) {
            throw new Error(
                "Error HTTP: " + respuesta.status
            );
        }

        const datos = await respuesta.json();

        elementos.total.textContent =
            Number(datos.TOTAL_PRODUCTOS).toLocaleString("es-GT");

        elementos.existencia.textContent =
            Number(datos.CON_EXISTENCIA).toLocaleString("es-GT");

        elementos.baja.textContent =
            Number(datos.EXISTENCIA_BAJA).toLocaleString("es-GT");

        elementos.valor.textContent =
            new Intl.NumberFormat("es-GT", {
                style: "currency",
                currency: "GTQ"
            }).format(Number(datos.VALOR_INVENTARIO));

    } catch (error) {

        console.error(
            "Error al cargar estadísticas:",
            error
        );

        Object.values(elementos).forEach(el => {
            el.textContent = "No disponible";
        });
    }
}

/* Cargar estadísticas al abrir Productos */

if (document.readyState === "loading") {

    document.addEventListener(
        "DOMContentLoaded",
        cargarEstadisticasProductos
    );

} else {

    cargarEstadisticasProductos();

}



/* =========================================
   GRÁFICAS ANIMADAS DE INVENTARIO
========================================= */

async function cargarGraficasInventario() {

    const canvasExistencias =
        document.getElementById("graficaExistencias");

    const canvasTipos =
        document.getElementById("graficaTipos");

    if (!canvasExistencias || !canvasTipos) {
        return;
    }

    if (typeof Chart === "undefined") {
        console.error("Chart.js no está disponible.");
        return;
    }

    try {

        // Consultar los datos reales de Oracle
        const [respuestaExistencias, respuestaTipos] =
            await Promise.all([
                fetch("../webapi/Productos/GetTopExistencias", {
                    credentials: "same-origin"
                }),
                fetch("../webapi/Productos/GetDistribucionTipos", {
                    credentials: "same-origin"
                })
            ]);

        if (!respuestaExistencias.ok || !respuestaTipos.ok) {
            throw new Error("No se pudieron obtener las estadísticas.");
        }

        const productos = await respuestaExistencias.json();
        const tipos = await respuestaTipos.json();

        // =====================================
        // GRÁFICA 1: BARRAS ANIMADAS
        // =====================================

        new Chart(canvasExistencias, {

            type: "bar",

            data: {
                labels: productos.map(p => p.NOMBRE),

                datasets: [{
                    label: "Existencia",
                    data: productos.map(p => Number(p.EXISTENCIA)),
                    backgroundColor: "#d89a55",
                    borderRadius: 7,
                    maxBarThickness: 55
                }]
            },

            options: {
                responsive: true,
                maintainAspectRatio: false,

                animation: {
                    duration: 1800,
                    easing: "easeOutQuart"
                },

                animations: {
                    y: {
                        from: 0
                    }
                },

                plugins: {
                    legend: {
                        display: false
                    }
                },

                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });

        // =====================================
        // GRÁFICA 2: CIRCULAR ANIMADA
        // =====================================

        new Chart(canvasTipos, {

            type: "doughnut",

            data: {
                labels: tipos.map(t => t.TIPO),

                datasets: [{
                    data: tipos.map(t => Number(t.CANTIDAD)),
                    backgroundColor: [
                        "#d89a55",
                        "#3f5968"
                    ],
                    borderWidth: 3,
                    borderColor: "#ffffff",
                    hoverOffset: 10
                }]
            },

            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: "65%",

                animation: {
                    duration: 2000,
                    easing: "easeOutCubic",
                    animateRotate: true,
                    animateScale: true
                },

                plugins: {
                    legend: {
                        position: "bottom",
                        labels: {
                            padding: 18,
                            usePointStyle: true
                        }
                    }
                }
            }
        });

    } catch (error) {

        console.error(
            "Error al cargar gráficas:",
            error
        );
    }
}


/* Ejecutar las gráficas al cargar la página */

if (document.readyState === "loading") {

    document.addEventListener(
        "DOMContentLoaded",
        cargarGraficasInventario
    );

} else {

    cargarGraficasInventario();

}


/* =========================================
   ALERTA DE EXISTENCIAS BAJAS
========================================= */

function irAProduccion() {
    window.location.href = "produccion.jsp?origen=existencias-bajas";
}

const tarjetaExistenciaBaja =
    document.getElementById("tarjetaExistenciaBaja");

if (tarjetaExistenciaBaja) {

    tarjetaExistenciaBaja.addEventListener(
        "click",
        irAProduccion
    );

    tarjetaExistenciaBaja.addEventListener(
        "keydown",
        function(evento) {
            if (evento.key === "Enter" ||
                evento.key === " ") {
                evento.preventDefault();
                irAProduccion();
            }
        }
    );
}




/* =========================================
   NOTIFICACIONES MODERNAS
========================================= */

function mostrarNotificacion(tipo, titulo, mensaje) {

    return Swal.fire({
        icon: tipo,
        title: titulo,
        text: mensaje,

        confirmButtonText: "Aceptar",
        confirmButtonColor: "#373B44",

        background: "#ffffff",
        color: "#373B44",

        showClass: {
            popup: "swal2-show"
        },

        hideClass: {
            popup: "swal2-hide"
        },

        customClass: {
            popup: "notificacion-panaderia"
        }
    });
}




}


/* =========================================
   NOTIFICACIONES MODERNAS
========================================= */

function mostrarNotificacion(tipo, titulo, mensaje) {

    if (typeof Swal === "undefined") {
        console.error("SweetAlert2 no está cargado.");
        alert(titulo + "\n" + mensaje);
        return Promise.resolve();
    }

    return Swal.fire({
        icon: tipo,
        title: titulo,
        text: mensaje,

        confirmButtonText: "Aceptar",
        confirmButtonColor: "#373B44",

        background: "#ffffff",
        color: "#373B44",

        customClass: {
            popup: "notificacion-panaderia"
        }
    });
}



async function editarReceta(idReceta) {

    try {

        const [respuestaReceta, respuestaDetalles] =
            await Promise.all([

                fetch(
                    "../webapi/Recetas/GetReceta?id_receta=" +
                    encodeURIComponent(idReceta)
                ),

                fetch(
                    "../webapi/Recetas/CargarDetallesReceta?id_receta=" +
                    encodeURIComponent(idReceta)
                )

            ]);

        if (!respuestaReceta.ok || !respuestaDetalles.ok) {
            throw new Error("No se pudo consultar la receta.");
        }

        const recetas = await respuestaReceta.json();
        const detalles = await respuestaDetalles.json();

        if (!Array.isArray(recetas) || recetas.length === 0) {
            throw new Error("No se encontró la receta.");
        }

        if (!Array.isArray(detalles) || detalles.length === 0) {
            throw new Error(
                "La receta no tiene ingredientes registrados."
            );
        }

        const receta = recetas[0];

        const modal = document.getElementById("modalReceta");
        const formulario = document.getElementById("formReceta");
        const contenedor = document.getElementById(
            "contenedorIngredientesReceta"
        );

        if (!modal || !formulario || !contenedor) {
            throw new Error(
                "No se encontró el formulario de recetas."
            );
        }

        // Cargar ingredientes disponibles antes de crear filas.
        await cargarIngredientesDisponiblesReceta();

        // Limpiar el formulario anterior.
        formulario.reset();
        contenedor.innerHTML = "";

        // Cargar los datos principales.
        document.getElementById("idProductoReceta").value =
            receta.ID_PRODUCTO;

        document.getElementById("nombreReceta").value =
            receta.NOMBRE;

        document.getElementById("rendimientoReceta").value =
            receta.RENDIMIENTO;

        // Cargar los ingredientes existentes.
        detalles.forEach(function (detalle) {

            agregarFilaIngredienteReceta(
                detalle.ID_PRODUCTO,
                detalle.CANTIDAD
            );

        });

        // Activar el modo edición.
        idRecetaEnEdicion = Number(receta.ID_RECETA);

        document.getElementById("btnGuardarReceta").textContent =
            "Actualizar receta";

        // Mostrar el formulario editable.
        document.getElementById("modalVerReceta").hidden = true;
        modal.hidden = false;

        document.getElementById("nombreReceta").focus();

        console.log(
            "Editando receta:",
            idRecetaEnEdicion
        );

    } catch (error) {

        console.error("Error al editar receta:", error);

        await mostrarNotificacion(
            "error",
            "No se pudo abrir la edición",
            error.message
        );
    }
}

/* =========================================
   MODAL DE RECETAS
========================================= */


async function abrirModalReceta(idProducto, nombreProducto) {

    // Indicar que estamos creando una receta nueva.
    idRecetaEnEdicion = null;

    const modal = document.getElementById("modalReceta");
    const formularioReceta = document.getElementById("formReceta");

    if (!modal || !formularioReceta) {
        console.error("No se encontró el formulario de recetas.");
        return;
    }

    formularioReceta.reset();

    document.getElementById("idProductoReceta").value =
        idProducto;

    document.getElementById("nombreReceta").value =
        "Receta de " + nombreProducto;

    document.getElementById(
        "contenedorIngredientesReceta"
    ).innerHTML = "";

    document.getElementById("btnGuardarReceta").textContent =
        "Guardar receta";

    modal.hidden = false;

    try {

        await cargarIngredientesDisponiblesReceta();

        agregarFilaIngredienteReceta();

    } catch (error) {

        console.error("Error al cargar ingredientes:", error);

        await mostrarNotificacion(
            "error",
            "No se pudieron cargar los ingredientes",
            "Revisa la conexión con el servidor."
        );
    }

    document.getElementById("nombreReceta").focus();
}



function cerrarModalReceta() {

    const modal = document.getElementById("modalReceta");

    if (modal) {
        modal.hidden = true;
    }

    // Limpiar el modo de edición.
    idRecetaEnEdicion = null;

    // Restaurar el texto del botón.
    const botonGuardar = document.getElementById("btnGuardarReceta");

    if (botonGuardar) {
        botonGuardar.textContent = "Guardar receta";
    }
}


document.getElementById("btnCerrarReceta")
    ?.addEventListener("click", cerrarModalReceta);

document.getElementById("btnCancelarReceta")
    ?.addEventListener("click", cerrarModalReceta);



let ingredientesDisponiblesReceta = [];

async function cargarIngredientesDisponiblesReceta() {

    const respuesta = await fetch(
        "../webapi/Productos/CargarProductos"
    );

    if (!respuesta.ok) {
        throw new Error("No se pudieron cargar los ingredientes.");
    }

    const productos = await respuesta.json();

    ingredientesDisponiblesReceta = productos.filter(p =>
        p.TIPO_PRODUCTO === "I" &&
        Number(p.ESTADO) === 1
    );

    return ingredientesDisponiblesReceta;
}


function agregarFilaIngredienteReceta(
    idIngrediente = "",
    cantidadIngrediente = ""
) {

    const contenedor = document.getElementById(
        "contenedorIngredientesReceta"
    );

    if (!contenedor) return;

    const fila = document.createElement("div");
    fila.className = "fila-ingrediente-receta";

    // SELECT DE INGREDIENTES
    const selector = document.createElement("select");
    selector.className = "ingrediente-receta";
    selector.required = true;

    const opcionInicial = document.createElement("option");
    opcionInicial.value = "";
    opcionInicial.textContent = "Seleccionar ingrediente";
    selector.appendChild(opcionInicial);

    ingredientesDisponiblesReceta.forEach(ingrediente => {

        const opcion = document.createElement("option");

        opcion.value = ingrediente.ID_PRODUCTO;

        opcion.textContent =
            ingrediente.NOMBRE +
            " (" + ingrediente.UNIDAD_MEDIDA + ")";

        selector.appendChild(opcion);
    });

    // Seleccionar ingrediente existente al editar.
    if (idIngrediente !== "") {

        selector.value = String(idIngrediente);

        // Evitar perder un ingrediente histórico que esté inactivo.
        if (selector.value !== String(idIngrediente)) {

            const opcionAnterior = document.createElement("option");

            opcionAnterior.value = String(idIngrediente);
            opcionAnterior.textContent =
                "Ingrediente actual #" + idIngrediente +
                " (no disponible para nuevas recetas)";

            selector.appendChild(opcionAnterior);
            selector.value = String(idIngrediente);
        }
    }

    // CAMPO DE CANTIDAD
    const cantidad = document.createElement("input");

    cantidad.type = "number";
    cantidad.className = "cantidad-ingrediente-receta";
    cantidad.min = "0.0001";
    cantidad.step = "any";
    cantidad.placeholder = "Cantidad";
    cantidad.required = true;

    // Cargar cantidad existente al editar.
    if (cantidadIngrediente !== "") {
        cantidad.value = String(cantidadIngrediente);
    }

    // BOTÓN ELIMINAR
    const eliminar = document.createElement("button");

    eliminar.type = "button";
    eliminar.className = "btn-eliminar-ingrediente";
    eliminar.textContent = "×";
    eliminar.title = "Eliminar ingrediente";

    eliminar.addEventListener("click", function () {
        fila.remove();
    });

    fila.appendChild(selector);
    fila.appendChild(cantidad);
    fila.appendChild(eliminar);

    contenedor.appendChild(fila);
}


document.getElementById("btnAgregarIngrediente")
    ?.addEventListener("click", function () {

        agregarFilaIngredienteReceta();

    });



/* =========================================
   GUARDAR RECETA COMPLETA
========================================= */


document.getElementById("formReceta")
    ?.addEventListener("submit", async function (evento) {

        evento.preventDefault();

        const formulario = this;
        const botonGuardar = document.getElementById("btnGuardarReceta");

        const esEdicion = idRecetaEnEdicion !== null;

        const idProducto = Number(
            document.getElementById("idProductoReceta").value
        );

        const nombre = document.getElementById("nombreReceta")
            .value.trim();

        const rendimiento = Number(
            document.getElementById("rendimientoReceta").value
        );

        const filas = document.querySelectorAll(
            "#contenedorIngredientesReceta .fila-ingrediente-receta"
        );

        const ingredientes = [];

        // Validar cada ingrediente.
        for (const fila of filas) {

            const idIngrediente = Number(
                fila.querySelector(".ingrediente-receta").value
            );

            const cantidad = Number(
                fila.querySelector(".cantidad-ingrediente-receta").value
            );

            if (!Number.isInteger(idIngrediente) ||
                    idIngrediente <= 0 ||
                    !Number.isFinite(cantidad) ||
                    cantidad <= 0) {

                await mostrarNotificacion(
                    "warning",
                    "Datos incompletos",
                    "Selecciona un ingrediente y una cantidad válida en cada fila."
                );

                return;
            }

            ingredientes.push({
                id_producto: idIngrediente,
                cantidad: cantidad
            });
        }

        // Validar datos principales.
        if (!Number.isInteger(idProducto) || idProducto <= 0 ||
                !nombre ||
                !Number.isFinite(rendimiento) ||
                rendimiento <= 0) {

            await mostrarNotificacion(
                "warning",
                "Datos incompletos",
                "Verifica el producto, nombre y rendimiento."
            );

            return;
        }

        if (ingredientes.length === 0) {

            await mostrarNotificacion(
                "warning",
                "Sin ingredientes",
                "Debes agregar al menos un ingrediente."
            );

            return;
        }

        // Evitar ingredientes repetidos.
        const ids = ingredientes.map(i => i.id_producto);

        if (new Set(ids).size !== ids.length) {

            await mostrarNotificacion(
                "warning",
                "Ingrediente repetido",
                "No puedes repetir ingredientes en una receta."
            );

            return;
        }

        // Preparar datos según el modo.
        const receta = {
            nombre: nombre,
            rendimiento: rendimiento,
            ingredientes: ingredientes
        };

        if (esEdicion) {
            receta.id_receta = idRecetaEnEdicion;
        } else {
            receta.id_producto = idProducto;
        }

        const url = esEdicion
            ? "../webapi/Recetas/UpdRecetaCompleta"
            : "../webapi/Recetas/InsRecetaCompleta";

        const metodo = esEdicion ? "PUT" : "POST";

        botonGuardar.disabled = true;
        botonGuardar.textContent = esEdicion
            ? "Actualizando..."
            : "Guardando...";

        try {

            const respuesta = await fetch(url, {
                method: metodo,
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(receta)
            });

            const datos = await respuesta.json();

            if (!respuesta.ok || datos.EXITO !== true) {

                throw new Error(
                    datos.MENSAJE ||
                    "No se pudo guardar la receta."
                );
            }

            console.log(
                esEdicion
                    ? "Receta actualizada:"
                    : "Receta registrada:",
                datos
            );

            cerrarModalReceta();

            await mostrarNotificacion(
                "success",
                esEdicion
                    ? "¡Receta actualizada!"
                    : "¡Receta registrada!",
                datos.MENSAJE
            );

            formulario.reset();
            idRecetaEnEdicion = null;

            // Actualizar los botones de la tabla.
            await cargarProductos();

        } catch (error) {

            console.error("Error al guardar receta:", error);

            await mostrarNotificacion(
                "error",
                esEdicion
                    ? "No se pudo actualizar la receta"
                    : "No se pudo registrar la receta",
                error.message
            );

        } finally {

            botonGuardar.disabled = false;

            botonGuardar.textContent =
                idRecetaEnEdicion !== null
                    ? "Actualizar receta"
                    : "Guardar receta";
        }
    });

    
/* =========================================
   CONSULTAR RECETA E INGREDIENTES
========================================= */


async function consultarReceta(idReceta) {

    try {

        const [respuestaReceta, respuestaDetalles] =
            await Promise.all([

                fetch(
                    "../webapi/Recetas/GetReceta?id_receta=" +
                    encodeURIComponent(idReceta)
                ),

                fetch(
                    "../webapi/Recetas/CargarDetallesReceta?id_receta=" +
                    encodeURIComponent(idReceta)
                )

            ]);

        if (!respuestaReceta.ok || !respuestaDetalles.ok) {
            throw new Error("No se pudo consultar la receta.");
        }

        const recetas = await respuestaReceta.json();
        const ingredientes = await respuestaDetalles.json();

        if (!Array.isArray(recetas) || recetas.length === 0) {
            throw new Error("No se encontró la receta solicitada.");
        }

        if (!Array.isArray(ingredientes)) {
            throw new Error("Los ingredientes recibidos no son válidos.");
        }

        const receta = recetas[0];

        // Guardar el ID de la receta visualizada.
        idRecetaVisualizada = Number(receta.ID_RECETA);

        console.log("Receta consultada:", receta);
        console.log("Ingredientes de la receta:", ingredientes);

        // Mostrar el modal con los datos de Oracle.
        await mostrarDetalleReceta(receta, ingredientes);

    } catch (error) {

        idRecetaVisualizada = null;

        console.error("Error al consultar receta:", error);

        await mostrarNotificacion(
            "error",
            "Error al consultar receta",
            error.message
        );
    }
}


/* =========================================
   MOSTRAR DETALLE DE RECETA
========================================= */

async function mostrarDetalleReceta(receta, detalles) {

    const modal = document.getElementById("modalVerReceta");
    const cuerpo = document.getElementById("cuerpoDetalleReceta");

    if (!modal || !cuerpo) {
        throw new Error("No se encontró el modal de consulta.");
    }

    // Consultar los nombres y unidades de los productos
    const respuesta = await fetch(
        "../webapi/Productos/CargarProductos"
    );

    if (!respuesta.ok) {
        throw new Error("No se pudieron consultar los productos.");
    }

    const productos = await respuesta.json();

    if (!Array.isArray(productos)) {
        throw new Error("La lista de productos es inválida.");
    }

    const productosPorId = new Map(
        productos.map(producto => [
            Number(producto.ID_PRODUCTO),
            producto
        ])
    );

    // Datos generales de la receta
    document.getElementById("tituloVerReceta").textContent =
        receta.NOMBRE;

    const productoTerminado = productosPorId.get(
        Number(receta.ID_PRODUCTO)
    );

    document.getElementById("productoVerReceta").textContent =
        productoTerminado?.NOMBRE || "Producto no encontrado";

    document.getElementById("rendimientoVerReceta").textContent =
        receta.RENDIMIENTO +
        " " +
        (productoTerminado?.UNIDAD_MEDIDA || "unidades");

    document.getElementById("estadoVerReceta").textContent =
        String(receta.ESTADO) === "1"
            ? "Activa"
            : "Inactiva";

    // Limpiar los ingredientes anteriores
    cuerpo.innerHTML = "";

    detalles.forEach(function (detalle) {

        const ingrediente = productosPorId.get(
            Number(detalle.ID_PRODUCTO)
        );

        const fila = document.createElement("tr");

        const valores = [
            ingrediente?.NOMBRE || "Ingrediente no encontrado",
            detalle.CANTIDAD,
            ingrediente?.UNIDAD_MEDIDA || "-"
        ];

        valores.forEach(function (valor) {

            const celda = document.createElement("td");
            celda.textContent = valor;
            fila.appendChild(celda);

        });

        cuerpo.appendChild(fila);
    });

    if (detalles.length === 0) {

        const fila = document.createElement("tr");
        const celda = document.createElement("td");

        celda.colSpan = 3;
        celda.textContent = "Esta receta no tiene ingredientes registrados.";

        fila.appendChild(celda);
        cuerpo.appendChild(fila);
    }

    modal.hidden = false;
}



/* =========================================
   CERRAR DETALLE DE RECETA
========================================= */

function cerrarDetalleReceta() {

    const modal = document.getElementById("modalVerReceta");

    if (modal) {
        modal.hidden = true;
    }
}

document.getElementById("btnCerrarVerReceta")
    ?.addEventListener("click", cerrarDetalleReceta);

document.getElementById("btnCerrarDetalleReceta")
    ?.addEventListener("click", cerrarDetalleReceta);



/* =========================================
   EDITAR DESDE EL MODAL VER RECETA
========================================= */

let idRecetaVisualizada = null;


/* =========================================
   BOTÓN EDITAR DESDE VER RECETA
========================================= */



/* =========================================
   EDITAR RECETA DESDE MODAL VER RECETA
========================================= */

document.addEventListener("click", async function (evento) {

    const boton = evento.target.closest(
        "#btnEditarDesdeDetalle"
    );

    if (!boton) {
        return;
    }

    evento.preventDefault();

    console.log("CLIC EN EDITAR RECETA");
    console.log("ID RECETA:", idRecetaVisualizada);



    if (!idRecetaVisualizada) {

        await mostrarNotificacion(
            "warning",
            "Receta no seleccionada",
            "Primero debes consultar una receta."
        );

        return;
    }

    await editarReceta(idRecetaVisualizada);

});


let idRecetaEnEdicion = null;



/* =========================================
   INGRESO DE EXISTENCIAS DE INGREDIENTES
========================================= */

document.addEventListener("DOMContentLoaded", function () {

    const botonAbrir = document.getElementById("btnIngresarExistencias");
    const modal = document.getElementById("modalIngresarExistencias");
    const formulario = document.getElementById("formIngresarExistencias");
    const selector = document.getElementById("insumoIngreso");
    const cantidadInput = document.getElementById("cantidadIngreso");
    const existenciaActual = document.getElementById("existenciaActualIngreso");
    const existenciaNueva = document.getElementById("existenciaNuevaIngreso");
    const botonGuardar = document.getElementById("btnGuardarIngreso");

    if (!botonAbrir || !modal || !formulario || !selector) {
        console.error("Faltan elementos del formulario de existencias.");
        return;
    }

    let insumos = [];

    function cerrarIngreso() {
        modal.hidden = true;
        formulario.reset();
        existenciaActual.textContent = "—";
        existenciaNueva.textContent = "—";
    }

    function actualizarExistencias() {
        const insumo = insumos.find(p =>
            String(p.ID_PRODUCTO) === selector.value
        );

        if (!insumo) {
            existenciaActual.textContent = "—";
            existenciaNueva.textContent = "—";
            return;
        }

        const actual = Number(insumo.EXISTENCIA);
        const cantidad = Number(cantidadInput.value);

        existenciaActual.textContent =
            actual + " " + insumo.UNIDAD_MEDIDA;

        existenciaNueva.textContent =
            (actual + (cantidad > 0 ? cantidad : 0)) +
            " " + insumo.UNIDAD_MEDIDA;
    }

    
botonAbrir.addEventListener("click", async function () {

    botonAbrir.disabled = true;

    try {

        const respuesta = await fetch(
            "../webapi/Productos/CargarProductos",
            {
                method: "GET",
                credentials: "same-origin",
                headers: {
                    "Accept": "application/json"
                }
            }
        );

        if (!respuesta.ok) {
            throw new Error("HTTP " + respuesta.status);
        }

        const productos = await respuesta.json();

        console.log("Productos recibidos:", productos);

        if (!Array.isArray(productos)) {
            throw new Error("El servicio no devolvió una lista.");
        }

        // Filtrar únicamente ingredientes activos.
        insumos = productos.filter(function (producto) {
            return String(producto.TIPO_PRODUCTO).trim().toUpperCase() === "I"
                && String(producto.ESTADO).trim() === "1";
        });

        console.log("Ingredientes disponibles:", insumos);

        selector.innerHTML = "";

        const opcionInicial = document.createElement("option");
        opcionInicial.value = "";
        opcionInicial.textContent = "Seleccionar ingrediente";
        selector.appendChild(opcionInicial);

        
insumos.forEach(function (insumo) {

    const opcion = document.createElement("option");

    opcion.value = String(insumo.ID_PRODUCTO);

    opcion.textContent =
        String(insumo.NOMBRE) +
        " (" + String(insumo.UNIDAD_MEDIDA) + ")";

    selector.add(opcion);

});

console.log("Opciones cargadas:", selector.options.length);


        formulario.reset();
selector.selectedIndex = 0;
actualizarExistencias();
modal.hidden = false;

        if (insumos.length === 0) {
            await mostrarNotificacion(
                "warning",
                "Sin ingredientes disponibles",
                "No se encontraron ingredientes activos."
            );
        }

    } catch (error) {

        console.error("Error al cargar ingredientes:", error);

        await mostrarNotificacion(
            "error",
            "Error al consultar ingredientes",
            error.message
        );

    } finally {

        botonAbrir.disabled = false;
    }

});

    document.getElementById("btnCerrarIngreso")
        .addEventListener("click", cerrarIngreso);

    document.getElementById("btnCancelarIngreso")
        .addEventListener("click", cerrarIngreso);

    selector.addEventListener("change", actualizarExistencias);
    cantidadInput.addEventListener("input", actualizarExistencias);

    formulario.addEventListener("submit", async function (evento) {
        evento.preventDefault();

        const idProducto = Number(selector.value);
        const cantidad = Number(cantidadInput.value);

        if (!Number.isInteger(idProducto) || idProducto <= 0 ||
            !Number.isFinite(cantidad) || cantidad <= 0) {

            await mostrarNotificacion(
                "warning",
                "Datos inválidos",
                "Selecciona un ingrediente y una cantidad mayor que cero."
            );
            return;
        }

        botonGuardar.disabled = true;
        botonGuardar.textContent = "Guardando...";

        try {
            const respuesta = await fetch(
                "../webapi/Productos/IngresarExistencias",
                {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                    body: JSON.stringify({
                        ID_PRODUCTO: idProducto,
                        CANTIDAD: cantidad
                    })
                }
            );

            const tipoRespuesta =
                respuesta.headers.get("content-type") || "";

            if (!tipoRespuesta.includes("application/json")) {
                throw new Error(
                    "El servidor no devolvió JSON. Verifica la sesión."
                );
            }

            const datos = await respuesta.json();

            if (!respuesta.ok || datos.EXITO !== true) {
                throw new Error(
                    datos.MENSAJE || "No se pudo ingresar la existencia."
                );
            }

            cerrarIngreso();

await mostrarNotificacion(
    "success",
    "¡Existencias actualizadas!",
    datos.MENSAJE
);

// Actualizar inventario, estadísticas y gráficas.
window.location.reload();
return;

            await cargarProductos();
           // Las estadísticas se actualizarán al recargar la página.

            // Actualizar las gráficas con los nuevos valores.
            // Se evita duplicar gráficos existentes de Chart.js.
         

        } catch (error) {
            console.error("Error al ingresar existencias:", error);

            await mostrarNotificacion(
                "error",
                "No se pudo completar el ingreso",
                error.message
            );

        } finally {
            botonGuardar.disabled = false;
            botonGuardar.textContent = "Guardar ingreso";
        }
    });
});
