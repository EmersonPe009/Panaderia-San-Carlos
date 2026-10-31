
document.addEventListener("DOMContentLoaded", function () {

    const formulario = document.getElementById("formLogin");
    const mensaje = document.getElementById("mensajeLogin");
    const boton = formulario.querySelector("button[type='submit']");

    formulario.addEventListener("submit", async function (event) {

        event.preventDefault();

        const usuario = document.getElementById("usuario").value.trim();
        const contrasena = document.getElementById("contrasena").value;

        mensaje.textContent = "";
        boton.disabled = true;
        boton.textContent = "VERIFICANDO...";

        try {

            const respuesta = await fetch(
                window.APP_CONTEXT + "/webapi/Usuarios/Login",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                    credentials: "same-origin",
                    body: JSON.stringify({
                        usuario: usuario,
                        contrasena: contrasena
                    })
                }
            );

            const datos = await respuesta.json();

            if (respuesta.ok && datos.AUTENTICADO === true) {

    const rol = String(datos.ROL || "")
        .trim()
        .toUpperCase();

    if (rol === "ADMINISTRADOR") {
        window.location.href =
            window.APP_CONTEXT + "/pages/dashboard.jsp";
        return;
    }

    if (rol === "SUCURSAL") {
        window.location.href =
            window.APP_CONTEXT + "/pages/empleado.jsp";
        return;
    }

    if (rol === "INVENTARIO") {
        window.location.href =
            window.APP_CONTEXT + "/pages/productos.jsp";
        return;
    }

    if (rol === "PRODUCCION") {
        window.location.href =
            window.APP_CONTEXT + "/pages/produccion.jsp";
        return;
    }

    mensaje.textContent =
        "No existe una página asignada para este rol.";

    return;
}

            mensaje.textContent =
                datos.MENSAJE || "No fue posible iniciar sesión.";

        } catch (error) {

            console.error("Error al iniciar sesión:", error);

            mensaje.textContent =
                "No se pudo conectar con el servidor.";

        } finally {

            boton.disabled = false;
            boton.textContent = "INGRESAR";
        }
    });
});
