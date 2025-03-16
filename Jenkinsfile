pipeline {
	agent any

    // 全局环境变量
    environment {
		HARBOR_URL = "192.168.59.140:9000"           // Harbor 镜像仓库地址
        HARBOR_PROJECT = "review-system"           // Harbor 项目名称
        DOCKER_CREDENTIALS_ID_USR = "PengJingzhao"    // Jenkins 中配置的 Docker 登录凭据 ID
        DOCKER_CREDENTIALS_ID_PSW = "12345678Pj"
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
                sh "" // 针对公共模块（common）进行编译和打包
            }
        }

        // 构建服务代码
        stage('Build Services') {
			steps {
                sh "" // 编译所有服务模块，跳过测试
            }
        }

        // 构建 Docker 镜像
        stage('Build Service Modules') {
			steps {
				sh "mvn clean package dockerfile:build"
				//script {
				//	echo "Building Docker images for all services..."
				//
                //    // 定义服务模块
                //    def services = [ 'user-service', 'review-service']
				//
                //    // 遍历服务模块，构建每个服务的 Docker 镜像
                //    for (service in services) {
				//		sh """
                //            docker build -t ${HARBOR_URL}/${HARBOR_PROJECT}/${service}:latest -f ${service}/Dockerfile ${service}
                //        """
                //    }
                //}
            }
        }

        // 推送 Docker 镜像到 Harbor
        stage('Push Docker Images') {
			steps {
				script {
                    // 登录 Harbor 镜像仓库
                    sh """
                        docker login ${HARBOR_URL} -u ${DOCKER_CREDENTIALS_ID_USR} -p ${DOCKER_CREDENTIALS_ID_PSW}
                    """

                    // 定义服务模块
                    def services = [ 'user-service', 'review-service']

                    // 推送每个服务的镜像
                    for (service in services) {
						sh """
                            docker push ${HARBOR_URL}/${HARBOR_PROJECT}/${service}:latest
                        """
                    }
                }
            }
        }

        // 部署服务
stage('Deploy Services') {
			steps {
				script {
					echo "Deploying services by pulling images from Harbor and starting containers..."

                    // 登录到 Harbor 镜像仓库
                    sh """
                        docker login ${HARBOR_URL} -u ${DOCKER_CREDENTIALS_ID_USR} -p ${DOCKER_CREDENTIALS_ID_PSW}
                    """

                    // 定义服务模块及端口映射
                    def services = [
                        'user-service': '21001:20880',
                        'review-service': '21002:20881'
                    ]

                    // 停止并移除已有容器
                    for (service in services.keySet()) {
						sh """
                            docker stop ${service} || true
                            docker rm ${service} || true
                        """
                    }

                    // 拉取镜像并启动容器
                    for (service in services.keySet()) {
						sh """
                            docker pull ${HARBOR_URL}/${HARBOR_PROJECT}/${service}:latest
                            docker run -d --name ${service} \\
                                -p ${services[service]} \\
                                ${HARBOR_URL}/${HARBOR_PROJECT}/${service}:latest
                        """
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