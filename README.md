## Deployment Workflow

```text
IntelliJ IDEA / VS Code
        │
        ▼
Build Project (Maven)
        │
        ▼
Build Docker Images (Windows)
        │
        ▼
Push Images to Docker Hub
        │
        ▼
AWS EC2 (Amazon Linux)
        │
        ▼
Pull Images
        │
        ▼
Run Containers
        │
        ▼
Application Running
```

## Build and Push (Windows PowerShell)

```powershell
cd C:\Users\Bereket\SWEProject

mvn -f backend\pom.xml clean package

docker build -t fekadie/sweproject-backend:latest .\backend
docker build -t fekadie/sweproject-frontend:latest .\frontend

docker push fekadie/sweproject-backend:latest
docker push fekadie/sweproject-frontend:latest
```

## Deploy on AWS EC2 (Amazon Linux)

```bash
cd ~/sweproject

sudo docker-compose pull

sudo docker-compose up -d

sudo docker ps
```