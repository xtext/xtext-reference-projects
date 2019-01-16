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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.xtext.swtbot.testing.lowlevel.XtextSWTBotView;

public final class ConsoleViewAPI {

	private final XtextSWTBotView view;

	ConsoleViewAPI(XtextSWTBotView view) {
		this.view = view;
	}

	public void waitForMWE2ToFinish() {
		waitForFinish("- Done.", "[ERROR]:", 1000 * 60, 1000 * 60 * 2);
	}

	public void waitForMavenToFinishWithSuccess() {
		waitForFinish("BUILD SUCCESS", "[ERROR]", 1000 * 60 * 10, 1000 * 60 * 10);
	}

	public void waitForGradleToFinishWithSuccess() {
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
				// check for both, success and error message, to fail without waiting for a timeout 
				return view.bot().styledText().getText().contains(successText) || view.bot().styledText().getText().contains(failText);
			}
		}, timeout, 500);
	}

	private void activateLastConsole() {
		SWTBotToolbarDropDownButton button = view.toolbarDropDownButton("Display Selected Console");
		if (button.isEnabled()) {
			List<? extends SWTBotMenu> menuItems = button.menuItems(WidgetOfType.widgetOfType(MenuItem.class));
			menuItems.get(menuItems.size() - 1).click();
		}
	}
}
