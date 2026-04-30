pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        BACK_HOST    = '114.132.77.204'
        BACK_USER    = 'deploy'
        APP_DIR      = '/opt/campus-platform'
        SERVICE_NAME = 'campus-platform'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -pl campus-server -am clean package -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                sshagent(credentials: ['tx-deploy-ssh']) {
                    sh '''
                        JAR_FILE=$(ls campus-server/target/*.jar | head -n 1)
                        scp -o StrictHostKeyChecking=no "$JAR_FILE" ${BACK_USER}@${BACK_HOST}:${APP_DIR}/app/app.jar.new
                        ssh -o StrictHostKeyChecking=no ${BACK_USER}@${BACK_HOST} "
                            mv ${APP_DIR}/app/app.jar.new ${APP_DIR}/app/app.jar &&
                            sudo systemctl restart ${SERVICE_NAME} &&
                            sudo systemctl --no-pager status ${SERVICE_NAME}
                        "
                    '''
                }
            }
        }
    }
}
