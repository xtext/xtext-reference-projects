pipeline {
  agent none

  triggers {
    // only on master branch / 30 minutes before nightly sign-and-deploy
    cron(env.BRANCH_NAME == 'master' ? 'H 18 * * *' : '')
    githubPush()
  }

  environment {
    GRADLE_USER_HOME = "$WORKSPACE/.gradle" // workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=564559
  }

  options {
    buildDiscarder(logRotator(numToKeepStr:'15'))
    disableConcurrentBuilds()
    timeout(time: 90, unit: 'MINUTES')
  }

  // Build stages
  stages {
    stage('do the tests') {
      parallel {
        stage('Build scripts/integrationtests-wizard.sh on temurin-jdk11-latest') {
          agent {
            kubernetes {
              inheritFrom 'centos-8'
            }
          }
          tools {
            maven "apache-maven-3.8.6"
            jdk "temurin-jdk11-latest"
          }
          environment {
            EXTRA_ARGS = "-Dmaven.repo.local=.m2 -Dtycho.localArtifacts=ignore"
            MAVEN_OPTS="-Xmx512m"
          }
          steps {
            checkout scm
            wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
              sh 'scripts/integrationtests-wizard.sh'
            }
          }
        }
        stage('Build scripts/integrationtests-xtend-examples.sh on temurin-jdk11-latest') {
          agent {
            kubernetes {
              inheritFrom 'centos-8'
            }
          }
          tools {
            maven "apache-maven-3.8.6"
            jdk "temurin-jdk11-latest"
          }
          environment {
            EXTRA_ARGS = "-Dmaven.repo.local=.m2 -Dtycho.localArtifacts=ignore"
            MAVEN_OPTS="-Xmx512m"
          }
          steps {
            checkout scm
            wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
              sh 'scripts/integrationtests-xtend-examples.sh'
            }
          }
        }
        stage('Build scripts/integrationtests-xtext-examples.sh on temurin-jdk11-latest') {
          agent {
            kubernetes {
              inheritFrom 'centos-8'
            }
          }
          tools {
            maven "apache-maven-3.8.6"
            jdk "temurin-jdk11-latest"
          }
          environment {
            EXTRA_ARGS = "-Dmaven.repo.local=.m2 -Dtycho.localArtifacts=ignore"
            MAVEN_OPTS="-Xmx512m"
          }
          steps {
            checkout scm
            wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
              sh 'scripts/integrationtests-xtext-examples.sh'
            }
          }
        }

      }
    }
    
  }

  post {
    cleanup {
      script {
        def curResult = currentBuild.currentResult
        def lastResult = 'NEW'
        if (currentBuild.previousBuild != null) {
          lastResult = currentBuild.previousBuild.result
        }

        if (curResult != 'SUCCESS' || lastResult != 'SUCCESS') {
          def color = ''
          switch (curResult) {
            case 'SUCCESS':
              color = '#00FF00'
              break
            case 'UNSTABLE':
              color = '#FFFF00'
              break
            case 'FAILURE':
              color = '#FF0000'
              break
            default: // e.g. ABORTED
              color = '#666666'
          }

          slackSend (
            message: "${lastResult} => ${curResult}: <${env.BUILD_URL}|${env.JOB_NAME}#${env.BUILD_NUMBER}>",
            botUser: true,
            channel: 'xtext-builds',
            color: "${color}"
          )
        }
      }
    }
  }
}

/** return the Java version as Integer (8, 11, ...) */
def javaVersion() {
  return Integer.parseInt(params.JDK_VERSION.replaceAll(".*-jdk(\\d+).*", "\$1"))
}

def runScript(javaVersion, theScript) {
  stage(script + " on " + javaVersion) {
    agent {
      kubernetes {
        inheritFrom 'centos-8'
      }
    }
    tools {
      maven "apache-maven-3.8.6"
      jdk "${javaVersion}"
    }
    environment {
      EXTRA_ARGS = "-Dmaven.repo.local=.m2 -Dtycho.localArtifacts=ignore"
    }
    steps {
      checkout scm
      wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
        // TODO the following scripts should run independent and fail at the end only
        sh "${theScript}"
      }
    }
  }
}

