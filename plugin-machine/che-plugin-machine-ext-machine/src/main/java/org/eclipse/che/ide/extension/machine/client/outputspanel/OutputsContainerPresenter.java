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
package org.eclipse.che.ide.extension.machine.client.outputspanel;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.HasView;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.OutputConsole;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for the output consoles.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class OutputsContainerPresenter extends BasePresenter implements OutputsContainerView.ActionDelegate, HasView, ProjectActionHandler {

    private final MachineLocalizationConstant localizationConstant;
    private final OutputsContainerView        view;

    private final List<OutputConsole> consoles;

    @Inject
    public OutputsContainerPresenter(OutputsContainerView view, MachineLocalizationConstant localizationConstant, EventBus eventBus) {
        this.view = view;
        this.localizationConstant = localizationConstant;
        this.view.setTitle(localizationConstant.outputsConsoleViewTitle());
        this.view.setDelegate(this);

        consoles = new ArrayList<>();

        eventBus.addHandler(ProjectActionEvent.TYPE, this);
    }

    /** Add {@code console} to the container. */
    public void addConsole(final OutputConsole console) {
        // check whether console for an appropriate command is already opened
        OutputConsole existingOutputConsole = null;
        for (final OutputConsole outputConsole : consoles) {
            if (console.getCommand().getId().equals(outputConsole.getCommand().getId())) {
                existingOutputConsole = outputConsole;
                break;
            }
        }

        if (existingOutputConsole == null) {
            console.go(new AcceptsOneWidget() {
                @Override
                public void setWidget(IsWidget widget) {
                    consoles.add(console);
                    view.addConsole(console.getTitle(), widget);
                    view.showConsole(consoles.size() - 1);
                }
            });
        } else {
            // replace existing console with new one
            final int existingConsoleIndex = consoles.indexOf(existingOutputConsole);
            console.go(new AcceptsOneWidget() {
                @Override
                public void setWidget(IsWidget widget) {
                    // add new console in place of existing one
                    consoles.add(existingConsoleIndex, console);
                    view.insertConsole(console.getTitle(), widget, existingConsoleIndex);

                    // remove existing console
                    consoles.remove(existingConsoleIndex + 1);
                    view.removeConsole(existingConsoleIndex + 1);

                    view.showConsole(existingConsoleIndex);
                }
            });
        }
    }

    @Override
    public View getView() {
        return view;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizationConstant.outputsConsoleViewTitle();
    }

    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    @Override
    public String getTitleToolTip() {
        return localizationConstant.outputsConsoleViewTooltip();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void onConsoleSelected(int index) {
        view.showConsole(index);
    }

    @Override
    public void onConsoleClose(int index) {
        consoles.remove(index);

        view.removeConsole(index);
        if (index > 0) {
            view.showConsole(index - 1);
        }
    }

    @Override
    public void onProjectOpened(ProjectActionEvent event) {
    }

    @Override
    public void onProjectClosing(ProjectActionEvent event) {
    }

    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        consoles.clear();
        view.removeAllConsoles();
    }
}
