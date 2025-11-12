pipeline {
  agent any

  environment {
    DOCKERHUB_USER = 'oohabanoth123'
    DOCKER_IMAGE = "${DOCKERHUB_USER}/devops-app"
    GIT_REPO = 'https://github.com/Banoth-Renuka/devops-project.git'
    DOCKER_CRED_ID = 'dockerhub-token'     // Jenkins credential id (username/password or token)
    KUBECONFIG_CRED = 'kubeconfig-cred-id' // Jenkins credential id (kubeconfig file)
  }

  stages {
    stage('Checkout') {
      steps {
        git url: env.GIT_REPO, branch: 'main'
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
    }

    stage('Build Image') {
      steps {
        withCredentials([usernamePassword(credentialsId: env.DOCKER_CRED_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh '''
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            TAG=${GIT_COMMIT}
            docker build -t ${DOCKER_IMAGE}:$TAG .
            docker push ${DOCKER_IMAGE}:$TAG
            docker tag ${DOCKER_IMAGE}:$TAG ${DOCKER_IMAGE}:latest
            docker push ${DOCKER_IMAGE}:latest
          '''
          script { env.IMAGE_TAG = sh(script: 'echo $GIT_COMMIT', returnStdout: true).trim() }
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
    failure {
      echo "Build failed. Check logs."
    }
  }
}
