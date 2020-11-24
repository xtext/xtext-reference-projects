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
 * Tests that create the Xtext examples using SWTBot.
 * 
 * @author Arne Deutsch - Initial contribution and API
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class XtextExamplesTest extends AbstractSwtBotTest {

	@Before
	public void setUp() {
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

	@Test
	public void xbaseTutorial() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xbaseTutorial'");
		// create example projects
		mainMenu().openNewProjectWizard().selectXtextExample("Xbase Tutorial").finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.xbase.tutorial"));

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

	@Test
	public void domainModelExample() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'domainModelExample'");
		// create example projects
		mainMenu().openNewProjectWizard().selectXtextExample("Xtext Domain-Model Example").finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.domainmodel"));
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.domainmodel" + ".ide"));
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.domainmodel" + ".tests"));
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.domainmodel" + ".ui"));
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.domainmodel" + ".ui.tests"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.eclipse.xtext.example.domainmodel");
		packageExplorer().expand("org.eclipse.xtext.example.domainmodel" + ".ide");
		packageExplorer().expand("org.eclipse.xtext.example.domainmodel" + ".tests");
		packageExplorer().expand("org.eclipse.xtext.example.domainmodel" + ".ui");
		packageExplorer().expand("org.eclipse.xtext.example.domainmodel" + ".ui.tests", "Plug-in Dependencies");

		// work around PDE bug (missing log4j.jar)
		touchFile("org.eclipse.xtext.example.domainmodel" + "/META-INF/MANIFEST.MF");
		waitForBuild();
		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());

		// run all unit tests and check there are no test failures
		runJunitTests("org.eclipse.xtext.example.domainmodel" + ".tests", "xtend-gen");
		assertTrue(junitView().isTestrunErrorFree());

		// run all plugin tests and check there are no test failures
		runJunitPluginTests("org.eclipse.xtext.example.domainmodel" + ".ui.tests", "xtend-gen");
		assertTrue(junitView().isTestrunErrorFree());

		// check that after a regeneration the projects source folders have not changed
		long oldBytes = calculateFolderSize("org.eclipse.xtext.example.domainmodel" + "/src");
		oldBytes += calculateFolderSize("org.eclipse.xtext.example.domainmodel" + ".ide/src");
		oldBytes += calculateFolderSize("org.eclipse.xtext.example.domainmodel" + ".tests/src");
		oldBytes += calculateFolderSize("org.eclipse.xtext.example.domainmodel" + ".ui/src");
		oldBytes += calculateFolderSize("org.eclipse.xtext.example.domainmodel" + ".ui.tests/src");
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMWE2("org.eclipse.xtext.example.domainmodel", "src", "org.eclipse.xtext.example.domainmodel",
				"GenerateDomainmodel.mwe2");
		consoleView().waitForMWE2ToFinish();
		waitForBuild();
		long newBytes = calculateFolderSize("org.eclipse.xtext.example.domainmodel" + "/src");
		newBytes += calculateFolderSize("org.eclipse.xtext.example.domainmodel" + ".ide/src");
		newBytes += calculateFolderSize("org.eclipse.xtext.example.domainmodel" + ".tests/src");
		newBytes += calculateFolderSize("org.eclipse.xtext.example.domainmodel" + ".ui/src");
		newBytes += calculateFolderSize("org.eclipse.xtext.example.domainmodel" + ".ui.tests/src");
		assertEquals(oldBytes, newBytes);

		// run the maven build and wait for successful termination
		packageExplorer().runMavenInstall("org.eclipse.xtext.example.domainmodel.releng", "pom.xml");
		consoleView().waitForMavenToFinishWithSuccess();
		packageExplorer().refreshAllProjects();
	}

	@Test
	public void homeAutomationExample() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'homeAutomationExample'");
		standardXtextExample("Xtext Home Automation Example", "org.eclipse.xtext.example.homeautomation", "GenerateRuleEngine.mwe2");
	}

	@Test
	public void simpleArithmeticsExample() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'simpleArithmeticsExample'");
		// create example projects
		mainMenu().openNewProjectWizard().selectXtextExample("Xtext Simple Arithmetics Example").finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.arithmetics"));
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.arithmetics" + ".ide"));
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.arithmetics" + ".tests"));
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.arithmetics" + ".ui"));
		assertTrue(packageExplorer().projectExist("org.eclipse.xtext.example.arithmetics" + ".ui.tests"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("org.eclipse.xtext.example.arithmetics");
		packageExplorer().expand("org.eclipse.xtext.example.arithmetics" + ".ide");
		packageExplorer().expand("org.eclipse.xtext.example.arithmetics" + ".tests");
		packageExplorer().expand("org.eclipse.xtext.example.arithmetics" + ".ui");
		packageExplorer().expand("org.eclipse.xtext.example.arithmetics" + ".ui.tests", "Plug-in Dependencies");

		// work around PDE bug (missing log4j.jar)
		touchFile("org.eclipse.xtext.example.arithmetics" + "/META-INF/MANIFEST.MF");
		waitForBuild();
		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());

		// run all unit tests and check there are no test failures
		runJunitTests("org.eclipse.xtext.example.arithmetics" + ".tests", "src");
		assertTrue(junitView().isTestrunErrorFree());

		// run all plugin tests and check there are no test failures
		runJunitPluginTests("org.eclipse.xtext.example.arithmetics" + ".ui.tests", "xtend-gen");
		assertTrue(junitView().isTestrunErrorFree());

		// check that after a regeneration the projects source folders have not changed
		long oldBytes = calculateFolderSize("org.eclipse.xtext.example.arithmetics" + "/src");
		oldBytes += calculateFolderSize("org.eclipse.xtext.example.arithmetics" + ".ide/src");
		oldBytes += calculateFolderSize("org.eclipse.xtext.example.arithmetics" + ".tests/src");
		oldBytes += calculateFolderSize("org.eclipse.xtext.example.arithmetics" + ".ui/src");
		oldBytes += calculateFolderSize("org.eclipse.xtext.example.arithmetics" + ".ui.tests/src");
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMWE2("org.eclipse.xtext.example.arithmetics", "src", "org.eclipse.xtext.example.arithmetics",
				"GenerateArithmetics.mwe2");
		consoleView().waitForMWE2ToFinish();
		waitForBuild();
		long newBytes = calculateFolderSize("org.eclipse.xtext.example.arithmetics" + "/src");
		newBytes += calculateFolderSize("org.eclipse.xtext.example.arithmetics" + ".ide/src");
		newBytes += calculateFolderSize("org.eclipse.xtext.example.arithmetics" + ".tests/src");
		newBytes += calculateFolderSize("org.eclipse.xtext.example.arithmetics" + ".ui/src");
		newBytes += calculateFolderSize("org.eclipse.xtext.example.arithmetics" + ".ui.tests/src");
		assertEquals(oldBytes, newBytes);
	}

	@Test
	public void stateMachineExample() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'stateMachineExample'");
		standardXtextExample("Xtext State-Machine Example", "org.eclipse.xtext.example.fowlerdsl", "GenerateStatemachine.mwe2");
	}

	private void standardXtextExample(String exampleLabel, String projectName, String mweFileName) throws Exception {
		// create example projects
		mainMenu().openNewProjectWizard().selectXtextExample(exampleLabel).finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist(projectName));
		assertTrue(packageExplorer().projectExist(projectName + ".ide"));
		assertTrue(packageExplorer().projectExist(projectName + ".tests"));
		assertTrue(packageExplorer().projectExist(projectName + ".ui"));
		assertTrue(packageExplorer().projectExist(projectName + ".ui.tests"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand(projectName);
		packageExplorer().expand(projectName + ".ide");
		packageExplorer().expand(projectName + ".tests");
		packageExplorer().expand(projectName + ".ui", "Plug-in Dependencies");
		packageExplorer().expand(projectName + ".ui.tests");

		// work around PDE bug (missing log4j.jar)
		touchFile(projectName + "/META-INF/MANIFEST.MF");
		waitForBuild();
		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());

		// run all unit tests and check there are no test failures
		runJunitTests(projectName + ".tests", "xtend-gen");
		assertTrue(junitView().isTestrunErrorFree());

		// run all plugin tests and check there are no test failures
		runJunitPluginTests(projectName + ".ui.tests", "xtend-gen");
		assertTrue(junitView().isTestrunErrorFree());

		// check that after a regeneration the projects source folders have not changed
		long oldBytes = calculateFolderSize(projectName + "/src");
		oldBytes += calculateFolderSize(projectName + ".ide/src");
		oldBytes += calculateFolderSize(projectName + ".tests/src");
		oldBytes += calculateFolderSize(projectName + ".ui/src");
		oldBytes += calculateFolderSize(projectName + ".ui.tests/src");
		consoleView().resetAndClearAllConsoles();
		packageExplorer().runMWE2(projectName, "src", projectName, mweFileName);
		consoleView().waitForMWE2ToFinish();
		packageExplorer().refreshAllProjects();
		waitForBuild();
		long newBytes = calculateFolderSize(projectName + "/src");
		newBytes += calculateFolderSize(projectName + ".ide/src");
		newBytes += calculateFolderSize(projectName + ".tests/src");
		newBytes += calculateFolderSize(projectName + ".ui/src");
		newBytes += calculateFolderSize(projectName + ".ui.tests/src");
		assertEquals(oldBytes, newBytes);
	}

}
