cd greetings-maven/2.12.0

export PROFILES=
export SETTINGS=
export SET_TYCHO_VERSION=
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn $SET_TYCHO_VERSION $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES clean install
