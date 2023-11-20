/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.tests;

import static org.eclipse.xtext.swtbot.testing.api.EclipseAPI.*;
import static org.junit.Assert.*;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.xtext.swtbot.testing.AbstractSwtBotTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests that create Xtext projects using the NewProjectWizard. The builds are triggered and ensured there are no compile
 * errors. Unit tests are executed as well. The different options are iterated through (for eclipse, with LSP, with maven, gradle, source
 * layout ...
 * 
 * @author Arne Deutsch - Initial contribution and API
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class XtextNewProjectWizardTest extends AbstractSwtBotTest {

	@BeforeClass
	public static void initialize() throws Exception {
		AbstractSwtBotTest.initialize();
	}

	@Before
	public void setUp() throws Exception {
		mainMenu().openJavaPerspective();
		mainMenu().openJunitView();
		mainMenu().openConsoleView();
		mainMenu().openPackageExplorer();
		closeAllShells();
		closeAllEditors();
		consoleView().resetAndClearAllConsoles();
		deleteAllProjects();
	}

	@After
	public void tearDown() {
		closeAllShells();
		closeAllEditors();
		consoleView().resetAndClearAllConsoles();
		deleteAllProjects();
		System.out.println("#####################################");
		System.out.println();
		System.out.println();
	}

	@Test @org.junit.Ignore
	public void simpleXtextProject() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'simpleXtextProject'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleEclipsePlugin().toggleGenericIdeSupport()
				.toggleTestingSupport().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl", "Plug-in Dependencies");

		// generate the language
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMWE2("org.xtext.example.mydsl", "src", "org.xtext.example.mydsl", "GenerateMyDsl.mwe2");
		consoleView().waitForMWE2ToFinish();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());

		// replace grammar with one that uses Xbase
		packageExplorer().openXtextFile("org.xtext.example.mydsl", "src", "org.xtext.example.mydsl", "MyDsl.xtext")
				.replaceContent(helloWorldGrammerWithXbase()).save();
		waitForBuild();

		// generate the language again
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMWE2("org.xtext.example.mydsl", "src", "org.xtext.example.mydsl", "GenerateMyDsl.mwe2");
		consoleView().waitForMWE2ToFinish();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test @org.junit.Ignore
	public void simpleXtextProject_withMaven() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'simpleXtextProject_withMaven'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleEclipsePlugin().toggleGenericIdeSupport()
				.toggleTestingSupport().setMavenBuildType().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		
		packageExplorer().mavenRefresh("org.xtext.example.mydsl.parent");
		waitForBuild();

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl");
		packageExplorer().expand("org.xtext.example.mydsl.parent");

		// run the maven build and wait for successful termination
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();

		// replace grammar with one that uses Xbase
		packageExplorer().openXtextFile("org.xtext.example.mydsl", "src", "org.xtext.example.mydsl", "MyDsl.xtext")
				.replaceContent(helloWorldGrammerWithXbase()).save();

		// generate the language again
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test @org.junit.Ignore
	public void xtextProject_forEclipse() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_forEclipse'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleFeature().toggleUpdateSite()
				.toggleTestingSupport().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.feature"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.repository"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ui"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl", "Plug-in Dependencies");

		// generate the language
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMWE2("org.xtext.example.mydsl", "src", "org.xtext.example.mydsl", "GenerateMyDsl.mwe2");
		consoleView().waitForMWE2ToFinish();
		waitForBuild();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test @org.junit.Ignore
	public void xtextProject_forEclipse_withTesting() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_forEclipse_withTesting'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ui"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ui.tests"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl", "Plug-in Dependencies");

		// generate the language
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMWE2("org.xtext.example.mydsl", "src", "org.xtext.example.mydsl", "GenerateMyDsl.mwe2");
		consoleView().waitForMWE2ToFinish();
		waitForBuild();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// run all unit tests and check there are no test failures
		runJunitTests("org.xtext.example.mydsl.tests", "xtend-gen");
		assertTrue(junitView().isTestrunErrorFree());

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test @org.junit.Ignore
	public void xtextProject_forEclipse_withTesting_withMaven() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_forEclipse_withTesting_withMaven'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleFeature().toggleUpdateSite().setMavenBuildType()
				.finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.target"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ui"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ui.tests"));
		
		packageExplorer().mavenRefresh("org.xtext.example.mydsl.parent");
		waitForBuild();

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl", "Plug-in Dependencies");

		// run the maven build and wait for successful termination
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test @org.junit.Ignore
	public void xtextProject_forEclipse_withTesting_withGradle() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_forEclipse_withTesting_withGradle'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleUpdateSite().setGradleBuildType().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.feature"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.repository"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.target"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ui"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ui.tests"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl", "Plug-in Dependencies");
		
		packageExplorer().mavenRefresh("org.xtext.example.mydsl.parent");
		waitForBuild();

		// run the maven build and wait for successful termination
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runGradleTest("org.xtext.example.mydsl.parent", "build.gradle");
		consoleView().waitForGradleToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test @org.junit.Ignore
	public void xtextProject_webIntegration_withMaven_plain() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_webIntegration_withMaven_plain'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleEclipsePlugin().toggleWebIntegration()
				.setMavenBuildType().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.web"));
		
		packageExplorer().mavenRefresh("org.xtext.example.mydsl.parent");
		waitForBuild();
		
		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl.web", "Maven Dependencies");

		// run the maven build and wait for successful termination
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test @org.junit.Ignore
	public void xtextProject_webIntegration_withMaven_nested() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_webIntegration_withMaven_nested'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleEclipsePlugin().toggleWebIntegration()
				.setMavenBuildType().setMavenSourceLayout().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.web"));
		
		packageExplorer().mavenRefresh("org.xtext.example.mydsl.parent");
		waitForBuild();

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl.web", "Maven Dependencies");

		// run the maven build and wait for successful termination
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test
	public void xtextProject_webIntegration_withGradle_plain() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_webIntegration_withGradle_plain'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleEclipsePlugin().toggleWebIntegration()
				.setGradleBuildType().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.web"));

		// do a gradle refresh on parent
		consoleView().resetAndClearAllConsoles();
		packageExplorer().refreshGradleProject("org.xtext.example.mydsl.parent");
		packageExplorer().screenshot();
		sleep(500); // wait for asynchronous updates
		packageExplorer().screenshot();
		consoleView().waitForGradleToFinishWithSuccess();
		packageExplorer().screenshot();

		// run the maven build and wait for successful termination
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runGradleTest("org.xtext.example.mydsl.parent", "build.gradle");
		consoleView().waitForGradleToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test
	public void xtextProject_webIntegration_withGradle_nested() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_webIntegration_withGradle_nested'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleEclipsePlugin().toggleWebIntegration()
				.setGradleBuildType().setMavenSourceLayout().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.web"));

		// do a gradle refresh on parent
		consoleView().resetAndClearAllConsoles();
		packageExplorer().screenshot();
		packageExplorer().refreshGradleProject("org.xtext.example.mydsl.parent");
		packageExplorer().screenshot();
		sleep(500); // wait for asynchronous updates
		packageExplorer().screenshot();
		consoleView().waitForGradleToFinishWithSuccess();
		packageExplorer().screenshot();

		// run the maven build and wait for successful termination
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runGradleTest("org.xtext.example.mydsl.parent", "build.gradle");
		consoleView().waitForGradleToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test @org.junit.Ignore
	public void xtextProject_webIntegration_withLSPRegular() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_webIntegration_withLSPRegular'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleEclipsePlugin().setMavenBuildType()
				.setBuildLanguageServerRegular().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));
		
		packageExplorer().mavenRefresh("org.xtext.example.mydsl.parent");
		waitForBuild();

		// run the maven build and wait for successful termination
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		// workaround for race ...
		cleanBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test @org.junit.Ignore
	public void xtextProject_webIntegration_withLSPFatJar() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtextProject_webIntegration_withLSPFatJar'");
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().selectJava11().next().toggleEclipsePlugin().setMavenBuildType()
				.setBuildLanguageServerFatJar().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));
		
		packageExplorer().mavenRefresh("org.xtext.example.mydsl.parent");
		waitForBuild();

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl", "Maven Dependencies");

		// run the maven build and wait for successful termination
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		// workaround for race ...
		cleanBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	private String helloWorldGrammerWithXbase() {
		return "grammar org.xtext.example.mydsl.MyDsl with org.eclipse.xtext.xbase.Xbase\n"
				+ "generate myDsl \"http://www.xtext.org/example/mydsl/MyDsl\"\n" + "Model:\n" + "    greetings+=Greeting*;\n" + "    \n"
				+ "Greeting:\n" + "    'Hello' name=ID '=' value=XBlockExpression;";
	}

}
