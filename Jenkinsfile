pipeline {
	agent any

    // 全局环境变量
    environment {
		HARBOR_URL = "10.21.32.95:9000"           // Harbor 镜像仓库地址
        HARBOR_PROJECT = "review-system"           // Harbor 项目名称
        DOCKER_CREDENTIALS_ID = "a890512a-7d3a-4693-b40f-a37b40df0b33"
        TAG = "latest"
    }

    stages {
		// 拉取代码
        stage('Pull Code') {
			steps {
checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '8c220f6e-bbf4-449b-b98a-5841d37b334c', url: 'git@github.com:PengJingzhao/review-system.git']])
            }
        }

        // 将父工程的pom.xml添加到本地仓库
        stage('Install Parent Pom'){
            steps{
                script{
                    sh " mvn clean install -N"
                }
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

                // 编译公共模块
                stage('Compile and Build Common') {
        			steps {
        			    script{
        			        def commonsPath = "./common/pom.xml "

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
                    def services = [ 'file-service','media-service','count-service','user-service', 'shop-service','content-service']
                    def ports = [15012,15010,15006,15001,15002,15011]
                    def ports1 = [50052,50055,50054,50051,50056,50053]

                    def index = 0;


                    // 遍历服务模块，构建每个服务的 Docker 镜像
                    for (service in services) {

						def imageName = "${service}:${TAG}"
						def tagName = "${HARBOR_URL}/${HARBOR_PROJECT}/${service}:${TAG}"
						def p = ports[index]
						def p1 = ports1[index]
						index = index + 1

                        // 根据dockerfile打包成镜像
						sh """
						cd ${service}

						mvn clean package dockerfile:build -Dmaven.test.skip=true

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

// 						sh "echo /app/review-system/deploy.sh ${HARBOR_URL} ${HARBOR_PROJECT} ${service} ${TAG} ${p} ${p1}"
//
// // 						// 通过ssh远程执行生产服务器上的部署脚本
// 						sshPublisher(
// 							publishers:
// 								[
// 									sshPublisherDesc(
// 										configName: 'Jenkins-prod', transfers: [sshTransfer(cleanRemote: false, excludes: '',
// 											execCommand: "/app/review-system/deploy.sh ${HARBOR_URL} ${HARBOR_PROJECT} ${service} ${TAG} ${p} ${p1}", execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false
// 										)
// 								]
// 						)

                    }
                }
            }
        }

        stage('Deploy Services'){
            steps{
                script{
                    // 定义服务模块
                                        def services = [ 'file-service','media-service','count-service','user-service', 'shop-service','content-service']
                                        def ports = [15012,15010,15006,15001,15002,15011]
                                        def ports1 = [50052,50055,50054,50051,50056,50053]

                                        def index = 0;


                                        // 遍历服务模块，构建每个服务的 Docker 镜像
                                        for (service in services) {

                    						def imageName = "${service}:${TAG}"
                    						def tagName = "${HARBOR_URL}/${HARBOR_PROJECT}/${service}:${TAG}"
                    						def p = ports[index]
                    						def p1 = ports1[index]
                    						index = index + 1



                    						sh "echo /app/review-system/deploy.sh ${HARBOR_URL} ${HARBOR_PROJECT} ${service} ${TAG} ${p} ${p1}"

                    // 						// 通过ssh远程执行生产服务器上的部署脚本
                    						sshPublisher(
                    							publishers:
                    								[
                    									sshPublisherDesc(
                    										configName: 'Jenkins-prod', transfers: [sshTransfer(cleanRemote: false, excludes: '',
                    											execCommand: "/app/review-system/deploy.sh ${HARBOR_URL} ${HARBOR_PROJECT} ${service} ${TAG} ${p} ${p1}", execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false
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