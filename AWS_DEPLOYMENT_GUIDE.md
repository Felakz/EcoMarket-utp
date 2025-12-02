# Guía de Despliegue en AWS - EcoMarket

## 📋 Requisitos Previos

- Cuenta de AWS activa
- AWS CLI configurado
- Maven instalado (para el backend)
- Node.js y npm (para el frontend)

## 🔧 Configuración para AWS

### 1. Backend (Spring Boot) - Opciones de Despliegue

#### Opción A: AWS Elastic Beanstalk (Recomendado para principiantes)

1. **Crear el archivo JAR:**
   ```bash
   cd backend-v2
   mvn clean package -DskipTests
   ```

2. **Configurar variables de entorno en Elastic Beanstalk:**
   - `SERVER_PORT=5000` (puerto por defecto de Elastic Beanstalk)
   - `AWS_ACCESS_KEY_ID=tu-access-key`
   - `AWS_SECRET_ACCESS_KEY=tu-secret-key`
   - `RDS_HOSTNAME=tu-rds-endpoint` (si usas RDS)
   - `RDS_PORT=5432`
   - `RDS_DB_NAME=ecomarket`
   - `RDS_USERNAME=postgres`
   - `RDS_PASSWORD=tu-password`

3. **Desplegar:**
   ```bash
   eb init -p java-17 ecomarket-backend
   eb create ecomarket-env
   eb deploy
   ```

#### Opción B: AWS EC2

1. **Crear instancia EC2:**
   - Amazon Linux 2 o Ubuntu
   - Tipo: t2.micro (Free tier) o superior
   - Security Group: Permitir puerto 8080

2. **Conectar y configurar:**
   ```bash
   ssh -i tu-key.pem ec2-user@tu-ip-publica
   
   # Instalar Java
   sudo yum install java-17-amazon-corretto -y
   
   # Copiar JAR
   scp -i tu-key.pem backend-v2/target/backend-v2-1.0.0.jar ec2-user@tu-ip:/home/ec2-user/
   
   # Ejecutar
   java -jar backend-v2-1.0.0.jar
   ```

3. **Configurar como servicio (systemd):**
   ```bash
   sudo nano /etc/systemd/system/ecomarket.service
   ```
   
   Contenido:
   ```ini
   [Unit]
   Description=EcoMarket Backend
   After=network.target

   [Service]
   User=ec2-user
   ExecStart=/usr/bin/java -jar /home/ec2-user/backend-v2-1.0.0.jar
   SuccessExitStatus=143
   Restart=always

   [Install]
   WantedBy=multi-user.target
   ```

   ```bash
   sudo systemctl enable ecomarket
   sudo systemctl start ecomarket
   ```

### 2. Base de Datos - AWS RDS (PostgreSQL)

1. **Crear instancia RDS:**
   - Motor: PostgreSQL 15
   - Tipo: db.t3.micro (Free tier)
   - Almacenamiento: 20 GB
   - VPC: Misma que tu EC2/Elastic Beanstalk

2. **Actualizar `application.properties`:**
   ```properties
   spring.datasource.url=jdbc:postgresql://${RDS_HOSTNAME}:${RDS_PORT}/${RDS_DB_NAME}
   spring.datasource.username=${RDS_USERNAME}
   spring.datasource.password=${RDS_PASSWORD}
   spring.jpa.hibernate.ddl-auto=update
   ```

### 3. Almacenamiento de Imágenes - AWS S3

1. **Crear bucket S3:**
   ```bash
   aws s3 mb s3://ecomarket-products --region us-east-1
   ```

2. **Configurar política de bucket (público para lectura):**
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Sid": "PublicReadGetObject",
         "Effect": "Allow",
         "Principal": "*",
         "Action": "s3:GetObject",
         "Resource": "arn:aws:s3:::ecomarket-products/*"
       }
     ]
   }
   ```

3. **Habilitar CORS en el bucket:**
   ```json
   [
     {
       "AllowedHeaders": ["*"],
       "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
       "AllowedOrigins": ["*"],
       "ExposeHeaders": []
     }
   ]
   ```

4. **Actualizar `application.properties`:**
   ```properties
   aws.s3.enabled=true
   aws.s3.bucket-name=ecomarket-products
   aws.s3.region=us-east-1
   ```

### 4. Frontend (Angular) - Despliegue

#### Opción A: AWS Amplify

1. **Conectar repositorio de GitHub:**
   - Ve a AWS Amplify Console
   - Conecta tu repositorio
   - Configura variables de entorno:
     - `API_URL=https://tu-backend.elasticbeanstalk.com/ecomarket/api`

2. **Configurar build:**
   ```yaml
   version: 1
   frontend:
     phases:
       preBuild:
         commands:
           - cd frontend-angular
           - npm ci
       build:
         commands:
           - npm run build -- --configuration production
     artifacts:
       baseDirectory: frontend-angular/dist/frontend-angular/browser
       files:
         - '**/*'
     cache:
       paths:
         - frontend-angular/node_modules/**/*
   ```

#### Opción B: S3 + CloudFront

1. **Build de producción:**
   ```bash
   cd frontend-angular
   npm run build -- --configuration production
   ```

2. **Subir a S3:**
   ```bash
   aws s3 sync dist/frontend-angular/browser/ s3://ecomarket-frontend --delete
   ```

3. **Configurar S3 como sitio web estático**

4. **Crear distribución CloudFront** apuntando al bucket S3

### 5. Configuración de CORS

**Actualizar `SecurityConfig.java`** con tu dominio de AWS:

```java
configuration.setAllowedOrigins(List.of(
    "http://localhost:4200",
    "https://tu-dominio-amplify.amplifyapp.com",
    "https://tu-dominio-cloudfront.cloudfront.net"
));
```

## 🔒 Seguridad

1. **No commitear credenciales** - Usar variables de entorno
2. **Configurar Security Groups** adecuadamente
3. **Usar HTTPS** con certificados SSL (AWS Certificate Manager)
4. **Implementar rate limiting** en API Gateway si aplica
5. **Habilitar CloudWatch** para monitoreo

## 📊 Monitoreo

- **CloudWatch Logs** para logs de aplicación
- **CloudWatch Metrics** para métricas del sistema
- **X-Ray** para tracing distribuido (opcional)

## 💰 Costos Estimados (Free Tier)

- EC2 t2.micro: Gratis primer año
- RDS db.t3.micro: Gratis primer año (750 horas/mes)
- S3: 5 GB gratis
- CloudFront: 50 GB de transferencia gratis/mes
- Elastic Beanstalk: Sin costo adicional (pagas por recursos)

## 🚀 Checklist de Despliegue

- [ ] Cambiar `spring.jpa.hibernate.ddl-auto` de `create-drop` a `update`
- [ ] Configurar variables de entorno para credenciales
- [ ] Habilitar S3 para imágenes en producción
- [ ] Actualizar CORS con dominios de producción
- [ ] Configurar RDS para base de datos persistente
- [ ] Actualizar `environment.prod.ts` con URL del backend
- [ ] Configurar HTTPS con certificado SSL
- [ ] Configurar backup de base de datos
- [ ] Implementar logging centralizado
- [ ] Configurar alarmas de CloudWatch

## 📝 URLs Finales

Después del despliegue tendrás:
- **Backend**: `https://tu-app.elasticbeanstalk.com/ecomarket/api` o `http://tu-ec2-ip:8080/ecomarket/api`
- **Frontend**: `https://tu-dominio.amplifyapp.com` o `https://tu-cloudfront.cloudfront.net`
- **Base de Datos**: `tu-rds-endpoint.region.rds.amazonaws.com:5432`
- **S3 Imágenes**: `https://ecomarket-products.s3.amazonaws.com/`

## 🔄 Actualizaciones

Para actualizar el backend:
```bash
mvn clean package -DskipTests
eb deploy
# o
scp -i tu-key.pem target/backend-v2-1.0.0.jar ec2-user@tu-ip:/home/ec2-user/
ssh -i tu-key.pem ec2-user@tu-ip 'sudo systemctl restart ecomarket'
```

Para actualizar el frontend:
```bash
npm run build -- --configuration production
aws s3 sync dist/frontend-angular/browser/ s3://ecomarket-frontend --delete
```
