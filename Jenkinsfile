pipeline {
	agent any

    // 全局环境变量
    environment {
		HARBOR_URL = "192.168.59.140:9000"           // Harbor 镜像仓库地址
        HARBOR_PROJECT = "review-system"           // Harbor 项目名称
        DOCKER_CREDENTIALS_ID = "3ff1f9a0-bf1e-41fa-9b0d-3db5f05c920d"
        TAG = "latest"
    }

    stages {
		// 拉取代码
        stage('Pull Code') {
			steps {
				checkout scmGit(
                    branches: [[name: '*/master']],
                    extensions: [],
                    userRemoteConfigs: [[
                        credentialsId: '3b7daefc-2fa8-4762-850c-5b6deacaea3e',
                        url: 'git@github.com:PengJingzhao/review-system.git'
                    ]]
                )
            }
        }

        // 编译公共模块
        stage('Compile and Build Commons') {
			steps {
			    script{
			        def commonsPath = "./commons/pom.xml "

                    sh "mvn -f ${commonsPath} clean install" // 针对公共模块（common）进行编译和打包
			    }
            }
        }

        // 构建 Docker 镜像
        stage('Build And Deploy') {
			steps {
				script {
					echo "Building Docker images for all services..."

                    // 定义服务模块
                    def services = [ 'user-service', 'review-service']
                    def ports = [21001,21002]

                    def index = 0;


                    // 遍历服务模块，构建每个服务的 Docker 镜像
                    for (service in services) {

						def imageName = "${service}:${TAG}"
						def tagName = "${HARBOR_URL}/${HARBOR_PROJECT}/${service}:${TAG}"
						def p = ports[index]
						index = index + 1

                        // 根据dockerfile打包成镜像
						sh """
						cd ${service}

						mvn clean package dockerfile:build

						"""

                        // 将镜像上传到harbor
						withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}", passwordVariable: 'password', usernameVariable: 'username')]) {
							// some block
							sh "docker login -u ${username} -p ${password} ${HARBOR_URL}"
						}

						sh """
						docker tag ${imageName} ${tagName}

						docker push ${tagName}

						docker rmi ${imageName}

						docker rmi ${tagName}
						"""

						sh "echo /app/jenkins/jenkins_shell/deploy.sh ${HARBOR_URL} ${HARBOR_PROJECT} ${service} ${TAG} ${p}"

						// 通过ssh远程执行生产服务器上的部署脚本
						sshPublisher(
							publishers:
								[
									sshPublisherDesc(
										configName: 'prod_server', transfers: [sshTransfer(cleanRemote: false, excludes: '',
											execCommand: "/app/jenkins/jenkins_shell/deploy.sh ${HARBOR_URL} ${HARBOR_PROJECT} ${service} ${TAG} ${p}", execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false
										)
								]
						)

                    }
                }
            }
        }
    }

    // 后置操作
    post {
		success {
			echo "Pipeline completed successfully!"
        }
        failure {
			echo "Pipeline failed. Please check the logs for details."
        }
    }
}