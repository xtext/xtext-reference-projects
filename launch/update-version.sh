#!/bin/bash

# Script to update/create version reference projects
# Usage: ./update-version.sh [version] (default: 2.45)

set -e

# In-place replacement that works with both BSD sed (macOS) and GNU sed (Linux).
# The usual "sed -i ''" idiom is BSD-only and makes GNU sed fail, while "sed -i" loses
# the file suffix on BSD. Writing to a temp file and copying it back avoids both, so
# GNU sed (gsed) is not required on macOS. The copy preserves inode and permissions.
# mktemp is given an explicit template because BSD mktemp requires one, while GNU mktemp
# also accepts it.
sed_replace() {
    local script="$1"
    local file="$2"
    local tmp
    tmp="$(mktemp "${TMPDIR:-/tmp}/update-version.XXXXXX")"
    sed "$script" "$file" > "$tmp"
    cat "$tmp" > "$file"
    rm -f "$tmp"
}

VERSION=${1:-2.45}
MAJOR_VERSION=$(echo "$VERSION" | cut -d. -f1)
MINOR_VERSION=$(echo "$VERSION" | cut -d. -f2)
PREV_MINOR_VERSION=$((MINOR_VERSION - 1))

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

echo "Updating to version $VERSION..."

# Update launch files
echo "Updating launch files..."

for file in "$ROOT_DIR/launch"/refproject-greetings-*-j21.launch; do
    # Update targetDir paths for J21
    sed_replace "s/greetings-gradle\/2\.${PREV_MINOR_VERSION}\.0-J21/greetings-gradle\/${VERSION}.0-J21/g" "$file"
    sed_replace "s/greetings-maven\/2\.${PREV_MINOR_VERSION}\.0-J21/greetings-maven\/${VERSION}.0-J21/g" "$file"
    sed_replace "s/greetings-tycho\/2\.${PREV_MINOR_VERSION}\.0-J21/greetings-tycho\/${VERSION}.0-J21/g" "$file"
    # Update xtextVersion
    sed_replace "s/xtextVersion=2\.${PREV_MINOR_VERSION}\.0-SNAPSHOT/xtextVersion=2.${MINOR_VERSION}.0-SNAPSHOT/g" "$file"
done

for file in "$ROOT_DIR/launch"/refproject-greetings-*-j25.launch; do
    # Update targetDir paths for J25
    sed_replace "s/greetings-gradle\/2\.${PREV_MINOR_VERSION}\.0-J25/greetings-gradle\/${VERSION}.0-J25/g" "$file"
    sed_replace "s/greetings-maven\/2\.${PREV_MINOR_VERSION}\.0-J25/greetings-maven\/${VERSION}.0-J25/g" "$file"
    sed_replace "s/greetings-tycho\/2\.${PREV_MINOR_VERSION}\.0-J25/greetings-tycho\/${VERSION}.0-J25/g" "$file"
    # Update xtextVersion
    sed_replace "s/xtextVersion=2\.${PREV_MINOR_VERSION}\.0-SNAPSHOT/xtextVersion=2.${MINOR_VERSION}.0-SNAPSHOT/g" "$file"
done

# Create new script files
echo "Creating new script files..."

# greetings-gradle-J21-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-gradle-J21-${VERSION}.sh" << EOF
cd greetings-gradle/2.${MINOR_VERSION}.0-J21/org.xtext.example.mydsl.parent
./gradlew clean build -Dorg.gradle.daemon=false

EOF

# greetings-maven-J21-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-maven-J21-${VERSION}.sh" << EOF
REPO_DIR="\${GITHUB_WORKSPACE:-\$(pwd)}"

cd greetings-maven/2.${MINOR_VERSION}.0-J21

export PROFILES=-Ptycho_snapshots
export SETTINGS="-s \$REPO_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml \$DISABLE_DOWNLOAD_PROGRESS \$SETTINGS \$PROFILES -Djava-21 clean install
EOF

# greetings-tycho-J21-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-tycho-J21-${VERSION}.sh" << EOF
cd greetings-tycho/2.${MINOR_VERSION}.0-J21

REPO_DIR="\${GITHUB_WORKSPACE:-../../}"

export PROFILES=
export SETTINGS="-s \$REPO_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml \$DISABLE_DOWNLOAD_PROGRESS \$SETTINGS \$PROFILES -Djava-21 -Dtycho.showEclipseLog=true clean install \$EXTRA_ARGS
EOF

# greetings-gradle-J25-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-gradle-J25-${VERSION}.sh" << EOF
cd greetings-gradle/2.${MINOR_VERSION}.0-J25/org.xtext.example.mydsl.parent
./gradlew clean build -Dorg.gradle.daemon=false

EOF

# greetings-maven-J25-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-maven-J25-${VERSION}.sh" << EOF
REPO_DIR="\${GITHUB_WORKSPACE:-\$(pwd)}"

cd greetings-maven/2.${MINOR_VERSION}.0-J25

export PROFILES=-Ptycho_snapshots
export SETTINGS="-s \$REPO_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml \$DISABLE_DOWNLOAD_PROGRESS \$SETTINGS \$PROFILES -Djava-25 clean install
EOF

# greetings-tycho-J25-X.XX.sh
cat > "$ROOT_DIR/scripts/greetings-tycho-J25-${VERSION}.sh" << EOF
cd greetings-tycho/2.${MINOR_VERSION}.0-J25

REPO_DIR="\${GITHUB_WORKSPACE:-../../}"

export PROFILES=
export SETTINGS="-s \$REPO_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B -f org.xtext.example.mydsl.parent/pom.xml \$DISABLE_DOWNLOAD_PROGRESS \$SETTINGS \$PROFILES -Djava-25 -Dtycho.showEclipseLog=true clean install \$EXTRA_ARGS
EOF

# domainmodel-X.XX.sh
cat > "$ROOT_DIR/scripts/domainmodel-${VERSION}.sh" << EOF
cd domainmodel/2.${MINOR_VERSION}.0/org.eclipse.xtext.example.domainmodel.releng

REPO_DIR="\${GITHUB_WORKSPACE:-../../../}"

export PROFILES=
export SETTINGS="-s \$REPO_DIR/settings.xml"
export DISABLE_DOWNLOAD_PROGRESS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn 

mvn -B \$DISABLE_DOWNLOAD_PROGRESS \$SETTINGS \$PROFILES -Dtycho.showEclipseLog=true clean install \$EXTRA_ARGS
EOF

chmod +x "$ROOT_DIR/scripts/greetings-gradle-J21-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-gradle-J25-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-maven-J21-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-maven-J25-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-tycho-J21-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/greetings-tycho-J25-${VERSION}.sh"
chmod +x "$ROOT_DIR/scripts/domainmodel-${VERSION}.sh"

echo "Done! Version $VERSION has been updated/created."
echo ""
echo "Summary of changes:"
echo "  - Updated launch files"
echo "  - Created scripts/greetings-gradle-J21-${VERSION}.sh"
echo "  - Created scripts/greetings-gradle-J25-${VERSION}.sh"
echo "  - Created scripts/greetings-maven-J21-${VERSION}.sh"
echo "  - Created scripts/greetings-maven-J25-${VERSION}.sh"
echo "  - Created scripts/greetings-tycho-J21-${VERSION}.sh"
echo "  - Created scripts/greetings-tycho-J25-${VERSION}.sh"
echo "  - Created scripts/domainmodel-${VERSION}.sh"
