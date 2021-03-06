/******************************************************************************* 
 * Copyright (c) 2012 - 2014 Red Hat, Inc. and others.  
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.ws.jaxrs.ui.internal.validation;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.jboss.tools.ws.jaxrs.core.internal.metamodel.domain.JaxrsJavaApplication;
import org.jboss.tools.ws.jaxrs.core.internal.metamodel.domain.JaxrsMetamodel;
import org.jboss.tools.ws.jaxrs.core.internal.metamodel.domain.JaxrsNameBinding;
import org.jboss.tools.ws.jaxrs.core.internal.metamodel.domain.JaxrsProvider;
import org.jboss.tools.ws.jaxrs.core.internal.metamodel.domain.JaxrsWebxmlApplication;
import org.jboss.tools.ws.jaxrs.core.jdt.Annotation;
import org.jboss.tools.ws.jaxrs.core.metamodel.domain.IJaxrsProvider;
import org.jboss.tools.ws.jaxrs.core.utils.JaxrsClassnames;
import org.jboss.tools.ws.jaxrs.ui.internal.utils.Logger;
import org.jboss.tools.ws.jaxrs.ui.preferences.JaxrsPreferences;

/**
 * Java-based JAX-RS Application validator
 * 
 * @author Xavier Coulon
 * 
 */
public class JaxrsJavaApplicationValidatorDelegate extends AbstractJaxrsElementValidatorDelegate<JaxrsJavaApplication> {

	/**
	 * Constructor
	 * @param markerManager the underlying marker manager to use
	 */
	public JaxrsJavaApplicationValidatorDelegate(final IMarkerManager markerManager) {
		super(markerManager);
	}

	/**
	 * @see org.jboss.tools.ws.jaxrs.ui.internal.validation.AbstractJaxrsElementValidatorDelegate#internalValidate(Object)
	 */
	@Override
	void internalValidate(final JaxrsJavaApplication application, final CompilationUnit ast) throws CoreException {
		Logger.debug("Validating element {}", application);
		validateApplicationOverride(application);
		validateApplicationSubclass(application);
		validateAtLeastOneProviderWithBinding(application);
	}

	/**
	 * Validates that the given {@link JaxrsJavaApplication} is a subclass of {@code javax.ws.rs.core.Application}
	 * @param application the application to validate
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	private void validateApplicationSubclass(final JaxrsJavaApplication application) throws CoreException,
			JavaModelException {
		final IType appJavaElement = application.getJavaElement();
		if (!application.isJaxrsCoreApplicationSubclass()) {
			markerManager.addMarker(application,
					application.getJavaElement()
							.getSourceRange(),
					JaxrsValidationMessages.JAVA_APPLICATION_INVALID_TYPE_HIERARCHY, new String[] { appJavaElement.getFullyQualifiedName() }, JaxrsPreferences.JAVA_APPLICATION_INVALID_TYPE_HIERARCHY,
					JaxrsMarkerResolutionIds.JAVA_APPLICATION_INVALID_TYPE_HIERARCHY_QUICKFIX_ID);
		}
	}

	/**
	 * Validates that there is no {@link JaxrsWebxmlApplication} if this {@link JaxrsJavaApplication} has a {@code javax.ws.rs.ApplicationPath} annotation.
	 * @param application the application to validate
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	private void validateApplicationOverride(final JaxrsJavaApplication application) throws CoreException,
			JavaModelException {
		final Annotation applicationPathAnnotation = application
				.getAnnotation(JaxrsClassnames.APPLICATION_PATH);
		final IType appJavaElement = application.getJavaElement();
		if (!application.isOverriden() && applicationPathAnnotation == null) {
			markerManager.addMarker(application,
					appJavaElement.getNameRange(), JaxrsValidationMessages.JAVA_APPLICATION_MISSING_APPLICATION_PATH_ANNOTATION,
					new String[0], JaxrsPreferences.JAVA_APPLICATION_MISSING_APPLICATION_PATH_ANNOTATION,
					JaxrsMarkerResolutionIds.JAVA_APPLICATION_MISSING_APPLICATION_PATH_ANNOTATION_QUICKFIX_ID);
		}
	}
	
	/**
	 * Validates that there is at least one {@link JaxrsProvider} annotated with
	 * exactly the same custom {@link JaxrsNameBinding} annotation(s) in the
	 * metamodel.
	 * 
	 * @param application
	 *            the {@link JaxrsJavaApplication} to validate.
	 * @throws CoreException 
	 */
	private void validateAtLeastOneProviderWithBinding(final JaxrsJavaApplication javaApplication) throws CoreException {
		if (javaApplication == null) {
			return;
		}
		final Map<String, Annotation> nameBindingAnnotations = javaApplication.getNameBindingAnnotations();
		if (nameBindingAnnotations.isEmpty()) {
			return;
		}
		final JaxrsMetamodel metamodel = javaApplication.getMetamodel();
		// take each NameBinding annotation and look for Providers that
		// have this annotation, too
		annotations_loop:
		for(Entry<String, Annotation> entry : nameBindingAnnotations.entrySet()) {
			final String nameBindingAnnotationClassName = entry.getKey();
			final Collection<IJaxrsProvider> annotatedProviders = metamodel
					.findProvidersByAnnotation(nameBindingAnnotationClassName);
			// if provider binding annotation(s) match the application binding annotations
			for (IJaxrsProvider provider : annotatedProviders) {
				if (javaApplication.getNameBindingAnnotations().keySet().containsAll(provider.getNameBindingAnnotations().keySet())) {
					// provider is valid, at least one method has all those bindings
					continue annotations_loop;
				}
			}
			// otherwise, add a problem marker
			final ISourceRange nameRange = entry.getValue().getJavaAnnotation().getNameRange();
			markerManager.addMarker(javaApplication, nameRange, JaxrsValidationMessages.PROVIDER_MISSING_BINDING, new String[]{nameBindingAnnotationClassName},
					JaxrsPreferences.PROVIDER_MISSING_BINDING);
			
		}
	}

}
