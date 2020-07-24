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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotView;

public final class ConsoleViewAPI {

	private static int counter = 0; // static to avoid multiple lines independent of instance count

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
		long millis = System.currentTimeMillis();
		try {
			waitForText(successText, failText, firstTimeout);
			if (view.bot().styledText().getText().contains(successText)) {
				System.out.println("Success after: " + ((System.currentTimeMillis() - millis) / 1000) + "sec");
				return;
			}
			if (view.bot().styledText().getText().contains(failText)) {
				System.out.println("Fail after: " + ((System.currentTimeMillis() - millis) / 1000) + "sec");
				throw new RuntimeException(view.bot().styledText().getText());
			}
		} catch (TimeoutException e) {
			// wrong console activated!?
		}
		System.out.println("Retry after: " + ((System.currentTimeMillis() - millis) / 1000) + "sec");
		// in case not the success nor the error message appears for some reason the wrong console might be shown
		// activate the last console and check again
		activateLastConsole();
		waitForText(successText, failText, secondTimeout);
		if (view.bot().styledText().getText().contains(successText)) {
			System.out.println("Success after: " + ((System.currentTimeMillis() - millis) / 1000) + "sec");
			return;
		}
		if (view.bot().styledText().getText().contains(failText)) {
			System.out.println("Fail after: " + ((System.currentTimeMillis() - millis) / 1000) + "sec");
			throw new RuntimeException(view.bot().styledText().getText());
		}
	}

	private void waitForText(String successText, String failText, long timeout) {
		System.out.println("Waiting");
		counter = 0;
		view.bot().waitUntil(new DefaultCondition() {

			@Override
			public String getFailureMessage() {
				return "Process still not terminated.";
			}

			@Override
			public boolean test() throws Exception {
				// Build might run for several minutes ... send keep alive signal
				counter++;
				System.out.print(".");
				if (counter >= 40) {
					System.out.println();
					counter = 0;
				}
				// check for both, success and error message, to fail without waiting for a timeout 
				String text = view.bot().styledText().getText();
				return text.contains(successText) || text.contains(failText);
			}
		}, timeout, 1000);
		System.out.println();
		System.out.println(view.bot().styledText().getText());
	}

	private void activateLastConsole() {
		System.out.println("Activate last console");
		SWTBotToolbarDropDownButton button = view.toolbarDropDownButton("Display Selected Console");
		if (button.isEnabled()) {
			List<? extends SWTBotMenu> menuItems = button.menuItems(WidgetOfType.widgetOfType(MenuItem.class));
			menuItems.get(menuItems.size() - 1).click();
		}
	}

	public void resetAndClearAllConsoles() {
		System.out.println("Reset and clear all consoles");
		clickToolbarButton("Remove All Terminated Launches");
		while (isToolbarButtonActive("Terminate")) {
			clickToolbarButton("Terminate");
			clickToolbarButton("Clear Console");
			clickToolbarButton("Remove All Terminated Launches");
		}
		clickToolbarButton("Clear Console");
	}

	private boolean isToolbarButtonActive(String tooltip) {
		return view.getToolbarButtons().stream().anyMatch(b -> tooltip.equals(b.getToolTipText()) && b.isVisible() && b.isEnabled());
	}

	private void clickToolbarButton(String tooltip) {
		List<SWTBotToolbarButton> buttons = view.getToolbarButtons();
		for (SWTBotToolbarButton b : buttons) {
			if (tooltip.equals(b.getToolTipText())) {
				if (b.isVisible() && b.isEnabled()) {
					System.out.println(" - click button '" + tooltip + "'");
					b.click();
					view.bot().sleep(200); // Wait short time to ensure button states update accordingly
				}
				return;
			}
		}
		System.out.println(" - no button found '" + tooltip + "'");
	}
}
