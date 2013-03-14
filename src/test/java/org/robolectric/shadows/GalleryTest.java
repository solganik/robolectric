package org.robolectric.shadows;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Gallery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(TestRunners.WithDefaults.class)
public class GalleryTest {
    private Gallery gallery;
    private TestOnKeyListener listener;
    private KeyEvent event;

    @Before
    public void setUp() throws Exception {
        gallery = new Gallery(Robolectric.application);
        listener = new TestOnKeyListener();
        gallery.setOnKeyListener(listener);
        event = new KeyEvent(1, 2);
    }

    @Test
    public void onKeyDown_dPadRightShouldTriggerKeyEventDPadRight() throws Exception {
        assertTrue(gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, event));
        assertThat(listener.keyCode).isEqualTo(KeyEvent.KEYCODE_DPAD_RIGHT);
        assertThat((Gallery) listener.view).isSameAs(gallery);
        assertThat(listener.event).isSameAs(event);
    }

    @Test
    public void onKeyDown_dPadLeftShouldTriggerKeyEventListener() throws Exception {
        assertTrue(gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, event));
        assertThat(listener.keyCode).isEqualTo(KeyEvent.KEYCODE_DPAD_RIGHT);
        assertThat((Gallery) listener.view).isSameAs(gallery);
        assertThat(listener.event).isSameAs(event);
    }

    private static class TestOnKeyListener implements View.OnKeyListener {
        View view;
        int keyCode;
        KeyEvent event;

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            this.view = view;
            this.keyCode = keyCode;
            this.event = event;
            return false;
        }
    }
}
