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
 * Tests to create the Xtend examples using SWTBot.
 * 
 * @author Arne Deutsch - Initial contribution and API
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class XtendExamplesTest extends AbstractSwtBotTest {

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
		System.out.println("#####################################");
		System.out.println();
		System.out.println();
	}

	@Test
	public void xtendActiveAnnotationExamples() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtendActiveAnnotationExamples'");
		// create example projects
		mainMenu().openNewProjectWizard().selectXtendExample("Xtend Active Annotation Examples").finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist("xtend-annotation-examples"));
		assertTrue(packageExplorer().projectExist("xtend-annotation-examples-client"));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand("xtend-annotation-examples", "Plug-in Dependencies");
		packageExplorer().expand("xtend-annotation-examples-client", "Plug-in Dependencies");

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());

		// run all unit tests and check there are no test failures
		runJunitTests("xtend-annotation-examples", "xtend-gen");
		assertTrue(junitView().isTestrunErrorFree());
	}

	@Test
	public void xtendIntroductoryExamples() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtendIntroductoryExamples'");
		standardXtendExample("Xtend Introductory Examples", "xtend-examples");
	}

	@Test
	public void xtendSolutionsForEuler() throws Exception {
		System.out.println();
		System.out.println("Starting Test 'xtendSolutionsForEuler'");
		standardXtendExample("Xtend Solutions for Euler", "xtend-euler");
	}

	private void standardXtendExample(String exampleLabel, String projectName) throws Exception {
		// create example projects
		mainMenu().openNewProjectWizard().selectXtendExample(exampleLabel).finish();
		sleep(500); // wait for asynchronous updates
		waitForBuild();

		// check example projects are created
		assertTrue(packageExplorer().projectExist(projectName));

		// expand projects to get more meaningful screenshots for trouble shooting
		packageExplorer().expand(projectName);

		// check example projects are error free
		assertEquals(problemsView().getErrorMessages().toString(), 0, problemsView().errorCount());
	}

}
