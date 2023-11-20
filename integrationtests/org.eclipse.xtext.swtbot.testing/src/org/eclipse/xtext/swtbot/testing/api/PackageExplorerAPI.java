/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotShell;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotView;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTWorkbenchBot;

public class PackageExplorerAPI {

	private final XtextSWTBotView view;

	PackageExplorerAPI(XtextSWTBotView view) {
		this.view = view;
	}

	public boolean projectExist(String projectName) {
		System.out.print("Check is project '" + projectName + "' exists: ");
		if (isTreeEmpty())
			return false;
		for (SWTBotTreeItem i : view.bot().tree().getAllItems()) {
			if (projectName.equals(i.getText())) {
				System.out.println("true");
				return true;
			}
		}
		System.out.println("false");
		return false;
	}

	public PackageExplorerAPI runJUnitPluginTests(String... path) {
		view.bot().tree().expandNode(path).select().contextMenu("Run As").menuWithRegex("? JUnit Plug-in Test").click();
		return this;
	}

	public PackageExplorerAPI runMWE2(String... path) {
		System.out.println("Run MWE2 for " + Arrays.toString(path));
		view.bot().tree().expandNode(path).select().contextMenu("Run As").menuWithRegex("MWE2 Workflow").click();
		return this;
	}

	public void runMavenInstall(String... path) {
		System.out.println("Run maven for " + Arrays.toString(path));
		view.bot().tree().expandNode(path).select().contextMenu("Run As").menu("Run Configurations...").click();
		RunConfigurationsDialogAPI dialog = new RunConfigurationsDialogAPI(view.bot().shell("Run Configurations"));
		dialog.newMavenBuildRunConfiguration().setBaseDirectoryToProject(path[0]).setGoals("clean install").enableUpdateSnapshots()
				.useLocalRepository().disableP2Mirrors().apply().run();
	}

	public void runGradleTest(String... path) {
		System.out.println("Run Gradle for " + Arrays.toString(path));
		view.bot().tree().expandNode(path).select().contextMenu("Run As").menu("Run Configurations...").click();
		RunConfigurationsDialogAPI dialog = new RunConfigurationsDialogAPI(view.bot().shell("Run Configurations"));
		dialog.newGradleProjectRunConfiguration().setGradleTasks("test").setWorkingDirectoryToProject(path[0]).disableShowExecutionView()
				.apply().run();
	}

	public XtextEditorAPI openXtextFile(String... path) {
		System.out.println("Open Xtext editor for " + Arrays.toString(path));
		view.bot().tree().expandNode(path).select().contextMenu("Open").click();
		return new XtextEditorAPI(new XtextSWTWorkbenchBot().editorByTitle(path[path.length - 1]));
	}

	public TextEditorAPI openTextFile(String... path) {
		System.out.println("Open Text Editor for " + Arrays.toString(path));
		view.bot().tree().expandNode(path).select().contextMenu("Open").click();
		return new TextEditorAPI(new XtextSWTWorkbenchBot().editorByTitle(path[path.length - 1]));
	}

	public TextEditorAPI openWithTextEditor(String... path) {
		System.out.println("Open Text Editor for " + Arrays.toString(path));
		view.bot().tree().expandNode(path).select().contextMenu("Open With").menu("Text Editor").click();
		return new TextEditorAPI(new XtextSWTWorkbenchBot().editorByTitle(path[path.length - 1]));
	}

	public JavaEditorAPI openJavaFile(String... path) {
		System.out.println("Open Java Editor for " + Arrays.toString(path));
		view.bot().tree().expandNode(path).select().contextMenu("Open With").menu("Java Editor").click();
		return new JavaEditorAPI(new XtextSWTWorkbenchBot().editorByTitle(path[path.length - 1]));
	}

	public void deleteFile(String... path) {
		System.out.println("Delete file " + Arrays.toString(path));
		view.bot().tree().expandNode(path).select().contextMenu("Delete").click();
		view.bot().shell("Delete").bot().button("OK").click();
	}

	public void deleteAllProjects() {
		System.out.println("Delete all projects");
		if (isTreeEmpty())
			return;
		EclipseAPI.waitForBuild();
		refreshAllProjects(); // avoid dialog with refresh message + collapse all projects
		deleteMavenProjects();
		if (isTreeEmpty())
			return;
		SWTBotTreeItem[] allItems = view.bot().tree().getAllItems();
		view.bot().tree().select(allItems).contextMenu("Delete").click();
		XtextSWTBotShell shell = view.bot().shell("Delete Resources");
		SWTBot shellBot = shell.bot();
		shellBot.checkBox().click();
		shellBot.button("OK").click();
		shell.waitUntilClosed();
	}

	private void deleteMavenProjects() {
		List<String> deleteList = new ArrayList<String>();
		for (SWTBotTreeItem i : view.bot().tree().getAllItems()) {
			String actualProject = i.getText();
			if (actualProject.contains(".parent"))
				deleteList.add(actualProject);
		}
		for (String project : deleteList)
			deleteMavenProject(project.substring(0, project.indexOf(".parent")));
	}

	private void deleteMavenProject(String projectNamePrefix) {
		String parentProjectName = projectNamePrefix + ".parent";
		if (projectExist(parentProjectName)) {
			// delete subprojects without deleting from disk
			collapseAllProjects();
			for (SWTBotTreeItem i : view.bot().tree().getAllItems()) {
				String actualProject = i.getText();
				if (!parentProjectName.equals(actualProject) && actualProject.startsWith(projectNamePrefix)) {
					view.bot().tree().select(actualProject).contextMenu("Delete").click();
					XtextSWTBotShell shell = view.bot().shell("Delete Resources");
					SWTBot shellBot = shell.bot();
					shellBot.button("OK").click();
					shell.waitUntilClosed(1000 * 10);
				}
			}
			refreshAllProjects();
			// delete parent project and delete all resources
			for (SWTBotTreeItem i : view.bot().tree().getAllItems()) {
				String actualProject = i.getText();
				if (parentProjectName.equals(actualProject)) {
					view.bot().tree().select(actualProject).contextMenu("Delete").click();
					XtextSWTBotShell shell = view.bot().shell("Delete Resources");
					SWTBot shellBot = shell.bot();
					shellBot.checkBox().click();
					shellBot.button("OK").click();
					shell.waitUntilClosed(1000 * 10);
				}
			}
		}
	}

	public void refreshAllProjects() {
		System.out.println("Refresh all projects");
		if (isTreeEmpty())
			return;
		collapseAllProjects();
		SWTBotTreeItem[] allItems = view.bot().tree().getAllItems();
		view.bot().tree().select(allItems).contextMenu("Refresh").click();
		EclipseAPI.waitForBuild();
	}

	public void collapseAllProjects() {
		System.out.println("Collapse all projects");
		if (isTreeEmpty())
			return;
		SWTBotTreeItem[] allItems = view.bot().tree().getAllItems();
		int index = 0;
		while (index < allItems.length) {
			allItems[index].collapse();
			allItems = view.bot().tree().getAllItems();
			index++;
		}
	}

	public PackageExplorerAPI expand(String... path) {
		System.out.println("Expand in 'Package Explorer' " + Arrays.toString(path));
		view.bot().tree().expandNode(path);
		return this;
	}
	
	public PackageExplorerAPI mavenRefresh(String path) {
		System.out.println("Maven Refresh in 'Package Explorer' " + path);
		view.bot().tree().expandNode(path).contextMenu("Maven").menu("Update Project...").click();
		view.bot().shell("Update Maven Project").bot().checkBox("Force Update of Snapshots/Releases").select();
		view.bot().shell("Update Maven Project").bot().button("OK").click();
		return this;
	}

	/** package projected by intention, usage is tricky ... use {@link EclipseAPI#runJunitTests(String...)} **/
	PackageExplorerAPI runJUnitTests(String... path) {
		view.bot().tree().expandNode(path).select().contextMenu("Run As").menuWithRegex("? JUnit Test").click();
		return this;
	}

	private boolean isTreeEmpty() {
		try {
			view.bot().waitUntil(waitForWidget(isA(Tree.class)), 200);
		} catch (TimeoutException e) {
			return true;
		}
		return view.bot().tree().getAllItems().length == 0;
	}

}
