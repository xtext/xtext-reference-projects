/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.*;

import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotShell;

public class NewProjectWizardAPI {

	private final XtextSWTBotShell shell;

	NewProjectWizardAPI(XtextSWTBotShell shell) {
		this.shell = shell;
	}

	public NewProjectWizardAPI expandXtendExamples() {
		System.out.println("Expand Xtend examples");
		shell.bot().tree().expandNode("Xtend", "Examples").expand();
		return this;
	}

	public NewXtextProjectWizardMainPageAPI selectXtextProject() {
		System.out.println("Select Xtext project");
		shell.bot().tree().expandNode("Xtext").select("Xtext Project");
		System.out.println("Press 'Next >'");
		shell.bot().button("Next >").click();
		return new NewXtextProjectWizardMainPageAPI(shell);
	}

	public NewXtextExampleSecondPageWizardAPI selectXtextExample(String exampleLabel) {
		System.out.println("Select Xtext example '" + exampleLabel + "'");
		shell.bot().tree().expandNode("Xtext", "Examples", exampleLabel).select();
		System.out.println("Press 'Next >'");
		shell.bot().button("Next >").click();
		return new NewXtextExampleSecondPageWizardAPI(shell);
	}

	public NewXtextExampleSecondPageWizardAPI selectXtendExample(String exampleLabel) {
		System.out.println("Select Xtend example '" + exampleLabel + "'");
		// workaround for SWTBot/Tree bug
		shell.bot().tree().expandNode("Xtend", "Examples").collapse();
		shell.bot().tree().expandNode("Xtend", "Examples", exampleLabel).select();
		System.out.println("Press 'Next >'");
		shell.bot().button("Next >").click();
		return new NewXtextExampleSecondPageWizardAPI(shell);
	}

	public void cancel() {
		System.out.println("Press 'Cancel'");
		shell.bot().button("Cancel").click();
	}

	public static class NewXtextExampleSecondPageWizardAPI {

		private final XtextSWTBotShell shell;

		NewXtextExampleSecondPageWizardAPI(XtextSWTBotShell shell) {
			this.shell = shell;
		}

		public void finish() {
			System.out.println("Press 'Finish'");
			shell.bot().button("Finish").click();
			shell.bot().waitUntil(shellCloses(shell), 1000 * 60 * 2, 1000);
		}

	}

	public static class NewXtextProjectWizardMainPageAPI {

		private final XtextSWTBotShell shell;

		NewXtextProjectWizardMainPageAPI(XtextSWTBotShell shell) {
			this.shell = shell;
		}

		public NewXtextProjectWizardConfigurationPageAPI next() {
			System.out.println("Press 'Next >'");
			shell.bot().button("Next >").click();
			return new NewXtextProjectWizardConfigurationPageAPI(shell);
		}

		public void finish() {
			System.out.println("Press 'Finish'");
			shell.bot().button("Finish").click();
			shell.bot().waitUntil(shellCloses(shell), 1000 * 60 * 2, 1000);
		}

	}

	public static class NewXtextProjectWizardConfigurationPageAPI {

		private final XtextSWTBotShell shell;

		NewXtextProjectWizardConfigurationPageAPI(XtextSWTBotShell shell) {
			this.shell = shell;
		}

		public NewXtextProjectWizardConfigurationPageAPI toggleEclipsePlugin() {
			System.out.println("Toggle 'Eclipse plug-in'");
			shell.bot().checkBox("Eclipse plug-in").click();
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI toggleFeature() {
			System.out.println("Toggle 'Create Feature'");
			shell.bot().checkBox("Create Feature").click();
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI toggleUpdateSite() {
			System.out.println("Toggle 'Create Update Site'");
			shell.bot().checkBox("Create Update Site").click();
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI toggleGenericIdeSupport() {
			System.out.println("Toggle 'Generic IDE Support'");
			shell.bot().checkBox("Generic IDE Support").click();
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI toggleTestingSupport() {
			System.out.println("Toggle 'Testing Support'");
			shell.bot().checkBox("Testing Support").click();
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI toggleWebIntegration() {
			System.out.println("Toggle 'Web Integration'");
			shell.bot().checkBox("Web Integration").click();
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI setMavenBuildType() {
			System.out.println("Set 'Maven Build Type'");
			shell.bot().comboBox(0).setSelection("Maven");
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI setGradleBuildType() {
			System.out.println("Set 'Gradle Build Type'");
			shell.bot().comboBox(0).setSelection("Gradle");
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI setBuildLanguageServerRegular() {
			System.out.println("Set 'LSP Regular'");
			shell.bot().comboBox(1).setSelection("Regular");
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI setBuildLanguageServerFatJar() {
			System.out.println("Set 'LSP Fat Jar'");
			shell.bot().comboBox(1).setSelection("Fat Jar");
			return this;
		}

		public NewXtextProjectWizardConfigurationPageAPI setMavenSourceLayout() {
			System.out.println("Set 'Maven Source Layout'");
			shell.bot().comboBox(2).setSelection("Maven/Gradle");
			return this;
		}

		public void finish() {
			System.out.println("Press 'Finish'");
			shell.bot().button("Finish").click();
			shell.bot().waitUntil(shellCloses(shell), 1000 * 60 * 2, 1000);
		}

	}

}
