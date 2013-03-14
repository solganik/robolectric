package org.robolectric.shadows;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import org.robolectric.Robolectric;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.res.ResName;
import org.robolectric.res.ResourceIndex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.robolectric.Robolectric.shadowOf;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(TypedArray.class)
public class ShadowTypedArray implements UsesResources {
    private Resources resources;
    private AttributeSet values;
    private int[] attrs;
    private int[] presentAttrs;
    private ResourceIndex resourceIndex;

    public static TypedArray create(Resources resources, AttributeSet set, int[] attrs) {
        TypedArray result = ShadowResources.inject(resources, Robolectric.newInstanceOf(TypedArray.class));
        Robolectric.shadowOf(result).populate(set, attrs);
        return result;
    }

    public void injectResources(Resources resources) {
        this.resources = resources;
        resourceIndex = shadowOf(resources).getResourceLoader().getResourceIndex();
    }

    @Implementation
    public int getIndexCount() {
        return presentAttrs.length;
    }

    @Implementation
    public int getIndex(int at) {
        return presentAttrs[at];
    }

    @Implementation
    public Resources getResources() {
        return resources;
    }

    @Implementation
    public CharSequence getText(int index) {
        ResName resName = getResName(index);
        CharSequence str = values.getAttributeValue(resName.namespace, resName.name);
        return str == null ? "" : str;
    }

    @Implementation
    public String getString(int index) {
        ResName resName = getResName(index);
        String str = values.getAttributeValue(resName.namespace, resName.name);
        return str == null ? "" : str;
    }

    @Implementation
    public boolean getBoolean(int index, boolean defValue) {
        ResName resName = getResName(index);
        return values.getAttributeBooleanValue(resName.namespace, resName.name, defValue);
    }

    @Implementation
    public int getInt(int index, int defValue) {
        ResName resName = getResName(index);
        return values.getAttributeIntValue(resName.namespace, resName.name, defValue);
    }

    @Implementation
    public float getFloat(int index, float defValue) {
        ResName resName = getResName(index);
        return values.getAttributeFloatValue(resName.namespace, resName.name, defValue);
    }

    @Implementation
    public int getInteger(int index, int defValue) {
        ResName resName = getResName(index);
        return values.getAttributeIntValue(resName.namespace, resName.name, defValue);
    }

    @Implementation
    public float getDimension(int index, float defValue) {
        return defValue;
    }

    @Implementation
    public int getResourceId(int index, int defValue) {
        ResName resName = getResName(index);
        return values.getAttributeResourceValue(resName.namespace, resName.name, defValue);
    }

    @Implementation
    public java.lang.CharSequence[] getTextArray(int index) {
        ResName resName = getResName(index);
        int resourceId = values.getAttributeResourceValue(resName.namespace, resName.name, -1);
        return resourceId == -1 ? null : resources.getTextArray(resourceId);
    }

    @Implementation
    public boolean getValue(int index, android.util.TypedValue outValue) {
        return false;
    }

    @Implementation
    public boolean hasValue(int index) {
        // todo ResName resName = getResName(index);
        //String attributeValue = values.getAttributeValue(getResName(index).namespace, getResName(index).name);
        return true;
    }

    @Implementation
    public android.util.TypedValue peekValue(int index) {
        return null;
    }

  private ResName getResName(int index) {
        return resourceIndex.getResName(attrs[index]);
    }

    public void populate(AttributeSet set, int[] attrs) {
        if (this.values != null || this.attrs != null) throw new IllegalStateException();
        this.values = set;
        this.attrs = attrs;

        Set<Integer> attrsPresent = new HashSet<Integer>();
        int count = values.getAttributeCount();
        for (int i = 0; i < count; i++) {
            attrsPresent.add(values.getAttributeNameResource(i));
        }
        attrsPresent.retainAll(Arrays.asList(box(attrs)));
        this.presentAttrs = new int[attrsPresent.size()];
        int j = 0;
        if (attrs == null) return;
        for (int i = 0; i < attrs.length; i++) {
            int attr = attrs[i];
            if (attrsPresent.contains(attr)) {
                presentAttrs[j++] = i;
            }
        }
    }

    private Integer[] box(int[] ints) {
        if (ints == null) return new Integer[0];
        Integer[] integers = new Integer[ints.length];
        for (int i = 0; i < ints.length; i++) integers[i] = ints[i];
        return integers;
    }

}
