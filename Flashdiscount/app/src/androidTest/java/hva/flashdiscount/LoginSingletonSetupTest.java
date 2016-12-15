package hva.flashdiscount;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import hva.flashdiscount.utils.LoginSingleton;

/**
 * TODO: Write description.
 *
 * @author sean
 * @since 13/12/2016
 */

@RunWith(AndroidJUnit4.class)
public class LoginSingletonSetupTest {
    private LoginSingleton singleton;

    @Before
    public void setUp() {
        singleton = LoginSingleton.getInstance(null);
    }

    @Test
    public void testNotLoggedInOnInit() {

    }
}
