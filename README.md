# Conversor de Monedas en Java 💱

Este proyecto es un **conversor de monedas** hecho en Java con interfaz gráfica Swing, que utiliza la API de [Exchangerate-API](https://www.exchangerate-api.com/) para obtener tasas de cambio en tiempo real.

Permite seleccionar una moneda base, una moneda destino, ingresar un monto y obtener la conversión actualizada al instante.


---

## 🚀 ¿Cómo usarlo?

1. Cloná el repositorio:

```bash
git clone https://github.com/arielito1989/Conversor-Monedas-Java.git
cd Conversor-Monedas-Java

```
2- Creá un archivo llamado config.properties en la raíz del proyecto con el siguiente contenido:

```
api_key=TU_API_KEY_AQUI
```
⚠️ Reemplazá TU_API_KEY_AQUI con tu clave real de exchangerate-api.

---

3- Compilá y ejecutá el proyecto desde IntelliJ o cualquier entorno Java.

---
🛠️ Tecnologías utilizadas
-
Java 17 (o compatible)

Swing (interfaz gráfica)

JSON (org.json)

Git y GitHub

🛡️ Buenas prácticas
-
✅ API key protegida mediante archivo config.properties (no subido al repositorio)

✅ .gitignore configurado para evitar archivos sensibles y de entorno

✅ Código organizado en paquetes (service, gui, util, main)

✅ Historial de Git limpiado para eliminar exposición previa de la clave

📌 Mejoras futuras
-
Guardar historial de conversiones

Validaciones de entrada

Manejo elegante de errores de red

Selector de idiomas (multi lenguaje)

Exportar conversiones a PDF o Excel

🤝 Contribuciones
-
¡Todo aporte es bienvenido! Si tenés ideas o querés mejorar algo, abrí un issue o enviá un pull request. 😄

## 🎥 Demo en video

Podés ver el funcionamiento del conversor en este video:

## 🎥 Demo en video

[![Ver el demo](https://github.com/arielito1989/Conversor-Monedas-Java/blob/master/img/captura-demo.png?raw=true)](https://github.com/arielito1989/Conversor-Monedas-Java/blob/master/demo/demo.mp4?raw=true)


