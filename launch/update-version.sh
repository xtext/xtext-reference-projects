#!/bin/bash

# Script to update/create version reference projects
# Usage: ./update-version.sh [version] (default: 2.43)

set -e

VERSION=${1:-2.43}
MAJOR_VERSION=$(echo "$VERSION" | cut -d. -f1)
MINOR_VERSION=$(echo "$VERSION" | cut -d. -f2)
PREV_MINOR_VERSION=$((MINOR_VERSION - 1))

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

echo "Updating to version $VERSION..."

# Update launch files
echo "Updating launch files..."
for file in "$ROOT_DIR/launch"/refproject-greetings-*-j17.launch; do
    # Update targetDir paths
    sed -i '' "s/greetings-gradle\/2\.${PREV_MINOR_VERSION}\.0-J17/greetings-gradle\/${VERSION}.0-J17/g" "$file"
    sed -i '' "s/greetings-maven\/2\.${PREV_MINOR_VERSION}\.0-J17/greetings-maven\/${VERSION}.0-J17/g" "$file"
    sed -i '' "s/greetings-tycho\/2\.${PREV_MINOR_VERSION}\.0-J17/greetings-tycho\/${VERSION}.0-J17/g" "$file"
    # Update xtextVersion
    sed -i '' "s/xtextVersion=2\.${PREV_MINOR_VERSION}\.0-SNAPSHOT/xtextVersion=2.${MINOR_VERSION}.0-SNAPSHOT/g" "$file"
done

for file in "$ROOT_DIR/launch"/refproject-greetings-*-j21.launch; do
    # Update targetDir paths for J21
    sed -i '' "s/greetings-gradle\/2\.${PREV_MINOR_VERSION}\.0-J21/greetings-gradle\/${VERSION}.0-J21/g" "$file"
    sed -i '' "s/greetings-maven\/2\.${PREV_MINOR_VERSION}\.0-J21/greetings-maven\/${VERSION}.0-J21/g" "$file"
    sed -i '' "s/greetings-tycho\/2\.${PREV_MINOR_VERSION}\.0-J21/greetings-tycho\/${VERSION}.0-J21/g" "$file"
    # Update xtextVersion
    sed -i '' "s/xtextVersion=2\.${PREV_MINOR_VERSION}\.0-SNAPSHOT/xtextVersion=2.${MINOR_VERSION}.0-SNAPSHOT/g" "$file"
    # Update javaVersion
    sed -i '' "s/javaVersion=JAVA21/javaVersion=JAVA25/g" "$file"
done

for file in "$ROOT_DIR/launch"/refproject-greetings-*-j25.launch; do
    # Update targetDir paths for J25
    sed -i '' "s/greetings-gradle\/2\.${PREV_MINOR_VERSION}\.0-J25/greetings-gradle\/${VERSION}.0-J25/g" "$file"
    sed -i '' "s/greetings-maven\/2\.${PREV_MINOR_VERSION}\.0-J25/greetings-maven\/${VERSION}.0-J25/g" "$file"
    sed -i '' "s/greetings-tycho\/2\.${PREV_MINOR_VERSION}\.0-J25/greetings-tycho\/${VERSION}.0-J25/g" "$file"
    # Update xtextVersion
    sed -i '' "s/xtextVersion=2\.${PREV_MINOR_VERSION}\.0-SNAPSHOT/xtextVersion=2.${MINOR_VERSION}.0-SNAPSHOT/g" "$file"
done

# Create new script files
echo "Creating new script files..."

# greetings-gradle-J17-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-gradle-J17-${VERSION}.sh" << EOF
cd greetings-gradle/2.${MINOR_VERSION}.0-J17/org.xtext.example.mydsl.parent
./gradlew clean build -Dorg.gradle.daemon=false

EOF

# greetings-gradle-J21-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-gradle-J21-${VERSION}.sh" << EOF
cd greetings-gradle/2.${MINOR_VERSION}.0-J21/org.xtext.example.mydsl.parent
./gradlew clean build -Dorg.gradle.daemon=false

EOF

# greetings-maven-J17-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-maven-J17-${VERSION}.sh" << 'EOF'
if ["$TRAVIS_BUILD_DIR" eq ""]
then
  export TRAVIS_BUILD_DIR=$(pwd)
fi

cd greetings-maven/2.${MINOR_VERSION}.0-J17

export PROFILES=-Ptycho_snapshots
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES clean install
EOF

# greetings-maven-J21-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-maven-J21-${VERSION}.sh" << 'EOF'
if ["$TRAVIS_BUILD_DIR" eq ""]
then
  export TRAVIS_BUILD_DIR=$(pwd)
fi

cd greetings-maven/2.${MINOR_VERSION}.0-J21

export PROFILES=-Ptycho_snapshots
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Djava-21 clean install
EOF

# greetings-tycho-J17-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-tycho-J17-${VERSION}.sh" << 'EOF'
cd greetings-tycho/2.${MINOR_VERSION}.0-J17

TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../../}"

export PROFILES=
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Dtycho.showEclipseLog=true clean install $EXTRA_ARGS
EOF

# greetings-tycho-J21-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-tycho-J21-${VERSION}.sh" << 'EOF'
cd greetings-tycho/2.${MINOR_VERSION}.0-J21

TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../../}"

export PROFILES=
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Djava-21 -Dtycho.showEclipseLog=true clean install $EXTRA_ARGS
EOF

# greetings-gradle-J25-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-gradle-J25-${VERSION}.sh" << EOF
cd greetings-gradle/2.${MINOR_VERSION}.0-J25/org.xtext.example.mydsl.parent
./gradlew clean build -Dorg.gradle.daemon=false

EOF

# greetings-maven-J25-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-maven-J25-${VERSION}.sh" << 'EOF'
if ["$TRAVIS_BUILD_DIR" eq ""]
then
  export TRAVIS_BUILD_DIR=$(pwd)
fi

cd greetings-maven/2.${MINOR_VERSION}.0-J25

export PROFILES=-Ptycho_snapshots
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Djava-25 clean install
EOF

# greetings-tycho-J25-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-tycho-J25-${VERSION}.sh" << 'EOF'
cd greetings-tycho/2.${MINOR_VERSION}.0-J25

TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../../}"

export PROFILES=
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Djava-25 -Dtycho.showEclipseLog=true clean install $EXTRA_ARGS
EOF

# domainmodel-X.XX.sh
cat > "$ROOT_DIR/scripts/domainmodel-${VERSION}.sh" << 'EOF'
cd domainmodel/2.${MINOR_VERSION}.0/org.eclipse.xtext.example.domainmodel.releng

TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../../../}"

export PROFILES=
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Dtycho.showEclipseLog=true clean install $EXTRA_ARGS
EOF

chmod +x "$ROOT_DIR/scripts/greetings-gradle-J17-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-gradle-J21-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-gradle-J25-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-maven-J17-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-maven-J21-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-maven-J25-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-tycho-J17-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-tycho-J21-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-tycho-J25-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/domainmodel-${VERSION}.sh"

echo "Done! Version $VERSION has been updated/created."
echo ""
echo "Summary of changes:"
echo "  - Updated launch files"
echo "  - Created scripts/greetings-gradle-J17-${VERSION}.sh"
echo "  - Created scripts/greetings-gradle-J21-${VERSION}.sh"
echo "  - Created scripts/greetings-gradle-J25-${VERSION}.sh"
echo "  - Created scripts/greetings-maven-J17-${VERSION}.sh"
echo "  - Created scripts/greetings-maven-J21-${VERSION}.sh"
echo "  - Created scripts/greetings-maven-J25-${VERSION}.sh"
echo "  - Created scripts/greetings-tycho-J17-${VERSION}.sh"
echo "  - Created scripts/greetings-tycho-J21-${VERSION}.sh"
echo "  - Created scripts/greetings-tycho-J25-${VERSION}.sh"
echo "  - Created scripts/domainmodel-${VERSION}.sh"
