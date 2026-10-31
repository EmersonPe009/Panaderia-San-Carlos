
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Iniciar sesión | San Carlos UMG GUATEMALA</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>

    <main class="login-pagina">

        <section class="login-presentacion">
            <a href="${pageContext.request.contextPath}/index.jsp"
               class="login-logo">
                SAN CARLOS
            </a>

            <div class="presentacion-contenido">
                <p>GESTIÓN DE PANADERÍA</p>
                <h1>Bienvenido<br>de nuevo.</h1>
                <span>
                    Administra las operaciones de
                    Panadería San Carlos desde un solo lugar.
                </span>
            </div>

            <p class="presentacion-pie">
                PANADERÍA SAN CARLOS UMG GUATEMALA © 2026
            </p>
        </section>

        <section class="login-formulario-contenedor">

            <div class="login-formulario">

                <p class="etiqueta-login">ACCESO AL SISTEMA</p>
                <h2>Iniciar sesión</h2>
                <p class="descripcion-login">
                    Ingresa tus credenciales para continuar.
                </p>

                <form id="formLogin">

                    <div class="campo-login">
                        <label for="usuario">Usuario</label>
                        <input type="text"
                               id="usuario"
                               name="usuario"
                               placeholder="Ingresa tu usuario"
                               autocomplete="username"
                               required>
                    </div>

                    <div class="campo-login">
                        <label for="contrasena">Contraseña</label>
                        <input type="password"
                               id="contrasena"
                               name="contrasena"
                               placeholder="Ingresa tu contraseña"
                               autocomplete="current-password"
                               required>
                    </div>

                    <button type="submit" class="btn-ingresar">
                        INGRESAR
                    </button>

                    <p id="mensajeLogin"
                       class="mensaje-login"
                       role="status"
                       aria-live="polite"></p>

                </form>

                <a href="${pageContext.request.contextPath}/index.jsp"
                   class="volver-inicio">
                    ← Volver a la página principal
                </a>

            </div>

        </section>

    </main>

<script>
    window.APP_CONTEXT = "${pageContext.request.contextPath}";
</script>

<script src="${pageContext.request.contextPath}/js/login.js"></script>

                   
                   
</body>
</html>
