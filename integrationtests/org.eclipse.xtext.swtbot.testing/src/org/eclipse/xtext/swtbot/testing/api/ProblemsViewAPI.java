/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotView;

/**
 * API to access functionality through the Problems View UI.
 * 
 * @author Arne Deutsch - Initial contribution and API
 */
public class ProblemsViewAPI {

	private final XtextSWTBotView view;

	ProblemsViewAPI(XtextSWTBotView view) {
		this.view = view;
	}

	public int errorCount() {
		// after a build it might take a while to refresh the markers ... to be sure we wait a short time
		view.bot().sleep(2000);
		SWTBotTree tree = view.bot().tree();
		for (SWTBotTreeItem i : tree.getAllItems()) {
			String text = i.getText();
			if (text.startsWith("Errors")) {
				text = text.substring("Errors (".length());
				text = text.substring(0, text.indexOf(" "));
				i.expand(); // debugging ... get more meaningful screenshots
				System.out.println("Get error count: '" + text + "'");
				return Integer.parseInt(text);
			}
		}
		System.out.println("Get error count: '0'");
		return 0;
	}

	public List<String> getErrorMessages() {
		// after a build it might take a while to refresh the markers ... to be sure we wait a short time
		view.bot().sleep(2000);
		SWTBotTree tree = view.bot().tree();
		for (SWTBotTreeItem i : tree.getAllItems()) {
			String text = i.getText();
			if (text.startsWith("Errors")) {
				i.expand();
				List<String> result = new ArrayList<>();
				for (SWTBotTreeItem e : i.getItems()) {
					result.add(e.cell(0) + "\n");
				}
				// Make debugging ón server more easy
				System.out.println("Get error messages: " + result);
				return result;
			}
		}
		System.out.println("Get error messages: []");
		return Collections.emptyList();
	}

}
