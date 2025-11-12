# devops-app - End-to-End CI/CD Demo

This repository is a full example pipeline:
- Build: Maven (Spring Boot)
- Container: Docker (push to DockerHub: oohabanoth123/devops-app)
- CI/CD: Jenkins (Jenkinsfile included)
- Deploy: Kubernetes (manifests in k8s/)
- Monitoring: Prometheus & Grafana (monitoring/)

## Quick start (local)
1. Build jar:
   mvn -B clean package
2. Build Docker image:
   docker build -t oohabanoth123/devops-app:latest .
3. Push to DockerHub:
   docker login
   docker push oohabanoth123/devops-app:latest
4. Deploy to Kubernetes:
   kubectl apply -f k8s/namespace.yaml
   kubectl apply -f k8s/deployment.yaml
   kubectl apply -f k8s/service.yaml

## Jenkins setup notes
- Create Jenkins pipeline pointing to: https://github.com/Banoth-Renuka/devops-project.git
- Add credentials in Jenkins:
  - ID: dockerhub-token (Username/password or token)
  - ID: kubeconfig-cred-id (File credential containing kubeconfig)

## Files to edit before use
- Jenkinsfile: set credential IDs if you use different IDs
- k8s/ingress.yaml: set host to your domain
- monitoring/*.yaml: adjust namespace if Prometheus Operator installed differently

