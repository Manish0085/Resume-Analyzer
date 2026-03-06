pipeline {
    agent any

    stages {

        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }

        stage('Clone Repository') {
            steps {
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
                    cd "$WORKSPACE"

                    echo "Project Structure:"
                    ls -la

                    cp "$BACKEND_ENV" backend/.env
                    cp "$FRONTEND_ENV" frontend/.env

                    echo "ENV files copied successfully"
                    '''
                }
            }
        }

        stage('Verify Docker Installation') {
            steps {
                sh 'docker --version'
                sh 'docker-compose --version'
            }
        }

        stage('Stop Old Containers') {
            steps {
                sh '''
                cd "$WORKSPACE"
                docker-compose down || true
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                cd "$WORKSPACE"
                docker-compose build
                '''
            }
        }

        stage('Start Containers') {
            steps {
                sh '''
                cd "$WORKSPACE"
                docker-compose up -d
                '''
            }
        }

        stage('Verify Running Containers') {
            steps {
                sh 'docker ps'
            }
        }

    }

    post {
        success {
            echo "Deployment Successful 🚀"
        }
        failure {
            echo "Pipeline Failed ❌"
        }
    }
}