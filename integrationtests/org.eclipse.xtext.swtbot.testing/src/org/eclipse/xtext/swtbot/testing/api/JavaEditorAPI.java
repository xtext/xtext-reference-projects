/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.api;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;

/**
 * @author Arne Deutsch - Initial contribution and API
 */
public class JavaEditorAPI extends TextEditorAPI {

	public JavaEditorAPI(SWTBotEditor editor) {
		super(editor);
	}

	public JavaEditorAPI organizeImports() {
		System.out.println("Organize imports for editor '" + editor.getTitle() + "'");
		editor.bot().styledText().contextMenu("Source").menu("Organize Imports").click();
		return this;
	}
	
}
