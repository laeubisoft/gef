/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ide.language;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.gef.dot.internal.language.DotSplineTypeRuntimeModule;
import org.eclipse.gef.dot.internal.language.DotSplineTypeStandaloneSetup;
import org.eclipse.xtext.util.Modules2;

/**
 * Initialization support for running Xtext languages as language servers.
 */
public class DotSplineTypeIdeSetup extends DotSplineTypeStandaloneSetup {

	@Override
	public Injector createInjector() {
		return Guice.createInjector(Modules2.mixin(new DotSplineTypeRuntimeModule(), new DotSplineTypeIdeModule()));
	}
	
}
