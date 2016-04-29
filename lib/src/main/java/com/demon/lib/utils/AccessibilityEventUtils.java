/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.demon.lib.utils;

import android.view.accessibility.AccessibilityEvent;

/**
 * This class contains utility methods.
 */
public class AccessibilityEventUtils {
    private AccessibilityEventUtils() {
        // This class is not instantiable.
    }

    /**
     * Determines if an accessibility event is of a type defined by a mask of
     * qualifying event types.
     * @param event The event to evaluate
     * @param typeMask A mask of event types that will cause this method to
     * accept the event as matching
     * @return {@code true} if {@code event}'s type is one of types defined in
     * {@code typeMask}, {@code false} otherwise
     */
    public static boolean eventMatchesAnyType(AccessibilityEvent event, int typeMask) {
        return event != null && (event.getEventType() & typeMask) != 0;
    }

}
