cd domainmodel/2.14.0/org.eclipse.xtext.example.domainmodel.releng

export PROFILES=-Ptycho_snapshots,xtext_snapshots
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export SET_TYCHO_VERSION=-Dtycho-version=1.1.0-SNAPSHOT
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn $SET_TYCHO_VERSION $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Dtycho.showEclipseLog=true clean install
