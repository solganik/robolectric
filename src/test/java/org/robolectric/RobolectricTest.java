package org.robolectric;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultRequestDirector;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.shadows.ShadowDisplay;
import org.robolectric.util.TestOnClickListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(TestRunners.WithDefaults.class)
public class RobolectricTest {

    private PrintStream originalSystemOut;
    private ByteArrayOutputStream buff;
    private String defaultLineSeparator;

    @Before
    public void setUp() {
        originalSystemOut = System.out;
        defaultLineSeparator = System.getProperty("line.separator");

        System.setProperty("line.separator", "\n");
        buff = new ByteArrayOutputStream();
        PrintStream testOut = new PrintStream(buff);
        System.setOut(testOut);
    }

    @After
    public void tearDown() throws Exception {
        System.setProperty("line.separator", defaultLineSeparator);
        System.setOut(originalSystemOut);
    }

    @Test
    @Ignore // When this test is run via ant (not Intellj and not Maven) we get a bunch of "No Shadow method found for Typeface.finalize()" in the log along with the message for getContext()
    @Config(shadows = TestShadowView.class)
    public void shouldLogMissingInvokedShadowMethodsWhenRequested() throws Exception {
        Robolectric.logMissingInvokedShadowMethods();

        View aView = new View(Robolectric.application);
        // There's a shadow method for this in ShadowView but not TestShadowView
        aView.getContext();
        String output = buff.toString();
        assertThat(output).contains("No Shadow method found for View.__constructor__(android.content.Context)\n");
        buff.reset();

        aView.findViewById(27);
        // No shadow here... should be logged
        output = buff.toString();
        assertEquals("No Shadow method found for View.findViewById(int)\n", output);
    }

    @Test // This is nasty because it depends on the test above having run first in order to fail
    @Ignore // we aren't running that test right now...
    public void shouldNotLogMissingInvokedShadowMethodsByDefault() throws Exception {
        View aView = new View(Robolectric.application);
        aView.findViewById(27);
        String output = buff.toString();

        assertEquals("", output);
    }

    @Test(expected = RuntimeException.class)
    public void clickOn_shouldThrowIfViewIsDisabled() throws Exception {
        View view = new View(Robolectric.application);
        view.setEnabled(false);
        Robolectric.clickOn(view);
    }

    @Test
    public void shouldResetBackgroundSchedulerBeforeTests() throws Exception {
        assertThat(Robolectric.getBackgroundScheduler().isPaused()).isFalse();
        Robolectric.getBackgroundScheduler().pause();
    }

    @Test
    public void shouldResetBackgroundSchedulerAfterTests() throws Exception {
        assertThat(Robolectric.getBackgroundScheduler().isPaused()).isFalse();
        Robolectric.getBackgroundScheduler().pause();
    }

    @Test
    public void httpRequestWasSent_ReturnsTrueIfRequestWasSent() throws IOException, HttpException {
        makeRequest("http://example.com");

        assertTrue(Robolectric.httpRequestWasMade());
    }

    @Test
    public void httpRequestWasMade_ReturnsFalseIfNoRequestWasMade() {
        assertFalse(Robolectric.httpRequestWasMade());
    }

    @Test
    public void httpRequestWasMade_returnsTrueIfRequestMatchingGivenRuleWasMade() throws IOException, HttpException {
        makeRequest("http://example.com");
        assertTrue(Robolectric.httpRequestWasMade("http://example.com"));
    }

    @Test
    public void httpRequestWasMade_returnsFalseIfNoRequestMatchingGivenRuleWasMAde() throws IOException, HttpException {
        makeRequest("http://example.com");
        assertFalse(Robolectric.httpRequestWasMade("http://example.org"));
    }

    @Test
    public void idleMainLooper_executesScheduledTasks() {
        final boolean[] wasRun = new boolean[]{false};
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wasRun[0] = true;
            }
        }, 2000);

        assertFalse(wasRun[0]);
        Robolectric.idleMainLooper(1999);
        assertFalse(wasRun[0]);
        Robolectric.idleMainLooper(1);
        assertTrue(wasRun[0]);
    }

    @Test
    public void shouldUseSetDensityForContexts() throws Exception {
        assertThat(new Activity().getResources().getDisplayMetrics().density).isEqualTo(1.0f);
        Robolectric.setDisplayMetricsDensity(1.5f);
        assertThat(new Activity().getResources().getDisplayMetrics().density).isEqualTo(1.5f);
    }

    @Test
    public void shouldUseSetDisplayForContexts() throws Exception {
        assertThat(new Activity().getResources().getDisplayMetrics().widthPixels).isEqualTo(480);
        assertThat(new Activity().getResources().getDisplayMetrics().heightPixels).isEqualTo(800);

        Display display = Robolectric.newInstanceOf(Display.class);
        ShadowDisplay shadowDisplay = shadowOf(display);
        shadowDisplay.setWidth(100);
        shadowDisplay.setHeight(200);
        Robolectric.setDefaultDisplay(display);

        assertThat(new Activity().getResources().getDisplayMetrics().widthPixels).isEqualTo(100);
        assertThat(new Activity().getResources().getDisplayMetrics().heightPixels).isEqualTo(200);
    }

    @Test
    public void clickOn_shouldCallClickListener() throws Exception {
        View view = new View(Robolectric.application);
        TestOnClickListener testOnClickListener = new TestOnClickListener();
        view.setOnClickListener(testOnClickListener);
        Robolectric.clickOn(view);
        assertTrue(testOnClickListener.clicked);
    }

    @Implements(View.class)
    public static class TestShadowView {
        @SuppressWarnings({"UnusedDeclaration"})
        @Implementation
        public Context getContext() {
            return null;
        }
    }

    private void makeRequest(String uri) throws HttpException, IOException {
        Robolectric.addPendingHttpResponse(200, "a happy response body");

        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                return 0;
            }

        };
        DefaultRequestDirector requestDirector = new DefaultRequestDirector(null, null, null, connectionKeepAliveStrategy, null, null, null, null, null, null, null, null);

        requestDirector.execute(null, new HttpGet(uri), null);
    }
}
