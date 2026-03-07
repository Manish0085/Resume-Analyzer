pipeline {
    agent any

    options {
        timestamps()
    }

    environment {
        PROJECT_NAME = "Resume Analyzer"
    }

    stages {

        stage('Clean Workspace') {
            steps {
                echo "Cleaning workspace..."
                deleteDir()
            }
        }

        stage('Clone Repository') {
            steps {
                echo "Cloning repository..."
                git branch: 'main', url: 'https://github.com/Manish0085/Resume-Analyzer.git'
            }
        }

        stage('Load ENV Files') {
            steps {
                withCredentials([
                    file(credentialsId: 'backend-env-file', variable: 'BACKEND_ENV'),
                    file(credentialsId: 'frontend-env-file', variable: 'FRONTEND_ENV')
                ]) {
                    sh '''
                    echo "Workspace: $WORKSPACE"

                    cp "$BACKEND_ENV" backend/.env
                    cp "$FRONTEND_ENV" frontend/.env

                    echo "ENV files copied successfully"
                    '''
                }
            }
        }

        stage('Verify Docker Installation') {
            steps {
                sh '''
                docker --version
                docker-compose --version
                '''
            }
        }

        stage('Stop Old Containers') {
            steps {
                sh '''
                echo "Stopping old containers..."
                docker-compose down || true
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                echo "Building Docker images..."
                docker-compose build
                '''
            }
        }

        stage('Start Containers') {
            steps {
                sh '''
                echo "Starting containers..."
                docker-compose up -d
                '''
            }
        }

        stage('Verify Running Containers') {
            steps {
                sh '''
                echo "Running containers:"
                docker ps
                '''
            }
        }

        stage('Application Health Check') {
            steps {
                sh '''
                echo "Checking application health..."
                sleep 10
                curl -f http://localhost || exit 1
                '''
            }
        }

    }

    post {

        success {
            mail to: 'your-email@gmail.com',
            subject: "✅ Deployment Successful - ${env.JOB_NAME}",
            body: """
🚀 Deployment Successful

Project: ${env.JOB_NAME}
Build Number: #${env.BUILD_NUMBER}

Build Logs:
${env.BUILD_URL}

Server: ${env.NODE_NAME}
"""
        }

        failure {
            mail to: 'your-email@gmail.com',
            subject: "❌ Deployment Failed - ${env.JOB_NAME}",
            body: """
🚨 Deployment Failed

Project: ${env.JOB_NAME}
Build Number: #${env.BUILD_NUMBER}

Check Jenkins logs:
${env.BUILD_URL}

Server: ${env.NODE_NAME}
"""
        }

        always {
            echo "Pipeline execution finished."
        }
    }
}