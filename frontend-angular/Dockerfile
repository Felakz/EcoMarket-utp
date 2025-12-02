# Multi-stage build para Angular

# Stage 1: Build
FROM node:20-alpine AS build
WORKDIR /app

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar dependencias
RUN npm ci --silent

# Copiar código fuente
COPY . .

# Build de producción
RUN npm run build

# Stage 2: Runtime con Nginx
FROM nginx:1.25-alpine

# Copiar configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Copiar archivos compilados desde stage de build
COPY --from=build /app/dist/frontend-angular/browser /usr/share/nginx/html

# Exponer puerto
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/health || exit 1

# Nginx se inicia automáticamente
CMD ["nginx", "-g", "daemon off;"]
