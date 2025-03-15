pipeline {
	agent any

    stages {
		stage('pull code') {
			steps {
				checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '3b7daefc-2fa8-4762-850c-5b6deacaea3e', url: 'git@github.com:PengJingzhao/review-system.git']])
            }
        }

        stage('build project'){
			steps{
				sh 'mvn clean package'
            }
        }

        stage('deploy'){
			steps{
				echo 'deploy'
            }
        }
    }
}
