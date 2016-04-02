/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.behaviors.GraphLayoutBehavior;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.NavigationModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel.ViewportState;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;

import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Pair;

/**
 * The {@link GraphPart} is the controller for a {@link Graph} content object.
 * It starts a layout pass after activation and when its content children
 * change.
 *
 * @author mwienand
 *
 */
public class GraphPart extends AbstractFXContentPart<Group> {

	private MapChangeListener<String, Object> graphAttributesObserver = new MapChangeListener<String, Object>() {

		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends Object> change) {
			// refresh visuals
			refreshVisual();
			// apply layout
			applyLayout(true);
		}
	};

	private ListChangeListener<Object> graphChildrenObserver = new ListChangeListener<Object>() {

		@SuppressWarnings("serial")
		@Override
		public void onChanged(ListChangeListener.Change<? extends Object> c) {
			// synchronize children
			getAdapter(new TypeToken<ContentBehavior<Node>>() {
			}).synchronizeContentChildren(doGetContentChildren());

			// update layout context
			updateLayoutContext();
			// apply layout
			getAdapter(GraphLayoutBehavior.class).applyLayout(true);
		}
	};

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	private void applyLayout(boolean clean) {
		GraphLayoutBehavior layoutBehavior = getAdapter(GraphLayoutBehavior.class);
		if (layoutBehavior != null) {
			layoutBehavior.applyLayout(clean);
		}
	}

	@Override
	protected Group createVisual() {
		Group visual = new Group();
		visual.setAutoSizeChildren(false);
		return visual;
	}

	@Override
	protected void doActivate() {
		super.doActivate();

		getContent().attributesProperty().addListener(graphAttributesObserver);
		getContent().getNodes().addListener(graphChildrenObserver);
		getContent().getEdges().addListener(graphChildrenObserver);

		// apply layout if no viewport state is saved for this graph, or we are
		// nested inside a node, or the saved viewport is outdated
		NavigationModel navigationModel = getViewer().getAdapter(NavigationModel.class);
		ViewportState savedViewport = navigationModel == null ? null : navigationModel.getViewportState(getContent());
		InfiniteCanvas canvas = ((FXViewer) getViewer()).getCanvas();
		boolean isNested = getParent() instanceof NodePart;
		boolean isViewportChanged = savedViewport != null
				&& (savedViewport.getWidth() != canvas.getWidth() || savedViewport.getHeight() != canvas.getHeight());
		if (savedViewport == null || isNested || isViewportChanged) {
			applyLayout(true);
		}
	}

	@Override
	protected void doDeactivate() {
		getContent().attributesProperty().removeListener(graphAttributesObserver);
		getContent().getNodes().removeListener(graphChildrenObserver);
		getContent().getEdges().removeListener(graphChildrenObserver);
		super.doDeactivate();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		List<Object> children = new ArrayList<>();
		children.addAll(getContent().getNodes());
		for (org.eclipse.gef4.graph.Node n : getContent().getNodes()) {
			if (ZestProperties.getExternalLabel(n) != null) {
				children.add(new Pair<>(n, ZestProperties.ELEMENT_EXTERNAL_LABEL));
			}
		}
		children.addAll(getContent().getEdges());
		for (Edge e : getContent().getEdges()) {
			if (ZestProperties.getLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.ELEMENT_LABEL));
			}
			if (ZestProperties.getExternalLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.ELEMENT_EXTERNAL_LABEL));
			}
			if (ZestProperties.getSourceLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.EDGE_SOURCE_LABEL));
			}
			if (ZestProperties.getTargetLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.EDGE_TARGET_LABEL));
			}
		}
		return children;
	}

	@Override
	public void doRefreshVisual(Group visual) {
		// TODO: setGraphStyleSheet();
	}

	@Override
	public Graph getContent() {
		return (Graph) super.getContent();
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

	@Override
	public void setContent(Object content) {
		super.setContent(content);

		// update layout context
		updateLayoutContext();
	}

	private void updateLayoutContext() {
		GraphLayoutContext layoutContext = getAdapter(GraphLayoutContext.class);
		if (layoutContext != null) {
			layoutContext.setGraph(getContent());
		}
	}

}
