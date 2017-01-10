package hva.flashdiscount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentActivity;
import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import hva.flashdiscount.service.GpsService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.any;
import static org.junit.Assert.assertThat;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GPSServiceIntegrationTest {

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    private GpsService mGpsService;
    private FragmentActivity test = new FragmentActivity();

    @Before
    public void createGpsService(){
        mGpsService = new GpsService(test);
    }

    @Test
    public void testWithStartedService() {
        try {
            mServiceRule.startService(
                    new Intent(InstrumentationRegistry.getTargetContext(), GpsService.class));
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        assertTrue("True wasn't returned", mGpsService.checkPermission());

    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.

        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("hva.flashdiscount", appContext.getPackageName());
    }
}
