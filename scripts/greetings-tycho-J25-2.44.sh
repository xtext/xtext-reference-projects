cd greetings-tycho/2.44.0-J25

REPO_DIR="${GITHUB_WORKSPACE:-../../}"

export PROFILES=
export SETTINGS="-s $REPO_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Djava-25 -Dtycho.showEclipseLog=true clean install $EXTRA_ARGS
