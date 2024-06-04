package com.github.starnowski.quarkus.fun.liquid.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.regex.Pattern;

public class RegexMatcher implements Matcher {

    public static final String START_TAG = "{{START_REGEXP}}";
    public static final String END_TAG = "{{END_REGEXP}}";
    private final Pattern pattern;
    private final String expected;

    public RegexMatcher(String expected) {
        this.expected = expected;
        String[] phrases = expected.split(Pattern.quote(START_TAG));
        StringBuilder patterBuilder = new StringBuilder();
        int index = 0;
        for (String phrase : phrases) {
            if (index == 0) {
                patterBuilder.append(Pattern.quote(phrase));
            } else {
                int endTagIndex = phrase.indexOf(END_TAG);
                patterBuilder.append(phrase.substring(0, endTagIndex));
                patterBuilder.append(Pattern.quote(phrase.substring(endTagIndex + END_TAG.length(), phrase.length())));
            }
            index++;
        }
        pattern = Pattern.compile(patterBuilder.toString());
    }


    @Override
    public boolean matches(Object o) {
        java.util.regex.Matcher result = pattern.matcher((CharSequence) o);
        return result.matches();
    }

    @Override
    public void describeMismatch(Object o, Description description) {
        description.appendText("Value do not math with pattern: " + pattern.pattern());
    }

    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Value do not math with pattern: " + pattern.pattern());
        description.appendText("Base pattern: " + expected);
    }
}
