/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import java.util.List;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotView;

public final class ConsoleViewAPI {

	private static String previousLine = ""; // static to avoid multiple lines independent of instance count
	
	private final XtextSWTBotView view;

	ConsoleViewAPI(XtextSWTBotView view) {
		this.view = view;
	}

	public void waitForMWE2ToFinish() {
		System.out.println("Wait for MWE2 to finish ...");
		waitForFinish("- Done.", "[ERROR]:", 1000 * 60, 1000 * 60 * 2);
	}

	public void waitForMavenToFinishWithSuccess() {
		System.out.println("Wait for Maven to finish ...");
		waitForFinish("BUILD SUCCESS", "[ERROR]", 1000 * 60 * 10, 1000 * 60 * 10);
	}

	public void waitForGradleToFinishWithSuccess() {
		System.out.println("Wait for Gradle to finish ...");
		waitForFinish("BUILD SUCCESSFUL", "[ERROR]", 1000 * 60 * 10, 1000 * 60 * 10);
	}

	private void waitForFinish(String successText, String failText, long firstTimeout, long secondTimeout) {
		try {
			waitForText(successText, failText, firstTimeout);
			if (view.bot().styledText().getText().contains(successText)) {
				return;
			}
			if (view.bot().styledText().getText().contains(failText)) {
				throw new RuntimeException(view.bot().styledText().getText());
			}
		} catch (TimeoutException e) {
			// wrong console activated!?
		}
		// in case not the success nor the error message appears for some reason the wrong console might be shown
		// activate the last console and check again
		activateLastConsole();
		waitForText(successText, failText, secondTimeout);
		if (view.bot().styledText().getText().contains(successText)) {
			return;
		}
		if (view.bot().styledText().getText().contains(failText)) {
			throw new RuntimeException(view.bot().styledText().getText());
		}
	}

	private void waitForText(String successText, String failText, long timeout) {
		view.bot().waitUntil(new DefaultCondition() {

			@Override
			public String getFailureMessage() {
				return "Process still not terminated.";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotStyledText styledText = view.bot().styledText();
				// Build might run for several minutes ... send keep alive signal
				int lineNumber = Math.max(0, styledText.getLineCount() - 1);
				String lastLine = styledText.getTextOnLine(lineNumber);
				if (lastLine.trim().isEmpty()) {
					lineNumber = Math.max(0, styledText.getLineCount() - 2);
					lastLine = styledText.getTextOnLine(lineNumber);
				}
				if (!previousLine.equals(lastLine)) {
					System.out.println("Console: " + lastLine);
					previousLine = lastLine;
				}
				// check for both, success and error message, to fail without waiting for a timeout 
				return styledText.getText().contains(successText) || styledText.getText().contains(failText);
			}
		}, timeout, 1000);
	}

	private void activateLastConsole() {
		SWTBotToolbarDropDownButton button = view.toolbarDropDownButton("Display Selected Console");
		if (button.isEnabled()) {
			List<? extends SWTBotMenu> menuItems = button.menuItems(WidgetOfType.widgetOfType(MenuItem.class));
			menuItems.get(menuItems.size() - 1).click();
		}
	}
}
