package org.robolectric.shadows;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import org.robolectric.Robolectric;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;

@Implements(AccessibilityManager.class)
public class ShadowAccessibilityManager {
    @Implementation
    public static AccessibilityManager getInstance(Context context) {
        return Robolectric.newInstanceOf(AccessibilityManager.class);
    }
}
