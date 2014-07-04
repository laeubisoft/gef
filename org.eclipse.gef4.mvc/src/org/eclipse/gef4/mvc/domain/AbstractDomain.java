/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.mvc.IActivatable;
import org.eclipse.gef4.mvc.bindings.AdaptableSupport;
import org.eclipse.gef4.mvc.bindings.AdapterMap;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 * @author anyssen
 * 
 * @param <VR>
 */
// add getTools() method that uses the adapters
public abstract class AbstractDomain<VR> implements IDomain<VR> {

	private AdaptableSupport<IDomain<VR>> as = new AdaptableSupport<IDomain<VR>>(
			this);

	private Set<IViewer<VR>> viewers;
	private IOperationHistory operationHistory;
	private IUndoContext undoContext;

	public AbstractDomain() {
	}

	@Override
	public <T> T getAdapter(Class<T> key) {
		return as.getAdapter(key);
	}

	@Override
	public <T> void setAdapter(Class<T> key, T adapter) {
		as.setAdapter(key, adapter);
	}

	@Inject
	public void setAdapters(
			@AdapterMap(AbstractDomain.class) Map<Class<?>, Object> adaptersWithKeys) {
		// do not override locally registered adapters (e.g. within constructor
		// of respective AbstractDomain) with those injected by Guice
		as.setAdapters(adaptersWithKeys, false);
	}

	@Override
	public <T> T unsetAdapter(Class<T> key) {
		return as.unsetAdapter(key);
	}

	@Override
	public IOperationHistory getOperationHistory() {
		return operationHistory;
	}

	@Inject
	@Override
	public void setOperationHistory(
			@Named("AbstractDomain") IOperationHistory stack) {
		operationHistory = stack;
	}

	public Map<Class<? extends ITool<VR>>, ITool<VR>> getTools() {
		return as.getAdapters(ITool.class);
	}

	@Override
	public IUndoContext getUndoContext() {
		return undoContext;
	}

	@Inject
	@Override
	public void setUndoContext(@Named("AbstractDomain") IUndoContext undoContext) {
		this.undoContext = undoContext;
	}

	@Override
	public void addViewer(IViewer<VR> viewer) {
		if (viewers != null && viewers.contains(viewer)) {
			return;
		}

		// add viewer
		if (viewers == null) {
			viewers = new HashSet<IViewer<VR>>();
		} else {
			// deactivate adapters if they had been activated before (will
			// re-activate them after the viewer has been hooked)
			deactivateAdapters();
		}
		viewers.add(viewer);
		viewer.setDomain(this);

		// activate adapters (tools)
		activateAdapters();
	}

	private void activateAdapters() {
		for (Object a : as.getAdapters().values()) {
			if (a instanceof IActivatable) {
				((IActivatable) a).activate();
			}
		}
	}

	@Override
	public void removeViewer(IViewer<VR> viewer) {
		if (viewers != null && !(viewers.contains(viewer))) {
			return;
		}

		// deactive all adapters (they have to be active), will re-activate them
		// if we are not completely unhooked.
		deactivateAdapters();

		// unhook viewer
		viewer.setDomain(null);
		viewers.remove(viewer);
		if (viewers.isEmpty()) {
			viewers = null;
		} else {
			// if we are not completely unhooked, re-activate adapters
			activateAdapters();
		}
	}

	private void deactivateAdapters() {
		for (Object a : as.getAdapters().values()) {
			if (a instanceof IActivatable) {
				((IActivatable) a).deactivate();
			}
		}
	}

	@Override
	public Set<IViewer<VR>> getViewers() {
		if (viewers == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(viewers);
		}
	}
}
