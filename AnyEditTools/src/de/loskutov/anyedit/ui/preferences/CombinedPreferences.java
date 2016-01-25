/*******************************************************************************
 * Copyright (c) 2009 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/

package de.loskutov.anyedit.ui.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import de.loskutov.anyedit.AnyEditToolsPlugin;
import de.loskutov.anyedit.IAnyEditConstants;

public class CombinedPreferences {

    private final IPreferenceStore pluginStore;

    private final IEclipsePreferences preferences;

    public CombinedPreferences(IScopeContext projectContext, IPreferenceStore pluginStore) {
        super();
        if (projectContext != null) {
            IEclipsePreferences node = projectContext.getNode(AnyEditToolsPlugin.getDefault()
                    .getBundle().getSymbolicName());
            if (node.getBoolean(IAnyEditConstants.PROJECT_PROPS_ENABLED, false)) {
                preferences = node;
            } else {
                preferences = null;
            }
        } else {
            preferences = null;
        }
        this.pluginStore = pluginStore;
    }

    public String getString(String key) {
        String result = null;
        if (preferences != null) {
            result = preferences.get(key, null);
        }
        if (result == null) {
            result = pluginStore.getString(key);
        }
        return result;
    }

    public boolean getBoolean(String key) {
        String resultStr = null;
        if (preferences != null) {
            resultStr = preferences.get(key, null);
        }
        boolean result;
        if (resultStr != null) {
            result = preferences.getBoolean(key, Boolean.valueOf(resultStr).booleanValue());
        } else {
            result = pluginStore.getBoolean(key);
        }
        return result;
    }

    public void setBoolean(String key, boolean value) {
        if (preferences != null) {
            preferences.putBoolean(key, value);
            try {
                preferences.sync();
            } catch (BackingStoreException e) {
                AnyEditToolsPlugin.logError("Failed to remember property: " + key, e);
            }
        } else {
            pluginStore.setValue(key, value);
        }
    }

    public int getInt(String key) {
        String resultStr = null;
        if (preferences != null) {
            resultStr = preferences.get(key, null);
        }
        int result;
        if (resultStr != null) {
            int value;
            try {
                value = Integer.parseInt(resultStr);
            } catch (NumberFormatException e) {
                // ignore
                if(IAnyEditConstants.EDITOR_TAB_WIDTH.equals(key)) {
                    value = IAnyEditConstants.DEFAULT_TAB_WIDTH;
                } else {
                    value = 0;
                }
            }
            result = preferences.getInt(key, value);
        } else {
            result = pluginStore.getInt(key);
        }
        return result;
    }

}
