package com.rn.tool.gcalimporter.testutils.matcher;

import com.google.api.services.calendar.model.EventDateTime;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class Same extends BaseMatcher<EventDateTime> {

  @Override
  public boolean matches(final Object item) {
    return false;
  }

  @Override
  public void describeTo(final Description description) {
    // TODO Auto-generated method stub

  }

}
