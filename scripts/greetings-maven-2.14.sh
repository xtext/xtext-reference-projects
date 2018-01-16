cd greetings-maven/2.14.0

export PROFILES=-Ptycho_snapshots,xtext_snapshots
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES clean install
