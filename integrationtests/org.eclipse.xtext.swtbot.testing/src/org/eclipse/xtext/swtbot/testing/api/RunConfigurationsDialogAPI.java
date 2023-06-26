/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotShell;

/**
 * @author Arne Deutsch - Initial contribution and API
 */
public class RunConfigurationsDialogAPI {

	private final XtextSWTBotShell shell;

	RunConfigurationsDialogAPI(XtextSWTBotShell shell) {
		this.shell = shell;
	}

	public MavenBuildRunConfigurationAPI newMavenBuildRunConfiguration() {
		System.out.println("New maven build run configuration");
		shell.bot().tree().select("Maven Build");
		shell.bot().toolbarButtonWithTooltip("New launch configuration").click();
		return new MavenBuildRunConfigurationAPI(shell);
	}

	public GradleBuildRunConfigurationAPI newGradleProjectRunConfiguration() {
		System.out.println("New gradle build run configuration");
		shell.bot().tree().select("Gradle Task");
		shell.bot().toolbarButtonWithTooltip("New launch configuration").click();
		return new GradleBuildRunConfigurationAPI(shell);
	}

	public static class MavenBuildRunConfigurationAPI {

		private final XtextSWTBotShell shell;

		public MavenBuildRunConfigurationAPI(XtextSWTBotShell shell) {
			this.shell = shell;
		}

		public MavenBuildRunConfigurationAPI setBaseDirectoryToProject(String projectName) {
			System.out.println("Set base directory: '" + projectName + "'");
			shell.bot().textWithLabel("Base directory:").setText("${workspace_loc:/" + projectName + "}");
			return this;
		}

		public MavenBuildRunConfigurationAPI setGoals(String goals) {
			System.out.println("Set goals: '" + goals + "'");
			shell.bot().textWithLabel("Goals:").setText(goals);
			return this;
		}

		public MavenBuildRunConfigurationAPI enableUpdateSnapshots() {
			System.out.println("Enable update snapshots");
			shell.bot().checkBox("Update Snapshots").select();
			return this;
		}

		public MavenBuildRunConfigurationAPI useLocalRepository() {
			System.out.println("Use local repository");
			shell.bot().button("Add...").click();
			SWTBot dialogBot = shell.bot().shell("Add Parameter").bot();
			dialogBot.text(0).setText("maven.repo.local");
			dialogBot.text(1).setText(".m2");
			dialogBot.button("OK").click();
			return this;
		}
		
		public MavenBuildRunConfigurationAPI disableP2Mirrors() {
			System.out.println("Disable P2 Mirrors");
			shell.bot().button("Add...").click();
			SWTBot dialogBot = shell.bot().shell("Add Parameter").bot();
			dialogBot.text(0).setText("tycho.disableP2Mirrors");
			dialogBot.text(1).setText("true");
			dialogBot.button("OK").click();
			return this;
		}

		public MavenBuildRunConfigurationAPI apply() {
			System.out.println("Press 'Apply'");
			shell.bot().button("Apply").click();
			return this;
		}

		public void run() {
			System.out.println("Press 'Run'");
			shell.bot().button("Run").click();
			shell.waitUntilClosed();
		}

	}

	public static class GradleBuildRunConfigurationAPI {

		private final XtextSWTBotShell shell;

		public GradleBuildRunConfigurationAPI(XtextSWTBotShell shell) {
			this.shell = shell;
		}

		public GradleBuildRunConfigurationAPI setWorkingDirectoryToProject(String projectName) {
			System.out.println("Set 'Working Directory' to project '" + projectName + "'");
			shell.bot().textInGroup("Working Directory:").setText("${workspace_loc:/" + projectName + "}");
			return this;
		}

		public GradleBuildRunConfigurationAPI setGradleTasks(String tasks) {
			System.out.println("Set 'Gradle Tasks': '" + tasks + "'");
			// TODO make this work for more than one task
			shell.bot().buttonInGroup("Add", "Gradle Tasks:").click();
			SWTBotTable tableInGroup = shell.bot().tableInGroup("Gradle Tasks:");
			tableInGroup.click(0, 0);
			SWTBotText cellEditor = new SWTBot(tableInGroup.widget).text();
			cellEditor.setText(tasks);
			shell.bot().button("Apply").click();
			return this;
		}

		public GradleBuildRunConfigurationAPI disableShowExecutionView() {
			shell.bot().cTabItem("Project Settings").activate();
			shell.bot().checkBox("Override project settings").select();
			shell.bot().checkBox("Show Executions View").deselect();
			return this;
		}

		public GradleBuildRunConfigurationAPI apply() {
			System.out.println("Press 'Apply'");
			shell.bot().button("Apply").click();
			return this;
		}

		public void run() {
			System.out.println("Press 'Run'");
			shell.bot().button("Run").click();
			shell.waitUntilClosed(1000 * 30);
		}
	}

}
