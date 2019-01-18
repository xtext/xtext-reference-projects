/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.*;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotTree;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotView;

public class JUnitViewAPI {

	private final XtextSWTBotView view;

	JUnitViewAPI(XtextSWTBotView view) {
		this.view = view;
	}

	public void focus() {
		System.out.println("Focus JUnit view");
		view.setFocus();
	}

	public boolean isTestrunErrorFree() {
		boolean result = atLeastOneTestIsRun() && errorCount() == 0 && failureCount() == 0;
		System.out.println("Check if test run is error free: '" + result + "'");
		return result;
	}

	void clearHistory() {
		view.toolbarDropDownButton("Test Run History...").click();
		SWTBot shellBot = view.bot().shell("Test Runs").bot();
		shellBot.button("Remove All").click();
		shellBot.button("OK").click();
	}

	/** package projected by intention, usage is tricky ... use {@link EclipseAPI#runJunitTests(String...)} **/
	void waitForTestrunToFinish() {
		focus();
		waitForTestsToStart();
		waitForTestsToFinish();
	}

	private boolean atLeastOneTestIsRun() {
		return view.bot().text(0).getText().matches("[1-9].*");
	}

	private int errorCount() {
		return Integer.parseInt(view.bot().text(1).getText());
	}

	private int failureCount() {
		return Integer.parseInt(view.bot().text(2).getText());
	}

	private void waitForTestsToStart() {
		XtextSWTBotTree tree = view.bot().tree();
		view.bot().waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Tree has still no items.";
			}

			@Override
			public boolean test() throws Exception {
				return tree.hasItems();
			}
		}, 1000 * 60, 100); // tests might need some tome to start (e.g. eclipse workspace)
	}

	private void waitForTestsToFinish() {
		SWTBotToolbarButton stopButton = view.toolbarButton("Stop JUnit Test Run");
		view.bot().waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Stop button is still enabled.";
			}

			@Override
			public boolean test() throws Exception {
				return !stopButton.isEnabled();
			}
		}, 1000 * 60 * 5, 1000); // might be many tests
	}

}
