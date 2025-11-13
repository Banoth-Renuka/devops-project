pipeline {
    agent any

    tools {
        maven "maven3"
        jdk "java17"
    }

    environment {
        DOCKERHUB_USER = 'oohabanoth123'
        DOCKER_IMAGE = "${DOCKERHUB_USER}/myapp:latest"
        GIT_REPO = 'https://github.com/Banoth-Renuka/devops-project.git'
        DOCKER_CRED_ID = 'dockerhub-token'
        KUBECONFIG_CRED = 'kubeconfig-cred-id'  // Make sure this matches your Jenkins kubeconfig credential
    }

    stages {
        stage('Cleanup Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout') {
            steps {
                git branch: 'main', url: "${env.GIT_REPO}", credentialsId: 'github_cred'
            }
        }

        stage('Build Maven Project') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Trivy Scan') {
            steps {
                sh """
                    # Install Trivy if not already installed
                    command -v trivy >/dev/null 2>&1 || \
                    curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh

                    # Scan Docker image for HIGH/CRITICAL vulnerabilities
                    trivy image --exit-code 1 --severity HIGH,CRITICAL ${DOCKER_IMAGE}
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: env.DOCKER_CRED_ID,
                                                  usernameVariable: 'DOCKER_USER',
                                                  passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push ${DOCKER_IMAGE}
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: env.KUBECONFIG_CRED, variable: 'KUBECONFIG_FILE')]) {
                    sh """
                        export KUBECONFIG=${KUBECONFIG_FILE}
                        kubectl apply -f k8s/namespace.yaml || true
                        kubectl apply -f k8s/deployment.yaml
                        kubectl apply -f k8s/service.yaml
                        kubectl -n devops rollout status deployment/myapp-deployment
                    """
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
            echo "✅ Pipeline completed successfully: Build → Scan → Docker → Kubernetes → Smoke Test"
        }
        failure {
            echo "❌ Pipeline failed. Check logs for errors."
        }
    }
}


