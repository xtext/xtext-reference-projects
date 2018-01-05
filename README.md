[![Build Status](https://travis-ci.org/itemis/xtext-reference-projects.svg?branch=master)](https://travis-ci.org/itemis/xtext-reference-projects)

# xtext-reference-projects
A collection of xtext sample projects for build integration tests

### Setting up a new project

- create the project 
  - create a new project via the wizard and commit the generated files
  - the generated project should be located under [projectName]-[buildSystem]/[xtextVersion]/
  - generate the language and commit the generated source files
- add the project to the travis build
  - create a shell script in the scripts folder
  - in the `.travis.yml` file, edit `matrix` and add a `BUILDCMD` entry for the shell script
- add the wizard configuration used to generate the project to this readme file

### Wizard configuration



|                                       | greetings-gradle | greetings-maven | greetings-tycho |
|---------------------------------------|------------------|-----------------|-----------------|
| Eclipse plug-in                       | NO               | NO              | YES             |
| Create Feature                        | NO               | NO              | YES             |
| Create Update Site                    | NO               | NO              | YES             |
| IntelliJ IDEA plug-in                 | NO               | NO              | NO              |
| Web Integration                       | NO               | NO              | NO              |
| Generic IDE Support                   | YES              | YES             | YES             |
| Testing Support                       | YES              | YES             | YES             |
| Preferred Build System                | Gradle           | Maven           | Maven           |
| Build Language Server (Xtext >= 2.13) | Fat Jar          | Regular         | Fat Jar         |
| Source Layout                         | Maven/Gradle     | Maven/Gradle    | Plain           |
  
### Troubleshooting
  
If the build of a gradle project fails with the output "./gradlew: Permission denied", it is possible that the execution rights for "gradlew" are not set correctly. To fix this, run "git update-index --chmod=+x gradlew" in the root folder of the project.