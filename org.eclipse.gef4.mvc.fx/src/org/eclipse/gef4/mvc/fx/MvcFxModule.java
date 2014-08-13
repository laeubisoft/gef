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
package org.eclipse.gef4.mvc.fx;

import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.mvc.MvcModule;
import org.eclipse.gef4.mvc.behaviors.AbstractHoverBehavior;
import org.eclipse.gef4.mvc.behaviors.AbstractSelectionBehavior;
import org.eclipse.gef4.mvc.behaviors.AbstractZoomBehavior;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.fx.behaviors.FXFocusBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXHoverBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXZoomBehavior;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXFeedbackPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXMarqueeOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnPinchSpreadPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnScrollPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXFocusTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXPinchSpreadTool;
import org.eclipse.gef4.mvc.fx.tools.FXScrollTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class MvcFxModule extends MvcModule<Node> {

	protected void bindAbstractFXContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register default behaviors ( (which are based on viewer models)
		adapterMapBinder
				.addBinding(AdapterKey.get(AbstractHoverBehavior.class)).to(
						FXHoverBehavior.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(AbstractSelectionBehavior.class)).to(
				FXSelectionBehavior.class);
		// TODO: add AbstractFocusBehavior and use as key here
		adapterMapBinder.addBinding(AdapterKey.get(FXFocusBehavior.class)).to(
				FXFocusBehavior.class);
	}

	protected void bindAbstractFXFeedbackPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// nothing to bind by default
	}

	protected void bindAbstractFXHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// TODO: create abstract hover policy and use as key
		adapterMapBinder
				.addBinding(AdapterKey.get(AbstractFXHoverPolicy.class)).to(
						FXHoverOnHoverPolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(AbstractHoverBehavior.class)).to(
						FXHoverBehavior.class);
	}

	protected void bindFXDefaultFeedbackPartFactory() {
		binder().bind(new TypeLiteral<IFeedbackPartFactory<Node>>() {
		}).to(FXDefaultFeedbackPartFactory.class);
	}

	protected void bindFXDefaultHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).to(FXDefaultHandlePartFactory.class);
	}

	protected void bindFXDomain() {
		binder().bind(new TypeLiteral<IDomain<Node>>() {
		}).to(FXDomain.class);
	}

	protected void bindFXDomainAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(FXHoverTool.class))
				.toInstance(new FXHoverTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.class))
				.toInstance(new FXClickDragTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.class))
				.toInstance(new FXTypeTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXPinchSpreadTool.class))
				.toInstance(new FXPinchSpreadTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXScrollTool.class))
				.toInstance(new FXScrollTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXFocusTool.class))
				.toInstance(new FXFocusTool());

		adapterMapBinder.addBinding(AdapterKey.get(IViewer.class)).to(
				new TypeLiteral<IViewer<Node>>() {
				});
	}

	protected void bindFXRootPart() {
		binder().bind(new TypeLiteral<IRootPart<Node>>() {
		}).to(FXRootPart.class);
	}

	protected void bindFXRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY)).to(
				FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY)).to(
				FXMarqueeOnDragPolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY)).to(
						FXHoverOnHoverPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXScrollTool.TOOL_POLICY_KEY)).to(
				FXZoomOnScrollPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXPinchSpreadTool.TOOL_POLICY_KEY)).to(
				FXZoomOnPinchSpreadPolicy.class);

		// register (default) behaviors (which are based on viewer models)
		adapterMapBinder.addBinding(
				AdapterKey.get(AbstractSelectionBehavior.class)).to(
				FXSelectionBehavior.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(AbstractHoverBehavior.class)).to(
						FXHoverBehavior.class);
		adapterMapBinder.addBinding(AdapterKey.get(AbstractZoomBehavior.class))
				.to(FXZoomBehavior.class);
	}

	protected void bindFXViewer() {
		binder().bind(new TypeLiteral<IViewer<Node>>() {
		}).to(FXViewer.class);
	}

	protected void bindFXViewerAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(IRootPart.class)).to(
				new TypeLiteral<IRootPart<Node>>() {
				});

		adapterMapBinder.addBinding(AdapterKey.get(IContentPartFactory.class))
				.to(new TypeLiteral<IContentPartFactory<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(IHandlePartFactory.class))
				.to(new TypeLiteral<IHandlePartFactory<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(IFeedbackPartFactory.class))
				.to(new TypeLiteral<IFeedbackPartFactory<Node>>() {
				});

	}

	@Override
	protected void configure() {
		super.configure();

		// bind root IRootPart<Node>, IViewer<Node> and IDomain<Node> to
		// FXRootPart, FXViewer, and FXDomain
		bindFXDomain();
		bindFXViewer();
		bindFXRootPart();

		// bind default factories for handles and feedback
		bindFXDefaultHandlePartFactory();
		bindFXDefaultFeedbackPartFactory();

		// bind additional adapters for FXDomain
		bindFXDomainAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXDomain.class));

		bindFXViewerAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXViewer.class));

		// bind additional adapters for FXRootPart
		bindFXRootPartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXRootPart.class));

		// bind additional adapters for FX specific visual parts
		bindAbstractFXContentPartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), AbstractFXContentPart.class));
		bindAbstractFXFeedbackPartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), AbstractFXFeedbackPart.class));
		bindAbstractFXHandlePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), AbstractFXHandlePart.class));
	}

}
