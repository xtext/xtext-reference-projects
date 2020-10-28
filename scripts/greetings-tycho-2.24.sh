cd greetings-tycho/2.24.0

TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../../}"

export PROFILES=-Pxtext_snapshots
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Dtycho.showEclipseLog=true clean install
