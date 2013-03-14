package org.robolectric.shadows;

import android.app.Activity;
import android.widget.CheckedTextView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(TestRunners.WithDefaults.class)
public class CheckedTextViewTest {

    private CheckedTextView checkedTextView;

    @Before
    public void beforeTests() {
        checkedTextView = new CheckedTextView(new Activity());
    }

    @Test
    public void testToggle() {
        assertFalse(checkedTextView.isChecked());

        checkedTextView.toggle();

        assertTrue(checkedTextView.isChecked());
    }

    @Test
    public void testSetChecked() {
        assertFalse(checkedTextView.isChecked());

        checkedTextView.setChecked(true);

        assertTrue(checkedTextView.isChecked());
    }

    @Test public void toggle_shouldChangeCheckedness() throws Exception {
        CheckedTextView view = new CheckedTextView(Robolectric.application);
        assertFalse(view.isChecked());
        view.toggle();
        assertTrue(view.isChecked());
        view.performClick();
        assertFalse(view.isChecked());
    }
}
