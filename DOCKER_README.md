# Japan Ramen Directory - Docker Setup

This guide explains how to dockerize and run the Japan Ramen Directory application using Docker and Docker Compose.

## Prerequisites

- Docker (version 20.10 or higher)
- Docker Compose (version 2.0 or higher)

## Project Structure

```
Japan-Ramen-Directory/
├── japanramendirectory/          # Spring Boot Backend
│   ├── Dockerfile
│   └── src/main/resources/
│       └── application-docker.properties
├── japan-ramen-frontend/         # React Frontend
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml
├── .dockerignore
└── DOCKER_README.md
```

## Quick Start

1. **Clone and navigate to the project directory:**
   ```bash
   cd Japan-Ramen-Directory
   ```

2. **Build and start all services:**
   ```bash
   docker-compose up --build
   ```

3. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - MySQL Database: localhost:3306

## Services

### 1. MySQL Database
- **Port:** 3306
- **Container:** ramen-mysql
- **Data Persistence:** Yes (mysql_data volume)

### 2. Spring Boot Backend
- **Port:** 8080
- **Container:** ramen-backend
- **Health Check:** http://localhost:8080/actuator/health

### 3. React Frontend
- **Port:** 3000
- **Container:** ramen-frontend
- **Served by:** Nginx

## Docker Commands

### Build and Run
```bash
# Build and start all services
docker-compose up --build

# Run in detached mode
docker-compose up -d --build

# Start only specific services
docker-compose up mysql backend
```

### Management
```bash
# View running containers
docker-compose ps

# View logs
docker-compose logs
docker-compose logs backend
docker-compose logs frontend

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Rebuild specific service
docker-compose build backend
```

### Individual Container Management
```bash
# Access backend container
docker exec -it ramen-backend /bin/bash

# Access frontend container
docker exec -it ramen-frontend /bin/sh

# Access MySQL container
docker exec -it ramen-mysql mysql -u root -p
```

## Environment Variables

The application uses the following environment variables that can be customized:

### Backend (Spring Boot)
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (set to 'docker')

### MySQL
- `MYSQL_ROOT_PASSWORD`: Root password
- `MYSQL_DATABASE`: Database name
- `MYSQL_USER`: Additional user
- `MYSQL_PASSWORD`: Additional user password

## Customization

### Changing Ports
Edit `docker-compose.yml` to change exposed ports:
```yaml
ports:
  - "8081:8080"  # Change backend port to 8081
  - "3001:80"    # Change frontend port to 3001
```

### Adding Environment Variables
Add environment variables in `docker-compose.yml`:
```yaml
environment:
  - CUSTOM_VAR=value
```

### Database Initialization
Place SQL scripts in `./mysql/init/` directory to run them on first startup.

## Troubleshooting

### Common Issues

1. **Port already in use:**
   ```bash
   # Check what's using the port
   lsof -i :8080
   # Change port in docker-compose.yml
   ```

2. **Database connection issues:**
   ```bash
   # Check MySQL container status
   docker-compose ps mysql
   # View MySQL logs
   docker-compose logs mysql
   ```

3. **Frontend not loading:**
   ```bash
   # Check frontend container
   docker-compose logs frontend
   # Rebuild frontend
   docker-compose build frontend
   ```

4. **Backend startup issues:**
   ```bash
   # Check backend logs
   docker-compose logs backend
   # Ensure MySQL is running first
   docker-compose up mysql
   ```

### Health Checks
All services include health checks. Monitor them with:
```bash
docker-compose ps
```

### Logs
View real-time logs:
```bash
docker-compose logs -f
```

## Production Considerations

1. **Security:**
   - Change default passwords
   - Use environment variables for sensitive data
   - Enable SSL/TLS
   - Configure proper firewall rules

2. **Performance:**
   - Use production-grade MySQL configuration
   - Configure JVM options for Spring Boot
   - Enable nginx caching

3. **Monitoring:**
   - Add monitoring tools (Prometheus, Grafana)
   - Configure log aggregation
   - Set up alerting

4. **Backup:**
   - Regular database backups
   - Volume snapshots
   - Configuration backups

## Development Workflow

For development, you can:

1. **Mount source code for live reloading:**
   ```yaml
   volumes:
     - ./japanramendirectory:/app
   ```

2. **Use development profiles:**
   ```yaml
   environment:
     - SPRING_PROFILES_ACTIVE=dev
   ```

3. **Enable debug mode:**
   ```yaml
   ports:
     - "5005:5005"  # Remote debugging
   ```

## Cleanup

To completely remove all containers, images, and volumes:
```bash
docker-compose down -v --rmi all
docker system prune -a
``` 