pipeline {
  agent any

  environment {
    DOCKERHUB_USER = 'oohabanoth123'
    DOCKER_IMAGE = "${DOCKERHUB_USER}/devops-app"
    GIT_REPO = 'https://github.com/Banoth-Renuka/devops-project.git'
    DOCKER_CRED_ID = 'dockerhub-token'      // Jenkins credential id (DockerHub username/password or token)
    KUBECONFIG_CRED = 'kubeconfig-cred-id'  // Jenkins credential id (Kubeconfig file)
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: "${env.GIT_REPO}", credentialsId: 'github_cred'
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -D -DskipTests clean package'
      }
    }

    stage('Build Docker Image & Push') {
      steps {
        withCredentials([usernamePassword(credentialsId: env.DOCKER_CRED_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh '''
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            TAG=$(git rev-parse --short HEAD)
            docker build -t ${DOCKER_IMAGE}:$TAG .
            docker push ${DOCKER_IMAGE}:$TAG
            docker tag ${DOCKER_IMAGE}:$TAG ${DOCKER_IMAGE}:latest
            docker push ${DOCKER_IMAGE}:latest
          '''
          script {
            env.IMAGE_TAG = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
          }
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        withCredentials([file(credentialsId: env.KUBECONFIG_CRED, variable: 'KUBECONFIG_FILE')]) {
          sh '''
            export KUBECONFIG=${KUBECONFIG_FILE}
            kubectl apply -f k8s/namespace.yaml || true
            kubectl apply -f k8s/deployment.yaml
            kubectl apply -f k8s/service.yaml
            kubectl -n devops rollout status deployment/devops-app-deployment
          '''
        }
      }
    }

    stage('Smoke Test') {
      steps {
        sh 'sleep 5; curl -fsS http://localhost:8080/ || true'
      }
    }
  }

  post {
    success {
      echo "✅ Build and deployment successful!"
    }
    failure {
      echo "❌ Build failed. Check logs."
    }
  }
}

