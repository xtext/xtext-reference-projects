if ["$TRAVIS_BUILD_DIR" eq ""]
then
  export TRAVIS_BUILD_DIR=$(pwd)
fi

cd greetings-maven/2.23.0-J11

export PROFILES=-Ptycho_snapshots
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES clean install
