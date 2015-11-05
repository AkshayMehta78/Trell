package app.desidime.ui.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import app.geochat.LoginActivity;

/**
 * Refer : https://developer.android.com/training/activity-testing/activity-basic-testing.html
 * Created by akshay on 23/7/15.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity mLoginActivity;
    private TextView mTextView;

    /**
     * The constructor is invoked by the test runner to instantiate the test class
     * @param activityClass
     */
    public LoginActivityTest(Class<LoginActivity> activityClass) {
        super(activityClass);
    }

    /**
     * Default constructor is required otherwise it will throw junit.framework.AssertionFailedError exception
     */
    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    /**
     * Invoked by the test runner before it runs any tests in the test class.
     * <ul>Typically, in the setUp() method, you should:
     * <li>Invoke the superclass constructor for setUp(), which is required by JUnit.</li>
     * <li><ul>Initialize your test fixture state by:
     *      <li>Defining the instance variables that store the state of the fixture.</li>
     *      <li>Creating and storing a reference to an instance of the Activity under test.</li>
     *      <li>Obtaining a reference to any UI components in the Activity that you want to test.</li>
     * </ul></li>
     * </ul>
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLoginActivity = getActivity();
        //mTextView = (TextView) mLoginActivity.findViewById(R.id.fullscreen_content);
    }

    /**
     * <ul>As a sanity check, it is good practice to verify that the test fixture has been set up correctly,
     * and the objects that you want to test have been correctly instantiated or initialized.
     * That way, you won’t have to see tests failing because something was wrong with the setup of your test fixture.
     * By convention, the method for verifying your test fixture is called testPreconditions().
     * <li>If the condition is false, the assertion method throws an AssertionFailedError exception, which is then typically reported by the test runner.
     * You can provide a string in the first argument of your assertion method to give some contextual details if the assertion fails.</li>
     * <li>If the condition is true, the test passes.</li>
     * </ul>
     *
     */
    public void testPreconditions() {
        assertNotNull("mLoginActivity is null", mLoginActivity);
        assertNotNull("mTextView is null", mTextView);
    }

    /**
     * Method simply checks that the default text of the TextView that is set by the layout is the same as the expected text defined in the strings.xml resource.<br/>
     * <b>NOTE : When naming test methods, you can use an underscore to separate what is being tested from the specific case being tested.
     * This style makes it easier to see exactly what cases are being tested.</b><br/><br/>
     * When doing this type of string value comparison, it’s good practice to read the expected string from your resources,
     * instead of hardcoding the string in your comparison code.
     * This prevents your test from easily breaking whenever the string definitions are modified in the resource file.<br/>
     * To perform the comparison, pass both the expected and actual strings as arguments to the assertEquals() method.
     * If the values are not the same, the assertion will throw an <b>AssertionFailedError</b> exception.
     *
     */
    public void testTextView_dummyText() {
        /*final String expected = mLoginActivity.getString(R.string.dummy_content);
        final String actual = mTextView.getText().toString();
        assertEquals(expected, actual);*/
    }
}
