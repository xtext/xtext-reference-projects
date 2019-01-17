/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotShell;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotView;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTWorkbenchBot;

/**
 * Main menu of eclipse. Can be used to start all kind of actions, e.g. open the preferences.
 * 
 * @author Arne Deutsch - Initial contribution and API
 * @author Christian Dietrich
 * @author Karsten Thoms
 */
public class MainMenuAPI {

	private final XtextSWTWorkbenchBot bot;

	MainMenuAPI(XtextSWTWorkbenchBot bot) {
		this.bot = bot;
	}

	/**
	 * Opens the Eclipse Preferences dialog. On MacOS the preferences are in the system menu which is not accessible via SWTBot.
	 */
	public PreferencesDialogAPI openPreferences() {
		System.out.println("Open 'Preferences'");
		// see https://www.eclipse.org/forums/index.php/t/854280/
		if (Platform.getOS().equals(Platform.OS_MACOSX)) {
			// On Mac, the Preferences menu is under the system menu
			final IWorkbench workbench = PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(() -> {
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if (window != null) {
					Menu appMenu = workbench.getDisplay().getSystemMenu();
					Optional<MenuItem> item = Arrays.stream(appMenu.getItems())
							.filter(i -> i.getText().startsWith("Preferences") || i.getText().startsWith("Einstellungen")).findFirst();
					if (item.isPresent()) {
						MenuItem menuItem = item.get();
						Event event = new Event();
						event.time = (int) System.currentTimeMillis();
						event.widget = menuItem;
						event.display = workbench.getDisplay();
						menuItem.setSelection(true);
						menuItem.notifyListeners(SWT.Selection, event);
					}
				}
			});
		} else {
			bot.menu("Window").menu("Preferences").click();
		}
		return new PreferencesDialogAPI(bot.shell("Preferences"));
	}

	public NewProjectWizardAPI openNewProjectWizard() {
		System.out.println("Open 'New Project Wizard'");
		bot.menu("File").menu("New").menu("Project...").click();
		return new NewProjectWizardAPI(bot.shell("New Project"));
	}

	public void openJavaPerspective() {
		System.out.println("Open 'Java Perspective'");
		openPerspective("Java");
	}

	private void openPerspective(String name) {
		if (name.equals(bot.activePerspective().getLabel())) {
			return;
		}
		bot.menu("Window").menu("Perspective").menu("Open Perspective").menu("Other...").click();
		XtextSWTBotShell dialog = bot.shell("Open Perspective");
		dialog.bot().table().select(name);
		dialog.bot().button("Open").click();
	}

	public PackageExplorerAPI openPackageExplorer() {
		System.out.println("Open 'Package Explorer'");
		return new PackageExplorerAPI(openAndActivateView("Java", "Package Explorer", "org.eclipse.jdt.ui.PackageExplorer"));
	}

	public ProblemsViewAPI openProblemsView() {
		System.out.println("Open 'Problems View'");
		return new ProblemsViewAPI(openAndActivateView("General", "Problems", "org.eclipse.ui.views.ProblemView"));
	}

	public JUnitViewAPI openJunitView() {
		System.out.println("Open 'JUnit View'");
		return new JUnitViewAPI(openAndActivateView("Java", "JUnit", "org.eclipse.jdt.junit.ResultView"));
	}

	public ConsoleViewAPI openConsoleView() {
		System.out.println("Open 'Console View'");
		return new ConsoleViewAPI(openAndActivateView("General", "Console", "org.eclipse.ui.console.ConsoleView"));
	}

	private XtextSWTBotView openAndActivateView(String category, String viewName, String viewId) {
		for (SWTBotView view : bot.views()) {
			if (viewId.equals(view.getReference().getId())) {
				view.setFocus();
				return (XtextSWTBotView) view;
			}
		}
		bot.menu("Window").menu("Show View").menu("Other...").click();
		XtextSWTBotShell shell = bot.shell("Show View");
		shell.bot().tree().expandNode(new String[] { category, viewName }).select();
		shell.bot().button("Open").click();
		XtextSWTBotView view = bot.viewById(viewId);
		view.setFocus();
		return view;
	}

}
