package com.example.inzynierka;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.textView_login), withText("Login"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction editText = onView(
                allOf(withId(R.id.editTextTextEmailAddressLogin), withText("E-mail"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.editTextTextEmailAddressLogin), withText("E-mail"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        editText2.check(matches(withText("E-mail")));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.editTextTextPassword), withText("Password"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        editText3.check(matches(isDisplayed()));

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.editTextTextPassword), withText("Password"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        editText4.check(matches(withText("Password")));

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.Remember_me_CheckBox), withText("Remember me"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        checkBox.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.Forgotten_Password_TextView), withText("I forgot my password"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("I forgot my password")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.Forgotten_Password_TextView), withText("I forgot my password"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.login_button), withText("Login"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.dont_hava_account_textView), withText("Don't have account?"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView4.check(matches(withText("Don't have account?")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.dont_hava_account_textView), withText("Don't have account?"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.sign_up_TextView), withText("Sign up"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView6.check(matches(withText("Sign up")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.sign_up_TextView), withText("Sign up"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView7.check(matches(isDisplayed()));

        ViewInteraction view = onView(
                allOf(withId(android.R.id.navigationBarBackground),
                        withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        ViewInteraction view2 = onView(
                allOf(withId(android.R.id.statusBarBackground),
                        withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)),
                        isDisplayed()));
        view2.check(matches(isDisplayed()));

        ViewInteraction frameLayout = onView(
                allOf(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class), isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction linearLayout = onView(
                allOf(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction frameLayout2 = onView(
                allOf(withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        frameLayout2.check(matches(isDisplayed()));

        ViewInteraction linearLayout2 = onView(
                allOf(withId(androidx.appcompat.R.id.action_bar_root),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        linearLayout2.check(matches(isDisplayed()));

        ViewInteraction frameLayout3 = onView(
                allOf(withId(android.R.id.content),
                        withParent(allOf(withId(androidx.appcompat.R.id.action_bar_root),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()));
        frameLayout3.check(matches(isDisplayed()));

        ViewInteraction viewGroup = onView(
                allOf(withParent(allOf(withId(android.R.id.content),
                                withParent(withId(androidx.appcompat.R.id.action_bar_root)))),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));

        ViewInteraction frameLayout4 = onView(
                allOf(withId(R.id.fragmentContainerView),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        frameLayout4.check(matches(isDisplayed()));

        ViewInteraction frameLayout5 = onView(
                allOf(withId(R.id.fragmentContainerView),
                        withParent(allOf(withId(R.id.fragmentContainerView),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        frameLayout5.check(matches(isDisplayed()));

        ViewInteraction frameLayout6 = onView(
                allOf(withParent(allOf(withId(R.id.fragmentContainerView),
                                withParent(withId(R.id.fragmentContainerView)))),
                        isDisplayed()));
        frameLayout6.check(matches(isDisplayed()));

        ViewInteraction frameLayout7 = onView(
                allOf(withParent(withParent(withId(R.id.fragmentContainerView))),
                        isDisplayed()));
        frameLayout7.check(matches(isDisplayed()));

        ViewInteraction scrollView = onView(
                allOf(withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        scrollView.check(matches(isDisplayed()));

        ViewInteraction linearLayout3 = onView(
                allOf(withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        linearLayout3.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.textView_login), withText("Login"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        textView8.check(matches(withText("Login")));
    }
}
