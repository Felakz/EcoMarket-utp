# Configuración de AWS S3 para EcoMarket

Este documento explica cómo configurar AWS S3 para el almacenamiento de imágenes en EcoMarket.

## 1. Crear una cuenta de AWS

1. Ve a [AWS Console](https://aws.amazon.com/)
2. Crea una cuenta gratuita (incluye 5GB gratis de S3 por 12 meses)

## 2. Crear un bucket de S3

1. Ve a AWS Console > S3
2. Click en "Create bucket"
3. Configuración recomendada:
   - **Bucket name**: `ecomarket-images` (debe ser único globalmente)
   - **AWS Region**: `us-east-1` (o la más cercana a ti)
   - **Block Public Access**: Desmarcar si quieres URLs públicas directas
   - **Bucket Versioning**: Opcional (recomendado para backups)
   - **Encryption**: Habilitar SSE-S3

### Configurar permisos públicos (si quieres URLs públicas)

Si quieres que las imágenes sean públicas:

1. En tu bucket, ve a "Permissions" > "Block public access"
2. Edit y desactiva "Block all public access"
3. En "Bucket Policy", agrega:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::ecomarket-images/*"
        }
    ]
}
```

4. En "CORS configuration":

```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE", "HEAD"],
        "AllowedOrigins": ["*"],
        "ExposeHeaders": ["ETag"]
    }
]
```

## 3. Crear credenciales de acceso (IAM)

1. Ve a AWS Console > IAM > Users
2. Click "Add users"
3. Nombre: `ecomarket-s3-user`
4. Access type: "Programmatic access"
5. Permisos: Adjunta la política `AmazonS3FullAccess` o crea una personalizada:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::ecomarket-images",
                "arn:aws:s3:::ecomarket-images/*"
            ]
        }
    ]
}
```

6. Guarda el **Access Key ID** y **Secret Access Key**

## 4. Configurar el proyecto

### Opción A: Variables de entorno (Recomendado para producción)

En tu sistema o servidor, define:

```bash
# Windows (PowerShell)
$env:AWS_ACCESS_KEY="tu-access-key-aqui"
$env:AWS_SECRET_KEY="tu-secret-key-aqui"
$env:AWS_S3_BUCKET="ecomarket-images"
$env:AWS_REGION="us-east-1"

# Linux/Mac
export AWS_ACCESS_KEY="tu-access-key-aqui"
export AWS_SECRET_KEY="tu-secret-key-aqui"
export AWS_S3_BUCKET="ecomarket-images"
export AWS_REGION="us-east-1"
```

### Opción B: Archivo de propiedades (Solo para desarrollo local)

Edita `backend/src/main/resources/application-dev.properties`:

```properties
aws.s3.access-key=AKIAIOSFODNN7EXAMPLE
aws.s3.secret-key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
aws.s3.bucket-name=ecomarket-images
aws.s3.region=us-east-1
aws.s3.use-presigned-urls=false
```

**⚠️ IMPORTANTE:** 
- NO subas este archivo con credenciales reales a Git
- Agrega las credenciales al `.gitignore`
- En producción, usa variables de entorno o AWS IAM Roles

## 5. Actualizar .gitignore

Agrega al archivo `.gitignore`:

```
# AWS credentials
**/application-dev.properties
.env
.aws-credentials

# Imágenes locales (ya no necesarias con S3)
backend/uploads/
uploads/
```

## 6. Endpoints disponibles

### Subir imagen
```http
POST /api/images/upload
Content-Type: multipart/form-data

file: [archivo de imagen]
```

Respuesta:
```json
{
    "success": true,
    "message": "Imagen subida exitosamente",
    "key": "images/uuid.jpg",
    "url": "https://ecomarket-images.s3.us-east-1.amazonaws.com/images/uuid.jpg",
    "filename": "producto.jpg",
    "size": 152341,
    "contentType": "image/jpeg"
}
```

### Obtener URL de imagen
```http
GET /api/images/images%2Fuuid.jpg/url
```

Respuesta:
```json
{
    "key": "images/uuid.jpg",
    "url": "https://ecomarket-images.s3.us-east-1.amazonaws.com/images/uuid.jpg"
}
```

### Eliminar imagen
```http
DELETE /api/images/images%2Fuuid.jpg
```

## 7. Uso en el frontend

```typescript
// Subir imagen
uploadImage(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  
  return this.http.post<any>('/api/images/upload', formData);
}

// Usar la URL retornada directamente en el HTML
<img [src]="product.imageUrl" alt="Product">
```

## 8. Migrar imágenes existentes

Si ya tienes imágenes locales y quieres migrarlas a S3:

1. Usa AWS CLI:
```bash
aws s3 sync ./backend/uploads/ s3://ecomarket-images/images/
```

2. O usa un script de migración (puedes crear un endpoint temporal en el backend)

## 9. Costos

AWS S3 Free Tier (primeros 12 meses):
- 5 GB de almacenamiento estándar
- 20,000 solicitudes GET
- 2,000 solicitudes PUT

Después del Free Tier (aproximado):
- $0.023 por GB/mes de almacenamiento
- $0.0004 por 1,000 solicitudes GET
- $0.005 por 1,000 solicitudes PUT

Para una tienda pequeña-mediana, el costo mensual es típicamente $1-5 USD.

## 10. Seguridad

### URLs prefirmadas (para buckets privados)

Si quieres mantener el bucket privado y generar URLs temporales:

1. Deja "Block all public access" activado en el bucket
2. En `application.properties`, configura:
```properties
aws.s3.use-presigned-urls=true
```

3. Las URLs generadas expirarán en 1 hora (configurable en `S3ImageStorageService.java`)

### Variables de entorno en producción

Para despliegue en servicios como:

- **Heroku**: Settings > Config Vars
- **AWS Elastic Beanstalk**: Configuration > Software > Environment properties
- **Docker**: `docker run -e AWS_ACCESS_KEY=xxx`
- **Kubernetes**: ConfigMaps o Secrets

## 11. Testing

Puedes probar con Postman importando la colección incluida en el proyecto.

## Troubleshooting

### Error: Access Denied
- Verifica que las credenciales IAM tengan permisos correctos
- Confirma que el bucket existe y el nombre es correcto

### Error: CORS
- Verifica la configuración CORS del bucket
- Asegúrate de que el origen del frontend esté permitido

### Imágenes no cargan
- Si usas bucket público, verifica la Bucket Policy
- Si usas URLs prefirmadas, pueden haber expirado (regenerar con GET /url)

### Credenciales no funcionan
- Verifica que estén en variables de entorno o application.properties
- Confirma que no haya espacios extra en las credenciales
