// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Class for finding meeting times
 * @author Zoe Barth
 * @version 1.0
 */
public final class FindMeetingQuery {
  /**
   * Returns a collection of available times for a meeting, given a collection of events and a
   * meeting request.
   * @param events the collection of events
   * @param request the meeting request
   * @return the collection of available times
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // These variables only include non-optional attendees.
    List<TimeRange> unavailable = new ArrayList<TimeRange>();
    List<TimeRange> available = new ArrayList<TimeRange>();

    // These variables include both optional and non-optional attendees.
    List<TimeRange> optionalUnavailable = new ArrayList<TimeRange>();
    List<TimeRange> optionalAvailable = new ArrayList<TimeRange>();

    Collection<String> meetingAttendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    long meetingDuration = request.getDuration();

    // If meeting duration is longer than a day, return an empty list.
    if (meetingDuration > TimeRange.END_OF_DAY) {
      return available;
    }

    // Loop through events and if attendees match, then add range to unavailable times for both
    // optional and non-optional.
    for (Event e : events) {
      if (!Collections.disjoint(meetingAttendees, e.getAttendees())) {
        unavailable.add(e.getWhen());
        optionalUnavailable.add(e.getWhen());
      } else if (!Collections.disjoint(optionalAttendees, e.getAttendees())) {
        // If only optional attendees are invited to the event, add to the optional list.
        optionalUnavailable.add(e.getWhen());
      }
    }

    // First operation is with both optional and non-optional attendees.
    findAvailableTimes(optionalAvailable, optionalUnavailable, meetingDuration);

    // If there are times available with optional attendees or the meeting has no non-optional
    // attendees, return.
    if (!optionalAvailable.isEmpty() || meetingAttendees.isEmpty()) {
      return optionalAvailable;
    } else {
      // If there is no suitable time with optional attendees, try only with non-optional attendees.
      findAvailableTimes(available, unavailable, meetingDuration);
      return available;
    }
  }

  /**
   * Private helper method to find available times for a meeting given list of unavailable times and
   * meeting duration
   * @param available the list available times will be added to
   * @param unavailable the list of unavailable times
   * @param meetingDuration the length of the meeting
   */
  private void findAvailableTimes(
      List<TimeRange> available, List<TimeRange> unavailable, long meetingDuration) {
    // Sort events by start time and add available times to list.
    Collections.sort(unavailable, TimeRange.ORDER_BY_START);
    int start = TimeRange.START_OF_DAY;
    for (TimeRange time : unavailable) {
      if (start + meetingDuration <= time.start()) {
        available.add(TimeRange.fromStartEnd(start, time.start(), false));
      }
      // Check end time to account for when an event has an earlier start time but later end time.
      if (time.end() > start) {
        start = time.end();
      }
    }
    // Add available time after all evenets.
    if (start + meetingDuration <= TimeRange.END_OF_DAY) {
      available.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
    }
  }
}
