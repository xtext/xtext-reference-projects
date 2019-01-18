/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import java.util.Arrays;

import org.eclipse.xtext.swtbot.testing.internal.XtextSWTBotView;

public final class OutlineViewAPI {

	private final XtextSWTBotView view;

	OutlineViewAPI(XtextSWTBotView view) {
		this.view = view;
	}

	public void deleteItem(String... path) {
		System.out.println("Delete outline item " + Arrays.toString(path));
		view.bot().tree().expandNode(path).select().contextMenu("Delete").click();
		view.bot().shell("Delete").bot().button("OK").click();
	}

}
