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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotShell;
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
		if (!result) {
			// for each test case in the view
			for (SWTBotTreeItem testCase : view.bot().tree().getAllItems()) {
				System.out.println();
				System.out.println("TEST CASE: " + testCase.getText());
				SWTBotTreeItem[] items = testCase.getItems();
				// for each test in the test case
				for (SWTBotTreeItem test : items) {
					// select it
					test.select();
					SWTBotTable failureTrace = view.bot().table();
					int rowCount = failureTrace.rowCount();
					String testText = test.getText();
					// and see whether the "Failure Trace" table gets populated;
					// if it is, that test has failed
					if (rowCount > 0 && !testText.isBlank()) {
						// sometimes the testText is empty and we get it at the next iteration
						System.out.println();
						System.out.println("FAILED TEST: " + testText);
						System.out.println("FAILURE TRACE:");
						for (int r = 0; r < rowCount; r++) {
							System.out.println(failureTrace.getTableItem(r).getText());
						}
						System.out.println("END FAILURE TRACE of: " + testText);
					}
				}
			}
			System.out.println();
		}
		return result;
	}

	void clearHistory() {
		view.toolbarDropDownButton("Test Run History...").click();
		XtextSWTBotShell shell = view.bot().shell("Test Runs");
		SWTBot shellBot = shell.bot();
		shellBot.button("Remove All").click();
		shellBot.button("OK").click();
		shell.waitUntilClosed();
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
