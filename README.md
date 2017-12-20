[![Build Status](https://travis-ci.org/itemis/xtext-reference-projects.svg?branch=master)](https://travis-ci.org/itemis/xtext-reference-projects)

# xtext-reference-projects
A collection of xtext sample projects for build integration tests

### Setting up a new project

- create the project 
  - create a new project via the wizard and commit the generated files
  - the generated project should be located under [projectName]-[buildSystem]/[xtextVersion]/
  - generate the language and commit the generated source files
- add the project to the travis build
  - in the .travis.yml file, add the line "  - PROJECT=[projectName]-[buildSystem]/[xtextVersion]" under matrix
- add the wizard configuration used to generate the project to this readme file

### Wizard configuration

- greetings-gradle
  - generic IDE support
  - testing support
  - gradle build system
  - gradle source layout
  - no Eclipse plug-in
  - no IntelliJ IDEA plug-in
  - no web integration

- greetings-maven
  - Eclipse plug-in
  - generic IDE support
  - testing support
  - maven build system
  - plain source layout
  - no Feature
  - no Update Site
  - no IntelliJ IDEA plug-in
  - no web integration
  
  ### Troubleshooting
  
  If the build of a gradle project fails with the output "./gradlew: Permission denied", it is possible that the execution rights for "gradlew" are not set correctly. To fix this, run "git update-index --chmod=+x gradlew" in the root folder of the project.