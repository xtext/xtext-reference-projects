cd "$(pwd)/integrationtests"
ls -la /opt/hostedtoolcache/
ls -la /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk
cat ~/.m2/settings.xml
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../}"
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 
./mvnw -B $DISABLE_DOWNLOAD_PROGRESS $SETTINGS -Dtycho.showEclipseLog=true clean install -Dtest=XtextExamplesTest $EXTRA_ARGS
