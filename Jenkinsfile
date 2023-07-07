#!/usr/bin/env groovy
//def BRANCH_NAME = git_branch.tokenize('/')[-2]+"/"+git_branch.tokenize('/')[-1]
def BRANCH_NAME = git_branch.replace("origin/","")
pipeline{
	agent {
		label 'jenkins-slave'
	}
    options {
        disableConcurrentBuilds()
        skipDefaultCheckout()
        timeout(time: 1, unit: 'HOURS')
        timestamps()
    }
	environment{
		GIT_URL = "ssh://root@gitea.startable.cn:6622/LessCode/lesscode-gateway.git"
		CREDENTIALS_ID = "c48eb940-6b7c-4fc6-b73c-13250fdfd4ef"
	}
	stages {
        stage('print vars') {
			steps {
				echo "${BRANCH_NAME}"
				echo "${env.env}"
				}
        }
		stage('checkout') {
			steps {
				git branch: "${BRANCH_NAME}", credentialsId: "${CREDENTIALS_ID}", url: "${GIT_URL}"
			}
		}
		stage('package') {
			steps {
				sh '''
				mvn clean package
				'''
			}
		}
		stage('docker build') {
			steps {
				sh '''
                echo "build docker image for lesscode-gateway"
				if [ ${env}"bjx" = "devbjx" -o ${env}"bjx" = "testbjx" -o ${env}"bjx" = "crmbjx" -o ${env}"bjx" = "crm2bjx" -o ${env}"bjx" = "k8s-testbjx" -o ${env}"bjx" = "fusebjx" -o ${env}"bjx" = "graybjx" ];then
				docker build -t registry-vpc.cn-shanghai.aliyuncs.com/polaris-team/lesscode-gateway:v1.${BUILD_ID} .
				fi				
                '''
			}
		}
		stage('docker push') {
			steps {
				sh '''
				if [ ${env}"bjx" = "devbjx" -o ${env}"bjx" = "testbjx" -o ${env}"bjx" = "crmbjx" -o ${env}"bjx" = "crm2bjx" -o ${env}"bjx" = "k8s-testbjx" -o ${env}"bjx" = "fusebjx" -o ${env}"bjx" = "graybjx" ];then
                docker push registry-vpc.cn-shanghai.aliyuncs.com/polaris-team/lesscode-gateway:v1.${BUILD_ID}			
				fi
                '''
			}
		}
		stage('deploy') {
			steps {
				sh '''
				if [ ${env}bjx = "devbjx" ];then
				ssh media@172.19.84.54 -p 19222 "cd /data/app/lesscode-gateway && ./upgrade.sh v1.${BUILD_ID}"
				elif [ ${env}bjx = "k8s-testbjx" ];then
				ssh media@172.19.84.54 -p 11222 "kubectl -ntest set image deployment/lesscode-gateway lesscode-gateway=registry.cn-shanghai.aliyuncs.com/polaris-team/lesscode-gateway:v1.${BUILD_ID} --record"
				elif [ ${env}bjx = "testbjx" ];then
				ssh media@172.19.132.103 "cd /data/app/lesscode-gateway && ./upgrade.sh v1.${BUILD_ID}"
				elif [ ${env}bjx = "fusebjx" ];then
				kubectl -nfuse set image deployment/lesscode-gateway lesscode-gateway=registry.cn-shanghai.aliyuncs.com/polaris-team/lesscode-gateway:v1.${BUILD_ID} --record
				elif [ ${env}bjx = "graybjx" ];then
				kubectl -ngray set image deployment/lesscode-gateway lesscode-gateway=registry.cn-shanghai.aliyuncs.com/polaris-team/lesscode-gateway:v1.${BUILD_ID} --record
				elif [ ${env}bjx = "crmbjx" ];then
				ssh media@172.19.84.54 -p 17222 "cd /data/app/lesscode-gateway && ./upgrade.sh v1.${BUILD_ID}"
				elif [ ${env}bjx = "crm2bjx" ];then
				ssh media@172.19.71.36 "cd /data/app/lesscode/lesscode-gateway && ./upgrade.sh v1.${BUILD_ID}"
				elif [ ${env}bjx = "test2bjx" ];then
				ssh media@172.19.132.102 "cd /data/app/lesscode/lesscode-gateway && ./upgrade.sh v1.${BUILD_ID}"
				elif [ ${env}bjx = "prodbjx" ];then
				echo "cd /data/app/lesscode-gateway && ./upgrade.sh v1.${BUILD_ID}"
				#ssh media@18.162.213.17 "./bigdata-deploy.sh 13.229.162.142 bigdata channel-data-backend v1.${BUILD_ID}"
				else
				echo "skip deploy"
				fi
                '''
			}
		}
	}
}