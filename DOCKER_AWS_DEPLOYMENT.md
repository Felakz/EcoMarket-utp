# 🐳 Guía Completa Docker + AWS Deployment - EcoMarket

## 📦 PASO 1: Probar Localmente con Docker

### 1.1 Build y Ejecutar con Docker Compose

```powershell
# Desde la raíz del proyecto
cd C:\Users\DARIO\EcoMarket-springboot

# Build y levantar todos los servicios
docker-compose up --build

# O en modo detached (background)
docker-compose up -d --build
```

**Servicios disponibles:**
- Frontend: http://localhost
- Backend: http://localhost:8080/ecomarket/api
- PostgreSQL: localhost:5432
- Adminer (DB Admin): http://localhost:8081

### 1.2 Verificar que todo funciona

```powershell
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs solo del backend
docker-compose logs -f backend

# Ver logs solo del frontend
docker-compose logs -f frontend

# Verificar contenedores corriendo
docker-compose ps

# Probar endpoints
curl http://localhost:8080/ecomarket/api/products
curl http://localhost:8080/ecomarket/api/actuator/health
```

### 1.3 Comandos Útiles Docker

```powershell
# Parar todos los servicios
docker-compose down

# Parar y eliminar volúmenes (base de datos)
docker-compose down -v

# Rebuild solo un servicio
docker-compose up -d --build backend

# Ejecutar comando en contenedor
docker-compose exec backend sh
docker-compose exec postgres psql -U ecomarket_user -d ecomarket_db

# Ver uso de recursos
docker stats
```

---

## 🚀 PASO 2: Deployment a AWS

### Opción A: ECS Fargate + RDS (Producción)

#### 2.1 Configurar AWS CLI

```powershell
# Descargar e instalar AWS CLI
# https://awscli.amazonaws.com/AWSCLIV2.msi

# Configurar credenciales
aws configure
# AWS Access Key ID: [Obtener de AWS Console IAM]
# AWS Secret Access Key: [Obtener de AWS Console IAM]
# Default region name: us-east-1
# Default output format: json

# Verificar configuración
aws sts get-caller-identity
```

#### 2.2 Crear RDS PostgreSQL

```powershell
# Crear base de datos PostgreSQL
aws rds create-db-instance `
  --db-instance-identifier ecomarket-db `
  --db-instance-class db.t4g.micro `
  --engine postgres `
  --engine-version 16.1 `
  --master-username ecomarketadmin `
  --master-user-password "EcoMarket2024!Secure" `
  --allocated-storage 20 `
  --storage-type gp3 `
  --backup-retention-period 7 `
  --multi-az `
  --db-name ecomarket_db `
  --region us-east-1

# Esperar a que esté disponible (5-10 min)
aws rds wait db-instance-available --db-instance-identifier ecomarket-db

# Obtener endpoint de la base de datos
$RDS_ENDPOINT = aws rds describe-db-instances `
  --db-instance-identifier ecomarket-db `
  --query 'DBInstances[0].Endpoint.Address' `
  --output text

Write-Host "✅ RDS Endpoint: $RDS_ENDPOINT"
```

#### 2.3 Crear S3 Bucket para Imágenes

```powershell
# Crear bucket S3
$BUCKET_NAME = "ecomarket-images-prod-$(Get-Random -Maximum 10000)"
aws s3 mb s3://$BUCKET_NAME --region us-east-1

# Habilitar CORS
@"
{
  "CORSRules": [
    {
      "AllowedHeaders": ["*"],
      "AllowedMethods": ["GET", "PUT", "POST"],
      "AllowedOrigins": ["*"],
      "ExposeHeaders": ["ETag"]
    }
  ]
}
"@ | Out-File -FilePath cors.json -Encoding utf8

aws s3api put-bucket-cors --bucket $BUCKET_NAME --cors-configuration file://cors.json

Write-Host "✅ S3 Bucket: $BUCKET_NAME"
```

#### 2.4 Push Backend a ECR

```powershell
# Crear repositorio en ECR
aws ecr create-repository --repository-name ecomarket-backend --region us-east-1

# Obtener URI del repositorio
$ACCOUNT_ID = aws sts get-caller-identity --query Account --output text
$ECR_URI = "$ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/ecomarket-backend"

Write-Host "ECR URI: $ECR_URI"

# Login en ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $ECR_URI

# Build y push imagen
cd backend-v2
docker build -t ecomarket-backend:latest .
docker tag ecomarket-backend:latest "$ECR_URI:latest"
docker push "$ECR_URI:latest"

Write-Host "✅ Imagen subida a ECR"
```

#### 2.5 Crear ECS Cluster

```powershell
# Crear cluster
aws ecs create-cluster --cluster-name ecomarket-cluster --region us-east-1

# Crear IAM Role para ECS
@"
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {"Service": "ecs-tasks.amazonaws.com"},
      "Action": "sts:AssumeRole"
    }
  ]
}
"@ | Out-File -FilePath trust-policy.json -Encoding utf8

aws iam create-role `
  --role-name ecomarketECSTaskRole `
  --assume-role-policy-document file://trust-policy.json

aws iam attach-role-policy `
  --role-name ecomarketECSTaskRole `
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy

# Adjuntar política S3 para subir imágenes
aws iam attach-role-policy `
  --role-name ecomarketECSTaskRole `
  --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess

Write-Host "✅ ECS Cluster y Role creados"
```

#### 2.6 Crear Task Definition

```powershell
# Crear archivo task-definition.json
@"
{
  "family": "ecomarket-backend-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::${ACCOUNT_ID}:role/ecomarketECSTaskRole",
  "taskRoleArn": "arn:aws:iam::${ACCOUNT_ID}:role/ecomarketECSTaskRole",
  "containerDefinitions": [
    {
      "name": "ecomarket-backend",
      "image": "${ECR_URI}:latest",
      "portMappings": [{"containerPort": 8080}],
      "environment": [
        {"name": "SPRING_DATASOURCE_URL", "value": "jdbc:postgresql://${RDS_ENDPOINT}:5432/ecomarket_db"},
        {"name": "SPRING_DATASOURCE_USERNAME", "value": "ecomarketadmin"},
        {"name": "SPRING_DATASOURCE_PASSWORD", "value": "EcoMarket2024!Secure"},
        {"name": "JWT_SECRET", "value": "ecomarket-aws-prod-secret-2024-change-this"},
        {"name": "AWS_S3_BUCKET_NAME", "value": "${BUCKET_NAME}"},
        {"name": "AWS_REGION", "value": "us-east-1"},
        {"name": "SPRING_PROFILES_ACTIVE", "value": "prod"}
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/ecomarket-backend",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs",
          "awslogs-create-group": "true"
        }
      }
    }
  ]
}
"@ -replace '\$\{ACCOUNT_ID\}', $ACCOUNT_ID `
   -replace '\$\{ECR_URI\}', $ECR_URI `
   -replace '\$\{RDS_ENDPOINT\}', $RDS_ENDPOINT `
   -replace '\$\{BUCKET_NAME\}', $BUCKET_NAME | Out-File -FilePath task-definition.json -Encoding utf8

# Registrar task definition
aws ecs register-task-definition --cli-input-json file://task-definition.json

Write-Host "✅ Task Definition registrada"
```

#### 2.7 Crear Application Load Balancer

```powershell
# Obtener subnets y VPC por defecto
$DEFAULT_VPC = aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" --query 'Vpcs[0].VpcId' --output text
$SUBNETS = aws ec2 describe-subnets --filters "Name=vpc-id,Values=$DEFAULT_VPC" --query 'Subnets[0:2].SubnetId' --output text

# Crear Security Group para ALB
$ALB_SG = aws ec2 create-security-group `
  --group-name ecomarket-alb-sg `
  --description "Security group for EcoMarket ALB" `
  --vpc-id $DEFAULT_VPC `
  --query 'GroupId' --output text

aws ec2 authorize-security-group-ingress `
  --group-id $ALB_SG `
  --protocol tcp --port 80 --cidr 0.0.0.0/0

# Crear ALB
$ALB_ARN = aws elbv2 create-load-balancer `
  --name ecomarket-alb `
  --subnets $SUBNETS.Split() `
  --security-groups $ALB_SG `
  --query 'LoadBalancers[0].LoadBalancerArn' --output text

# Crear Target Group
$TG_ARN = aws elbv2 create-target-group `
  --name ecomarket-tg `
  --protocol HTTP --port 8080 `
  --vpc-id $DEFAULT_VPC `
  --target-type ip `
  --health-check-path "/ecomarket/api/actuator/health" `
  --query 'TargetGroups[0].TargetGroupArn' --output text

# Crear Listener
aws elbv2 create-listener `
  --load-balancer-arn $ALB_ARN `
  --protocol HTTP --port 80 `
  --default-actions "Type=forward,TargetGroupArn=$TG_ARN"

Write-Host "✅ ALB creado"
```

#### 2.8 Crear ECS Service

```powershell
# Crear Security Group para ECS Tasks
$ECS_SG = aws ec2 create-security-group `
  --group-name ecomarket-ecs-sg `
  --description "Security group for EcoMarket ECS tasks" `
  --vpc-id $DEFAULT_VPC `
  --query 'GroupId' --output text

aws ec2 authorize-security-group-ingress `
  --group-id $ECS_SG `
  --protocol tcp --port 8080 --source-group $ALB_SG

# Crear servicio ECS
aws ecs create-service `
  --cluster ecomarket-cluster `
  --service-name ecomarket-backend-service `
  --task-definition ecomarket-backend-task `
  --desired-count 2 `
  --launch-type FARGATE `
  --network-configuration "awsvpcConfiguration={subnets=[$($SUBNETS.Replace(' ',','))],securityGroups=[$ECS_SG],assignPublicIp=ENABLED}" `
  --load-balancers "targetGroupArn=$TG_ARN,containerName=ecomarket-backend,containerPort=8080"

Write-Host "✅ ECS Service creado"
```

#### 2.9 Obtener URL del Backend

```powershell
$ALB_DNS = aws elbv2 describe-load-balancers `
  --load-balancer-arns $ALB_ARN `
  --query 'LoadBalancers[0].DNSName' --output text

Write-Host "`n=========================================="
Write-Host "🎉 DEPLOYMENT EXITOSO!"
Write-Host "=========================================="
Write-Host "Backend URL: http://$ALB_DNS/ecomarket/api"
Write-Host "RDS Endpoint: $RDS_ENDPOINT"
Write-Host "S3 Bucket: $BUCKET_NAME"
Write-Host "=========================================="
```

---

### Opción B: Elastic Beanstalk (Más Simple)

```powershell
# Instalar EB CLI
pip install awsebcli

# Inicializar
cd backend-v2
eb init -p docker ecomarket-backend --region us-east-1

# Crear environment con RDS
eb create ecomarket-prod `
  --instance-type t3.medium `
  --database `
  --database.engine postgres `
  --database.version 16

# Deploy
eb deploy

# Ver status
eb status

# Ver logs
eb logs
```

---

## 📊 Costos Estimados AWS

| Recurso | Tipo | Costo/Mes |
|---------|------|-----------|
| RDS PostgreSQL | db.t4g.micro Multi-AZ | $30 |
| ECS Fargate | 2 tasks (0.5 vCPU, 1GB) | $25 |
| ALB | Application Load Balancer | $20 |
| S3 | 10GB + 100GB transfer | $5 |
| ECR | 1 imagen | $1 |
| **TOTAL** | | **~$80/mes** |

**Free Tier (primer año):**
- RDS: 750 horas/mes db.t2.micro
- S3: 5GB storage
- ALB: 750 horas/mes

---

## 🔧 Troubleshooting

### Problema: Task no inicia

```powershell
# Ver logs de CloudWatch
aws logs tail /ecs/ecomarket-backend --follow

# Ver eventos del servicio
aws ecs describe-services `
  --cluster ecomarket-cluster `
  --services ecomarket-backend-service `
  --query 'services[0].events[0:5]'
```

### Problema: Health check fallando

```powershell
# Verificar que Spring Actuator está habilitado
# Debe responder en: /ecomarket/api/actuator/health

# Test desde contenedor
docker-compose exec backend wget -O- http://localhost:8080/ecomarket/api/actuator/health
```

### Problema: No conecta a RDS

```powershell
# Verificar Security Groups
# RDS SG debe permitir puerto 5432 desde ECS SG

# Test de conexión desde ECS task
aws ecs execute-command `
  --cluster ecomarket-cluster `
  --task <TASK_ID> `
  --container ecomarket-backend `
  --interactive `
  --command "/bin/sh"

# Dentro del contenedor:
apt-get update && apt-get install -y postgresql-client
psql -h $RDS_ENDPOINT -U ecomarketadmin -d ecomarket_db
```

---

## 🎯 Próximos Pasos

1. ✅ Configurar dominio personalizado con Route 53
2. ✅ Habilitar HTTPS con Certificate Manager
3. ✅ Configurar CloudFront para CDN
4. ✅ Implementar CI/CD con GitHub Actions
5. ✅ Configurar monitoring con CloudWatch
6. ✅ Setup de backups automáticos

---

**🚀 ¡Listo para producción!**
