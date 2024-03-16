package com.example.inzynierka;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void loginFragmentTest_navigation_login_forgottenpassword() {
        ViewInteraction materialTextView3 = onView(
                allOf(withId(R.id.Forgotten_Password_TextView), withText("I forgot my password"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3),
                                0)));
        materialTextView3.perform(scrollTo(), click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.back_to_login_button), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                5)));
        materialButton.perform(scrollTo(), click());
    }
    @Test
    public void loginFragmentTest_navigation_login_signup() {
        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.sign_up_TextView), withText("Sign up"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        5),
                                1)));
        materialTextView.perform(scrollTo(), click());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.login_TextView), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        9),
                                1)));
        materialTextView2.perform(scrollTo(), click());
    }
    @Test
    public void loginFragmentTest_login() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTextTextEmailAddressLogin),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                1)));
        appCompatEditText.perform(scrollTo(), replaceText("mir3k.60@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editTextTextPassword),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatEditText2.perform(scrollTo(), replaceText("123456"), closeSoftKeyboard());

        ViewInteraction materialCheckBox = onView(
                allOf(withId(R.id.Remember_me_CheckBox), withText("Remember me"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3),
                                1)));
        materialCheckBox.perform(scrollTo(), click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.login_button), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        materialButton2.perform(scrollTo(), click());
    }
    @Test
    public void loginFragmentTest_loginbutton() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.textView_login), withText("Login"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.textView_login), withText("Login"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        textView8.check(matches(withText("Login")));

    }
    @Test
    public void loginFragmentTest_emailfield(){
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
    }
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
