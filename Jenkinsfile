pipeline {
    agent any

    stages {

        stage('Clean workspace'){
            steps {
                deleteDir()
            }
        }
        stage('Clone Repository') {
            steps {
                git 'https://github.com/Manish0085/Resume-Analyzer.git'
            }
        }


        stage('Load ENV Files') {
            steps {
                withCredentials([
                    file(credentialsId: 'backend-env-file', variable: 'BACKEND_ENV'),
                    file(credentialsId: 'frontend-env-file', variable: 'FRONTEND_ENV')
                ]) {
                    sh '''
                    cp $BACKEND_ENV backend/.env
                    cp $FRONTEND_ENV frontend/.env
                    '''
                }
            }
        }

        stage('Verify Docker') {
            steps{
                sh 'docker --version'
                sh 'docker-compose --version'
            }
        }

        stage('Stop Old Containers') {
            steps {
                sh 'docker-compose down || true'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker-compose build'
            }
        }

        stage('Start Containers') {
            steps {
                sh 'docker-compose up -d'
            }
        }
    }
}