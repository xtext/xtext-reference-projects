#!/bin/bash

# Script to update/create version 2.43 reference projects
# Usage: ./update-version.sh [version] (default: 2.43)

set -e

VERSION=${1:-2.43}
MAJOR_VERSION=$(echo "$VERSION" | cut -d. -f1)
MINOR_VERSION=$(echo "$VERSION" | cut -d. -f2)
PREV_MINOR_VERSION=$((MINOR_VERSION - 1))

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

echo "Updating to version $VERSION..."

# Update .github/workflows/build.yml
echo "Updating .github/workflows/build.yml..."
sed -i '' "s/greetings-gradle-2\.${PREV_MINOR_VERSION}\.sh/greetings-gradle-${VERSION}.sh/g" "$ROOT_DIR/.github/workflows/build.yml"
sed -i '' "s/greetings-maven-2\.${PREV_MINOR_VERSION}\.sh/greetings-maven-${VERSION}.sh/g" "$ROOT_DIR/.github/workflows/build.yml"
sed -i '' "s/greetings-maven-J21-2\.${PREV_MINOR_VERSION}\.sh/greetings-maven-J21-${VERSION}.sh/g" "$ROOT_DIR/.github/workflows/build.yml"
sed -i '' "s/greetings-tycho-2\.${PREV_MINOR_VERSION}\.sh/greetings-tycho-${VERSION}.sh/g" "$ROOT_DIR/.github/workflows/build.yml"
sed -i '' "s/greetings-tycho-J21-2\.${PREV_MINOR_VERSION}\.sh/greetings-tycho-J21-${VERSION}.sh/g" "$ROOT_DIR/.github/workflows/build.yml"
sed -i '' "s/domainmodel-2\.${PREV_MINOR_VERSION}\.sh/domainmodel-${VERSION}.sh/g" "$ROOT_DIR/.github/workflows/build.yml"

# Update launch files
echo "Updating launch files..."
for file in "$ROOT_DIR/launch"/refproject-greetings-*.launch; do
    # Update targetDir paths
    sed -i '' "s/greetings-gradle\/2\.${PREV_MINOR_VERSION}\.0-J21/greetings-gradle\/${VERSION}.0-J21/g" "$file"
    sed -i '' "s/greetings-gradle\/2\.${PREV_MINOR_VERSION}\.0/greetings-gradle\/${VERSION}.0/g" "$file"
    sed -i '' "s/greetings-maven\/2\.${PREV_MINOR_VERSION}\.0-J21/greetings-maven\/${VERSION}.0-J21/g" "$file"
    sed -i '' "s/greetings-maven\/2\.${PREV_MINOR_VERSION}\.0/greetings-maven\/${VERSION}.0/g" "$file"
    sed -i '' "s/greetings-tycho\/2\.${PREV_MINOR_VERSION}\.0-J21/greetings-tycho\/${VERSION}.0-J21/g" "$file"
    sed -i '' "s/greetings-tycho\/2\.${PREV_MINOR_VERSION}\.0/greetings-tycho\/${VERSION}.0/g" "$file"
    # Update xtextVersion
    sed -i '' "s/xtextVersion=2\.${PREV_MINOR_VERSION}\.0-SNAPSHOT/xtextVersion=2.${MINOR_VERSION}.0-SNAPSHOT/g" "$file"
done

# Create new script files
echo "Creating new script files..."

# greetings-gradle-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-gradle-${VERSION}.sh" << EOF
cd greetings-gradle/2.${MINOR_VERSION}.0/org.xtext.example.mydsl.parent
./gradlew clean build -Dorg.gradle.daemon=false

EOF

# greetings-gradle-J21-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-gradle-J21-${VERSION}.sh" << EOF
cd greetings-gradle/2.${MINOR_VERSION}.0-J21/org.xtext.example.mydsl.parent
./gradlew clean build -Dorg.gradle.daemon=false

EOF

# greetings-maven-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-maven-${VERSION}.sh" << 'EOF'
if ["$TRAVIS_BUILD_DIR" eq ""]
then
  export TRAVIS_BUILD_DIR=$(pwd)
fi

cd greetings-maven/2.43.0

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

cd greetings-maven/2.43.0-J21

export PROFILES=-Ptycho_snapshots
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Djava-21 clean install
EOF

# greetings-tycho-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-tycho-${VERSION}.sh" << 'EOF'
cd greetings-tycho/2.43.0

TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../../}"

export PROFILES=
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Dtycho.showEclipseLog=true clean install $EXTRA_ARGS
EOF

# greetings-tycho-J21-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-tycho-J21-${VERSION}.sh" << 'EOF'
cd greetings-tycho/2.43.0-J21

TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../../}"

export PROFILES=
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Djava-21 -Dtycho.showEclipseLog=true clean install $EXTRA_ARGS
EOF

# domainmodel-X.XX.sh
cat > "$ROOT_DIR/scripts/domainmodel-${VERSION}.sh" << 'EOF'
cd domainmodel/2.43.0/org.eclipse.xtext.example.domainmodel.releng

TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-$GITHUB_WORKSPACE}"
TRAVIS_BUILD_DIR="${TRAVIS_BUILD_DIR:-../../../}"

export PROFILES=
export SETTINGS="-s $TRAVIS_BUILD_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B $DISABLE_DOWNLOAD_PROGRESS $SETTINGS $PROFILES -Dtycho.showEclipseLog=true clean install $EXTRA_ARGS
EOF

chmod +x "$ROOT_DIR/scripts/greetings-gradle-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-gradle-J21-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-maven-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-maven-J21-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-tycho-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-tycho-J21-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/domainmodel-${VERSION}.sh"

echo "Done! Version $VERSION has been updated/created."
echo ""
echo "Summary of changes:"
echo "  - Updated .github/workflows/build.yml"
echo "  - Updated launch files"
echo "  - Created scripts/greetings-gradle-${VERSION}.sh"
echo "  - Created scripts/greetings-gradle-J21-${VERSION}.sh"
echo "  - Created scripts/greetings-maven-${VERSION}.sh"
echo "  - Created scripts/greetings-maven-J21-${VERSION}.sh"
echo "  - Created scripts/greetings-tycho-${VERSION}.sh"
echo "  - Created scripts/greetings-tycho-J21-${VERSION}.sh"
echo "  - Created scripts/domainmodel-${VERSION}.sh"
