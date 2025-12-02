# 📸 Guía para Subir y Gestionar Imágenes en EcoMarket

## 🎯 Estado Actual del Proyecto

✅ **Backend compilado y corriendo** (puerto 8080)  
✅ **Frontend Angular funcional** (puerto 4200)  
✅ **Endpoint de upload de imágenes creado**  
✅ **Productos sin imágenes (imageFilename = null)**

---

## 📌 Opciones para Agregar Imágenes

### **Opción 1: Subir imágenes usando Postman o cURL** ⭐ Recomendado

#### A. Crear directorio de uploads (si no existe)
```powershell
# En la raíz del proyecto backend
mkdir c:\Users\DARIO\EcoMarket-springboot\backend-v2\uploads
```

#### B. Usando **Postman**:

1. **Crear nueva petición POST**
   - URL: `http://localhost:8080/ecomarket/api/upload/image`
   - Método: `POST`
   
2. **En la pestaña "Body"**:
   - Selecciona: `form-data`
   - Key: `file` (cambiar tipo a "File")
   - Value: Selecciona tu imagen desde tu computadora
   
3. **Click en "Send"**

**Respuesta exitosa:**
```json
{
  "filename": "abc123-uuid.jpg",
  "url": "/ecomarket/api/images/abc123-uuid.jpg",
  "message": "Imagen subida exitosamente"
}
```

#### C. Usando **cURL** desde PowerShell:

```powershell
# Navega a la carpeta donde tienes tu imagen
cd C:\Users\DARIO\Desktop\imagenes-productos

# Sube la imagen
curl -X POST http://localhost:8080/ecomarket/api/upload/image `
  -F "file=@mochila-eco.jpg"
```

**Ejemplo subir varias imágenes:**
```powershell
# Desde la carpeta con tus imágenes
Get-ChildItem *.jpg | ForEach-Object {
    curl -X POST http://localhost:8080/ecomarket/api/upload/image `
      -F "file=@$($_.Name)"
}
```

---

### **Opción 2: Copiar imágenes manualmente**

Si ya tienes imágenes con nombres específicos:

```powershell
# 1. Crear directorio uploads
mkdir c:\Users\DARIO\EcoMarket-springboot\backend-v2\uploads

# 2. Copiar tus imágenes ahí
# Por ejemplo:
cp C:\Users\DARIO\Desktop\mis-imagenes\*.jpg c:\Users\DARIO\EcoMarket-springboot\backend-v2\uploads\
```

Luego, actualiza los productos en la base de datos con los nombres de archivo correctos.

---

### **Opción 3: Usar URLs externas (temporalmente)**

Si quieres probar rápidamente sin subir archivos, modifica `DataSeeder.java` para usar URLs externas:

**Ejemplo:**
```java
.imageFilename("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400")
```

Luego recompila con:
```powershell
cd c:\Users\DARIO\EcoMarket-springboot\backend-v2
mvn clean package -DskipTests
java -jar target/backend-v2-1.0.0.jar
```

---

## 🔗 Asociar Imágenes a Productos

### Método 1: Usando la API de actualización de productos

```powershell
# Actualizar producto ID 1 con la imagen subida
curl -X PUT http://localhost:8080/ecomarket/api/products/1 `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer TU_JWT_TOKEN" `
  -d '{
    "name": "Mochila Ecológica",
    "imageFilename": "abc123-uuid.jpg",
    ...otros campos
  }'
```

### Método 2: Directamente en H2 Console

1. Abre: http://localhost:8080/ecomarket/api/h2-console
2. JDBC URL: `jdbc:h2:mem:ecomarket`
3. User: `sa`
4. Password: *(vacío)*
5. Ejecuta SQL:

```sql
-- Ver productos actuales
SELECT id, name, image_filename FROM products;

-- Actualizar imagen del producto 1
UPDATE products SET image_filename = 'nombre-archivo.jpg' WHERE id = 1;

-- Verificar
SELECT id, name, image_filename FROM products;
```

---

## 📝 Productos que Necesitan Imágenes

Los siguientes productos están en la base de datos y necesitan imágenes:

| ID | Nombre del Producto | Sugerencia de Imagen |
|----|---------------------|----------------------|
| 1  | Mochila Ecológica | Mochila de tela reciclada |
| 2  | Mesa de Material Reciclado | Mesa de madera recuperada |
| 3  | Bolsa Reutilizable | Bolsa de tela orgánica |
| 4  | Silla Reciclada | Silla de plástico reciclado |
| 5  | Set de Cubiertos de Bambú | Cubiertos de bambú |
| 6  | Lámpara Solar Reciclada | Lámpara solar |
| 7  | Tapete de Yoga Ecológico | Tapete de yoga |
| 8  | Estante de Madera Reciclada | Estante minimalista |

---

## 🌐 Acceso a las Imágenes

Una vez subidas, las imágenes estarán disponibles en:

```
http://localhost:8080/ecomarket/api/images/nombre-archivo.jpg
```

El frontend Angular automáticamente las cargará desde ese endpoint.

---

## 🛡️ Formatos de Imagen Soportados

- ✅ JPG/JPEG
- ✅ PNG
- ✅ GIF
- ✅ WEBP
- ✅ SVG

---

## 🧪 Probar que Todo Funciona

### 1. Verificar backend
```powershell
# El backend debe estar corriendo
# Deberías ver: "Tomcat started on port 8080"
```

### 2. Subir imagen de prueba
```powershell
curl -X POST http://localhost:8080/ecomarket/api/upload/image `
  -F "file=@test-image.jpg"
```

### 3. Verificar que se guardó
```powershell
ls c:\Users\DARIO\EcoMarket-springboot\backend-v2\uploads
```

### 4. Acceder desde el navegador
Abre: `http://localhost:8080/ecomarket/api/images/nombre-del-archivo.jpg`

### 5. Probar en el frontend
```powershell
cd c:\Users\DARIO\EcoMarket-springboot\frontend-angular
npm start
# Abre http://localhost:4200
```

---

## 🚀 Siguiente Paso: AWS Deployment

Una vez que todo funcione localmente:

1. ✅ Verifica que las imágenes se cargan correctamente
2. ✅ Prueba login, productos, carrito, etc.
3. ✅ Sigue la guía `AWS_DEPLOYMENT_GUIDE.md` para subir a AWS

---

## 🆘 Solución de Problemas

### ❌ Error 403 Forbidden al acceder a imágenes
- Verifica que el archivo existe en `backend-v2/uploads/`
- Verifica que el endpoint `/images/**` está en `permitAll()` en SecurityConfig

### ❌ No puedo subir imágenes
- Verifica que el backend está corriendo
- Verifica que el endpoint es: `POST /upload/image`
- Verifica que el campo se llama exactamente `file`

### ❌ Imágenes no aparecen en el frontend
- Abre la consola del navegador (F12)
- Verifica la URL completa de la imagen
- Verifica que no hay errores CORS

---

## 📞 Comandos Útiles

```powershell
# Ver logs del backend (si está en segundo plano)
# Ver proceso Java corriendo
Get-Process java

# Reiniciar backend
# 1. Detener (Ctrl+C en la terminal donde corre)
# 2. Recompilar si hiciste cambios
cd c:\Users\DARIO\EcoMarket-springboot\backend-v2
mvn clean package -DskipTests

# 3. Ejecutar
java -jar target/backend-v2-1.0.0.jar

# Iniciar frontend
cd c:\Users\DARIO\EcoMarket-springboot\frontend-angular
npm start
```

---

✨ **¡Listo! Ahora puedes subir imágenes y tener tu tienda EcoMarket completamente funcional localmente.**
