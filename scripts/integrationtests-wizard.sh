cd "$(pwd)/integrationtests"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../}"
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn $DISABLE_DOWNLOAD_PROGRESS $SETTINGS -Dtycho.showEclipseLog=true clean install -Dtest=XtextNewProjectWizardTest
