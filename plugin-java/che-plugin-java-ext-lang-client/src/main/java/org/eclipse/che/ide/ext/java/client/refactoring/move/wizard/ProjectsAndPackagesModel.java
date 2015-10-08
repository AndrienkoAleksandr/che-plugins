/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.java.client.refactoring.move.wizard;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.JavaProject;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.PackageFragment;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.PackageFragmentRoot;

import java.util.List;

/**
 * A model of a tree which contains all possible destinations from the current workspace.
 *
 * @author Evgen Vidolob
 * @author Valeriy Svydenko
 */
public class ProjectsAndPackagesModel implements TreeViewModel {
    private List<JavaProject>            projects;
    private SingleSelectionModel<Object> selectionModel;
    private JavaResources                resources;

    public ProjectsAndPackagesModel(List<JavaProject> projects, SingleSelectionModel<Object> selectionModel, JavaResources resources) {
        this.projects = projects;
        this.selectionModel = selectionModel;
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null) {
            return new DefaultNodeInfo<>(new ListDataProvider<>(projects), new AbstractCell<JavaProject>() {
                @Override
                public void render(Context context, JavaProject value, SafeHtmlBuilder sb) {
                    sb.appendHtmlConstant(resources.javaCategoryIcon().getSvg().getElement().getString()).appendEscaped(" ");
                    sb.appendEscaped(value.getName());
                }
            }, selectionModel, null);
        }

        if (value instanceof JavaProject) {
            final JavaProject project = (JavaProject)value;
            return new DefaultNodeInfo<>(new ListDataProvider<>(project.getPackageFragmentRoots()),
                                         new AbstractCell<PackageFragmentRoot>() {
                                             @Override
                                             public void render(Context context, PackageFragmentRoot value,
                                                                SafeHtmlBuilder sb) {
                                                 sb.appendHtmlConstant(resources.sourceFolder()
                                                                                .getSvg()
                                                                                .getElement()
                                                                                .getString())
                                                   .appendEscaped(" ");

                                                 sb.appendEscaped(value.getPath().substring(project.getPath().length()));
                                             }
                                         }, selectionModel, null);
        }

        if (value instanceof PackageFragmentRoot) {
            return new DefaultNodeInfo<>(new ListDataProvider<>(((PackageFragmentRoot)value).getPackageFragments()),
                                                        new AbstractCell<PackageFragment>() {
                                                            @Override
                                                            public void render(Context context, PackageFragment value, SafeHtmlBuilder sb) {
                                                                sb.appendHtmlConstant(resources.packageIcon()
                                                                                               .getSvg()
                                                                                               .getElement()
                                                                                               .getString())
                                                                  .appendEscaped(" ");

                                                                if (value.getName().isEmpty()) {
                                                                    sb.appendEscaped("(default package)");
                                                                } else {
                                                                    sb.appendEscaped(value.getName());
                                                                }
                                                            }
                                                        }, selectionModel, null);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLeaf(Object value) {
        return value instanceof PackageFragment;
    }
}