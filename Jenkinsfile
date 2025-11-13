pipeline {
  agent any

  environment {
    DOCKERHUB_USER = 'oohabanoth123'
    DOCKER_IMAGE = "${DOCKERHUB_USER}/myapp"
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
        sh 'mvn clean package -DskipTests'
      }
    }

   stage('Build Docker Image & Push') {
    steps {
        // Use the credentials you just created
        withCredentials([usernamePassword(credentialsId: 'dockerhub-token', 
                                          usernameVariable: 'DOCKER_USER', 
                                          passwordVariable: 'DOCKER_PASS')]) {
            // Build Docker image
            sh 'docker build -t my-dockerhub-username/devops-project:latest .'
            
            // Login to Docker Hub
            sh 'docker login -u $DOCKER_USER -p $DOCKER_PASS'
            
            // Push image to Docker Hub
            sh 'docker push my-dockerhub-username/devops-project:latest'
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
            kubectl -n devops rollout status deployment/myapp-deployment
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

