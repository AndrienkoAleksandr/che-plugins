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

package org.eclipse.ide.ext.machine;

import com.google.inject.AbstractModule;

import org.eclipse.che.inject.DynaModule;

@DynaModule
public class ProjectListenerModule extends AbstractModule {
    protected void configure() {
        bind(MachineProjectEventService.class);
    }
}
