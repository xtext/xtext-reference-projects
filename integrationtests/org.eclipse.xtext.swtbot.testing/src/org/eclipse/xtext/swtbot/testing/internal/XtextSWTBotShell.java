/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.swtbot.testing.internal;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.*;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.PlatformUI;

/**
 * Our own implementation of SWTBotShell to add new methods to standard implementation.
 * 
 * @author Arne Deutsch - Initial contribution and API
 */
public class XtextSWTBotShell extends SWTBotShell {

	XtextSWTBotShell(Shell shell) {
		super(shell);
	}

	@Override
	public XtextSWTBot bot() {
		return new XtextSWTBot(widget);
	}

	public void waitUntilClosed() {
		bot().waitUntil(shellCloses(this));
		UIThreadRunnable.syncExec(new VoidResult() {
			@Override
			public void run() {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();
			}
		});
	}

	public void waitUntilClosed(long timeout) {
		bot().waitUntil(shellCloses(this), timeout);
		UIThreadRunnable.syncExec(new VoidResult() {
			@Override
			public void run() {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();
			}
		});
	}
	
	public void waitUntilClosed(long timeout, long interval) {
		bot().waitUntil(shellCloses(this), timeout, interval);
		UIThreadRunnable.syncExec(new VoidResult() {
			@Override
			public void run() {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();
			}
		});
	}
	
}
