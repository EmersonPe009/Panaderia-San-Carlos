
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Panadería San Carlos | Inicio</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/inicio.css">
</head>

<body>

    <section class="hero">

        <header class="encabezado-publico">

            <a href="${pageContext.request.contextPath}/index.jsp"
               class="logo">
                SAN CARLOS
            </a>

            <nav class="navegacion-publica">

                <a href="#inicio">INICIO</a>
                <a href="#nosotros">NOSOTROS</a>
                <a href="#productos">PRODUCTOS</a>

            </nav>

            <a href="${pageContext.request.contextPath}/login.jsp"
               class="btn-login">
                INICIAR SESIÓN
            </a>

        </header>

        <main class="hero-contenido" id="inicio">

            <p class="bienvenida-hero">
                Bienvenidos a
            </p>

            <h1>PANADERÍA<br>SAN CARLOS</h1>

            <p class="descripcion-hero">
                Tradición, calidad y pasión por el pan.
                Elaboramos nuestros productos con dedicación
                para compartir el mejor sabor cada día.
            </p>

            <div class="hero-botones">

                <a href="#productos" class="btn-principal-hero">
                    NUESTROS PRODUCTOS
                </a>

                <a href="${pageContext.request.contextPath}/login.jsp"
                   class="btn-secundario-hero">
                    ACCESO AL SISTEMA
                </a>

            </div>

        </main>

    </section>

    <section class="seccion-publica" id="nosotros">
        <p class="etiqueta">NUESTRA ESENCIA</p>
        <h2>Hecho con dedicación</h2>
        <p>
            En Panadería San Carlos creemos en la calidad,
            el cuidado de nuestros ingredientes y el valor
            de compartir un buen pan.
        </p>
    </section>

    <section class="seccion-publica seccion-alterna" id="productos">
        <p class="etiqueta">PANADERÍA SAN CARLOS</p>
        <h2>Nuestros productos</h2>
        <p>
            Descubre nuestra variedad de productos
            elaborados con dedicación.
        </p>
    </section>

    <footer class="pie-publico">
        <p>SAN CARLOS © 2026 | PANADERÍA</p>
    </footer>

</body>
</html>
