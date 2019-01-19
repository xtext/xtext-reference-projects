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

	@Before
	public void setUp() {
		mainMenu().openJavaPerspective();
		mainMenu().openJunitView();
		mainMenu().openConsoleView();
		mainMenu().openPackageExplorer();
		closeAllShells();
		closeAllEditors();
		deleteAllProjects();
	}

	@After
	public void tearDown() {
		closeAllShells();
		closeAllEditors();
		deleteAllProjects();
	}

	@Test
	public void simpleXtextProject() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleEclipsePlugin().toggleGenericIdeSupport().toggleTestingSupport()
				.finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl", "Plug-in Dependencies");

		// generate the language
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
		packageExplorer().runMWE2("org.xtext.example.mydsl", "src", "org.xtext.example.mydsl", "GenerateMyDsl.mwe2");
		consoleView().waitForMWE2ToFinish();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test
	public void simpleXtextProject_withMaven() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleEclipsePlugin().toggleGenericIdeSupport().toggleTestingSupport()
				.setMavenBuildType().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl");
		packageExplorer().expand("org.xtext.example.mydsl.parent");

		// run the maven build and wait for successful termination
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();

		// replace grammar with one that uses Xbase
		packageExplorer().openXtextFile("org.xtext.example.mydsl", "src", "org.xtext.example.mydsl", "MyDsl.xtext")
				.replaceContent(helloWorldGrammerWithXbase()).save();

		// generate the language again
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test
	public void xtextProject_forEclipse() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleFeature().toggleUpdateSite().toggleTestingSupport().finish();
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
		packageExplorer().runMWE2("org.xtext.example.mydsl", "src", "org.xtext.example.mydsl", "GenerateMyDsl.mwe2");
		consoleView().waitForMWE2ToFinish();
		waitForBuild();
		// workaround for race ...
		cleanBuild();
		waitForBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test
	public void xtextProject_forEclipse_withTesting() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().finish();
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

	@Test
	public void xtextProject_forEclipse_withTesting_withMaven() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleFeature().toggleUpdateSite().setMavenBuildType().finish();
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

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl", "Plug-in Dependencies");

		// run the maven build and wait for successful termination
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
	public void xtextProject_forEclipse_withTesting_withGradle() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleUpdateSite().setGradleBuildType().finish();
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

		// run the maven build and wait for successful termination
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
	public void xtextProject_webIntegration_withMaven_plain() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleEclipsePlugin().toggleWebIntegration().setMavenBuildType()
				.finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.web"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl.web", "Maven Dependencies");

		// run the maven build and wait for successful termination
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
	public void xtextProject_webIntegration_withMaven_nested() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleEclipsePlugin().toggleWebIntegration().setMavenBuildType()
				.setMavenSourceLayout().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.web"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl.web", "Maven Dependencies");

		// run the maven build and wait for successful termination
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
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleEclipsePlugin().toggleWebIntegration().setGradleBuildType()
				.finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.web"));

		// run the maven build and wait for successful termination
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
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleEclipsePlugin().toggleWebIntegration().setGradleBuildType()
				.setMavenSourceLayout().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.web"));

		// run the maven build and wait for successful termination
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
	public void xtextProject_webIntegration_withLSPRegular() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleEclipsePlugin().setMavenBuildType()
				.setBuildLanguageServerRegular().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));

		// run the maven build and wait for successful termination
		packageExplorer().runMavenInstall("org.xtext.example.mydsl.parent", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		// workaround for race ...
		cleanBuild();

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test
	public void xtextProject_webIntegration_withLSPFatJar() throws Exception {
		// create xtext project
		mainMenu().openNewProjectWizard().selectXtextProject().next().toggleEclipsePlugin().setMavenBuildType()
				.setBuildLanguageServerFatJar().finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.ide"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.parent"));
		assertTrue(packageExplorer().projectExist("org.xtext.example.mydsl.tests"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.xtext.example.mydsl", "Maven Dependencies");

		// run the maven build and wait for successful termination
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
