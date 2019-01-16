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
public class TextEditorAPI {

	protected final SWTBotEditor editor;

	public TextEditorAPI(SWTBotEditor editor) {
		this.editor = editor;
	}

	public TextEditorAPI replaceContent(String newContent) {
		editor.bot().styledText().setText(newContent);
		return this;
	}

	public TextEditorAPI save() {
		editor.bot().styledText().contextMenu().menu("Save").click();
		return this;
	}

	public TextEditorAPI deleteRange(String startText, String endText) {
		int lineIndex = 0;
		boolean deleting = false;
		for (String line : editor.bot().styledText().getLines()) {
			if (line.contains(startText))
				deleting = true;
			if (deleting) {
				editor.bot().styledText().selectLine(lineIndex);
				editor.bot().styledText().contextMenu("Cut").click();
				if (line.contains(endText))
					break;
			}
			lineIndex++;
		}
		return this;
	}

	public TextEditorAPI deleteLine(String lineText) {
		int lineIndex = 0;
		for (String line : editor.bot().styledText().getLines()) {
			if (line.equals(lineText)) {
				editor.bot().styledText().selectLine(lineIndex);
				editor.bot().styledText().contextMenu("Cut").click();
				return this;
			}
			lineIndex++;
		}
		throw new RuntimeException("Line '" + lineText + "' not found.");
	}

}
