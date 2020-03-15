package com.example.kjankiewicz.android02c01;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.RemoteException;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.uiautomator.UiDevice;
import android.util.DisplayMetrics;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasHost;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasPath;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasScheme;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {

    private void changeActivityLocale(final Activity a, String locale ){
        Resources res = a.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(locale);
        res.updateConfiguration(conf, dm);
        a.getResources().updateConfiguration(conf, dm);

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Intent starterIntent = a.getIntent();
                a.finish();
                a.startActivity(starterIntent);
            }
        });
    }

    private Activity getActivityInstance(){
        final Activity[] currentActivity = {null};

        getInstrumentation().runOnMainSync(new Runnable(){
            public void run(){
                Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                Iterator<Activity> it = resumedActivity.iterator();
                currentActivity[0] = it.next();
            }
        });

        return currentActivity[0];
    }

    @Rule
    public ActivityTestRule<MyMainActivity> mActivityRule =
            new ActivityTestRule<>(MyMainActivity.class);

    @Test
    public void incrementationButtonPortaitWorks() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button2)).perform(click());
        onView(withId(R.id.button2)).perform(click());
        onView(withId(R.id.button2)).check(matches(withText("2")));
    }

    @Test
    public void incrementationButtonLandscapeWorks() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.setOrientationLeft();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button2)).perform(click());
        onView(withId(R.id.button2)).perform(click());
        onView(withId(R.id.button2)).check(matches(withText("2")));
    }

    @Test
    public void activitySavesItsState() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.EditText01)).perform(clearText(), typeText("AAA"));
        onView(withId(R.id.EditText02)).perform(clearText(), typeText("BBB"));
        onView(withId(R.id.button2)).perform(click());
        onView(withId(R.id.button2)).perform(click());
        try {
            device.setOrientationLeft();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.EditText01)).check(matches(withText("AAA")));
        onView(withId(R.id.EditText02)).check(matches(withText("BBB")));
        onView(withId(R.id.button2)).check(matches(withText("2")));
    }

    @Test
    public void secondActivityIsRunInPortrait() {
        Intents.init();
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button1)).perform(click());
        intended(hasComponent(MySecondActivity.class.getName()));
    }

    @Test
    public void secondActivityIsRunInLandscape() {
        Intents.init();
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.setOrientationLeft();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button1)).perform(click());
        intended(hasComponent(MySecondActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void webBrowserIsRunInPortrait() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button1)).perform(click());
        Intents.init();
        onView(withId(R.id.button1)).perform(click());
        intended(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(allOf(hasScheme("http"),
                        hasHost("jankiewicz.pl"),
                        hasPath("/studenci/panum.html")))
                )
        );
        Intents.release();
    }

    @Test
    public void webBrowserIsRunInLandscape() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.setOrientationLeft();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button1)).perform(click());
        Intents.init();
        onView(withId(R.id.button1)).perform(click());
        intended(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(allOf(hasScheme("http"),
                        hasHost("jankiewicz.pl"),
                        hasPath("/studenci/panum.html")))
                )
        );
        Intents.release();
    }

    @Test
    public void testDefaultResources() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        changeActivityLocale(getActivityInstance(), "us");
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.TextView03)).check(matches(withText("Portrait")));
        try {
            device.setOrientationLeft();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        changeActivityLocale(getActivityInstance(), "us");
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.TextView03)).check(matches(withText("Landscape")));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings)).check(matches(withText("Settings")));
    }

    @Test
    public void testPolishResources() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        changeActivityLocale(getActivityInstance(), "pl");
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.TextView03)).check(matches(withText("Pionowo")));
        try {
            device.setOrientationLeft();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        changeActivityLocale(getActivityInstance(), "pl");
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.TextView03)).check(matches(withText("Poziomo")));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings)).check(matches(withText("Ustawienia")));
    }
}