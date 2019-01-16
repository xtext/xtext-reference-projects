/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.xtext.swtbot.testing.lowlevel.XtextSWTBotShell;

/**
 * @author Arne Deutsch - Initial contribution and API
 */
public class RunConfigurationsDialogAPI {

	private final XtextSWTBotShell shell;

	RunConfigurationsDialogAPI(XtextSWTBotShell shell) {
		this.shell = shell;
	}

	public MavenBuildRunConfigurationAPI newMavenBuildRunConfiguration() {
		shell.bot().tree().select("Maven Build");
		shell.bot().toolbarButtonWithTooltip("New launch configuration").click();
		return new MavenBuildRunConfigurationAPI(shell);
	}

	public GradleBuildRunConfigurationAPI newGradleProjectRunConfiguration() {
		shell.bot().tree().select("Gradle Project");
		shell.bot().toolbarButtonWithTooltip("New launch configuration").click();
		return new GradleBuildRunConfigurationAPI(shell);
	}

	public static class MavenBuildRunConfigurationAPI {

		private final SWTBotShell shell;

		public MavenBuildRunConfigurationAPI(SWTBotShell shell) {
			this.shell = shell;
		}

		public MavenBuildRunConfigurationAPI setBaseDirectoryToProject(String projectName) {
			shell.bot().textWithLabel("Base directory:").setText("${workspace_loc:/" + projectName + "}");
			return this;
		}

		public MavenBuildRunConfigurationAPI setGoals(String goals) {
			shell.bot().textWithLabel("Goals:").setText(goals);
			return this;
		}

		public MavenBuildRunConfigurationAPI enableUpdateSnapshots() {
			shell.bot().checkBox("Update Snapshots").select();
			return this;
		}

		public MavenBuildRunConfigurationAPI useLocalRepository() {
			shell.bot().button("Add...").click();
			SWTBot dialogBot = shell.bot().shell("Add Parameter").bot();
			dialogBot.text(0).setText("maven.repo.local");
			dialogBot.text(1).setText(".m2");
			dialogBot.button("OK").click();
			return this;
		}

		public MavenBuildRunConfigurationAPI apply() {
			shell.bot().button("Apply").click();
			return this;
		}

		public void run() {
			shell.bot().button("Run").click();
		}

	}

	public static class GradleBuildRunConfigurationAPI {

		private final SWTBotShell shell;

		public GradleBuildRunConfigurationAPI(SWTBotShell shell) {
			this.shell = shell;
		}

		public GradleBuildRunConfigurationAPI setWorkingDirectoryToProject(String projectName) {
			shell.bot().textInGroup("Working Directory:").setText("${workspace_loc:/" + projectName + "}");
			return this;
		}

		public GradleBuildRunConfigurationAPI setGradleTasks(String tasks) {
			shell.bot().textInGroup("Gradle Tasks:").setText(tasks);
			return this;
		}

		public GradleBuildRunConfigurationAPI disableShowExecutionView() {
			shell.bot().checkBox("Show Execution View").deselect();
			return this;
		}

		public GradleBuildRunConfigurationAPI apply() {
			shell.bot().button("Apply").click();
			return this;
		}

		public void run() {
			shell.bot().button("Run").click();
		}
	}

}
