/*
 * Copyright (C) 2016 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.hardware;

import cyanogenmod.hardware.DisplayMode;
import org.cyanogenmod.hardware.util.FileUtils;
import android.util.Log;
import java.io.File;

/*
 * Display Modes API
 *
 * A device may implement a list of preset display modes for different
 * viewing intents, such as movies, photos, or extra vibrance. These
 * modes may have multiple components such as gamma correction, white
 * point adjustment, etc, but are activated by a single control point.
 *
 * This API provides support for enumerating and selecting the
 * modes supported by the hardware.
 */

public class DisplayModeControl {

    private static final String TAG = "DisplayModeControl";

    private static final String CONTROL_PATH = "/sys/devices/platform/s5p-mipi-dsim.1/lcd/panel/mdnie/mode";
    
    private static final String DEFAULT_MODE_FILE = "/data/misc/.displaymodedefault";

    private static final DisplayMode[] ALL_ITEMS = { new DisplayMode (0, "Dynamic"), new DisplayMode (1, "Standard"), new DisplayMode (2, "Natural"), new DisplayMode (3, "Movie"), new DisplayMode (4, "Auto")};

    /*
     * This is needed to get and set the user selected mode
     * on boot.
     */

    static {
        setMode(getDefaultMode(), true);
//        Log.d(TAG, "setMode(getDefaultMode(), true); was called");
    }

    /*
     * All HAF classes should export this boolean.
     * Real implementations must, of course, return true
     */
    public static boolean isSupported() {
        File f = new File(CONTROL_PATH);
        return f.exists();
    }

    /*
     * Get the list of available modes. A mode has an integer
     * identifier and a string name.
     *
     * It is the responsibility of the upper layers to
     * map the name to a human-readable format or perform translation.
     */
    public static DisplayMode[] getAvailableModes() {
        return ALL_ITEMS;
    }

    /*
     * Get the name of the currently selected mode. This can return
     * null if no mode is selected.
     */
    public static DisplayMode getCurrentMode() {

        String currentMode = FileUtils.readOneLine(CONTROL_PATH);

        if (currentMode.equals("0")) {
            return (ALL_ITEMS[0]);
        } else if (currentMode.equals("1")) {
            return (ALL_ITEMS[1]);
        } else if (currentMode.equals("2")) {
            return (ALL_ITEMS[2]);
        } else if (currentMode.equals("3")) {
            return (ALL_ITEMS[3]);
        } else if (currentMode.equals("4")) {
            return (ALL_ITEMS[4]);
        } else
            return null;
    }

    /*
     * Selects a mode from the list of available modes by it's
     * string identifier. Returns true on success, false for
     * failure. It is up to the implementation to determine
     * if this mode is valid.
     */
    public static boolean setMode(DisplayMode mode, boolean makeDefault) {
        boolean err = false;
        err = FileUtils.writeLine(CONTROL_PATH, Integer.toString(mode.id));
//	Log.d(TAG, "writeLine(" + CONTROL_PATH + ") returned: " + err + "; requested mode.id is: " + mode.id);
	if (err && makeDefault) {
	    err = FileUtils.writeLine(DEFAULT_MODE_FILE, Integer.toString(mode.id));
//	    Log.d(TAG, "writeLine(" + DEFAULT_MODE_FILE + ") returned: " + err + "; requested mode is: " + mode);
	}
        return err;
    }

    /*
     * Gets the preferred default mode for this device by it's
     * string identifier. Can return null if there is no default.
     */
    public static DisplayMode getDefaultMode() {
        File f = new File(DEFAULT_MODE_FILE);
	if (f.exists()) {
            return (ALL_ITEMS[Integer.parseInt(FileUtils.readOneLine(DEFAULT_MODE_FILE))]);
	} else {
	    return (ALL_ITEMS[4]);
	}
    }
}