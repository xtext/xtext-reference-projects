cd domainmodel/2.44.0/org.eclipse.xtext.example.domainmodel.releng

REPO_DIR="${GITHUB_WORKSPACE:-../../../}"

export PROFILES=
export SETTINGS="-s $REPO_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Dtycho.showEclipseLog=true clean install $EXTRA_ARGS
