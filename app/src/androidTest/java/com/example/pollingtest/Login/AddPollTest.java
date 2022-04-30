package com.example.pollingtest.Login;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.pollingtest.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddPollTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void addPollTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.login_email),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("fab@fab.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.login_password),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.loginBtn), withText("Log In"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton.perform(click());
        SystemClock.sleep(3000);
        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.grocery_polls_btn), withText("polls"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.idFABAddPoll),
                        childAtPosition(
                                allOf(withId(R.id.idRLHome),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());
        SystemClock.sleep(1000);

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.idEdtPollName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILPollName),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText.perform(replaceText("Test Poll"), closeSoftKeyboard());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.idEdtPollImageLink),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILPollImageLink),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("https://www.ncirl.ie/portals/0/Images/650x366-Cards-Teasers-Inners/img-research-1.jpg"), closeSoftKeyboard());

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.idEdtPollDescription),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILPollDescription),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText4.perform(replaceText("Favourite lecturer"), closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.idEdtOption1),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILVoteOption1),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText5.perform(replaceText("Josh"), closeSoftKeyboard());

        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.idEdtOption2),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILVoteOption2),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText6.perform(replaceText("Josh"), closeSoftKeyboard());

        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.idEdtOption3),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILVoteOption3),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText7.perform(replaceText("Josh"), closeSoftKeyboard());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.idBtnAdd), withText("Add Your poll"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                6)));
        materialButton3.perform(scrollTo(), click());
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
