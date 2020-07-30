pipeline {
    agent {
        docker 'gradle:latest'
    }
    stages {
        stage('build') {
            steps {
                gitlabCommitStatus('build') {
                    withGradle {
                        sh './gradlew distzip'
                    }
                }
            }
        }
    }
    post {
        always {
            script {
                def msg = "**Status:** " + currentBuild.currentResult.toLowerCase() + "\n"
                msg += "**Changes:** \n"
                if (!currentBuild.changeSets.isEmpty()) {
                    currentBuild.changeSets.first().getLogs().each {
                        msg += "- `" + it.getCommitId().substring(0, 8) + "` *" + it.getComment().substring(0, it.getComment().length()-1) + "*\n"
                    }
                } else {
                    msg += "no changes for this run\n"
                }
                if (msg.length() > 1024) msg.take(msg.length() - 1024)
                withCredentials([string(credentialsId: 'discord-webhook', variable: 'discordWebhook')]) {
                    discordSend thumbnail: "http://wnuke.dev/radiation-symbol.png", successful: currentBuild.resultIsBetterOrEqualTo('SUCCESS'), description: "${msg}", link: env.BUILD_URL, title: "mc-http-api #${BUILD_NUMBER}", webhookURL: "${discordWebhook}"
                }
            }
        }
    }
}