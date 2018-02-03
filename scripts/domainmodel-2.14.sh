cd domainmodel/2.14.0/org.eclipse.xtext.example.domainmodel.releng

export PROFILES=-Pxtext_snapshots
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Dtycho.showEclipseLog=true -Dtargetplatform.classifier=domainmodel-$TP clean install
