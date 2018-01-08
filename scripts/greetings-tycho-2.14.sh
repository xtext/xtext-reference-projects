cd greetings-tycho/2.14.0
mvn -Dtycho-version=1.1.0-SNAPSHOT -s $TRAVIS_BUILD_DIR/settings.xml -PuseJenkinsSnapshots clean install
