package org.robolectric.shadows;

import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(TestRunners.WithDefaults.class)
public class AbsoluteLayoutTest {
    @Test
    public void getLayoutParams_shouldReturnAbsoluteLayoutParams() throws Exception {
        ViewGroup.LayoutParams layoutParams = new AbsoluteLayout(Robolectric.application).getLayoutParams();

        assertThat(layoutParams).isInstanceOf(AbsoluteLayout.LayoutParams.class);
    }
}
