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

import java.util.*;

/** Class for finding meeting times
 * @author Zoe Barth
 * @version 1.0
 */
public final class FindMeetingQuery {
  
  /** Returns a collection of available times for a meeting, given a collection of events and a meeting request.
   * @param events the collection of events
   * @param request the meeting request
   * @return the collection of available times
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> unavailable = new ArrayList<TimeRange>();
    List<TimeRange> optionalUnavailable = new ArrayList<TimeRange>();
    List<TimeRange> available = new ArrayList<TimeRange>();

    Collection<String> meetingAttendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    long meetingDuration = request.getDuration();

    // If meeting duration is longer than a day, return an empty list
    if (meetingDuration > TimeRange.END_OF_DAY) {
      return available;
    }

    // Loop through events and if attendees match, then add range to unavailable times
    for (Event e: events) {
      if (!Collections.disjoint(meetingAttendees, e.getAttendees())) {
        unavailable.add(e.getWhen());
      } else if (!Collections.disjoint(optionalAttendees, e.getAttendees())) {
        optionalUnavailable.add(e.getWhen());
      }
    }
    
    // Sort events by start time and add available times to list
    Collections.sort(unavailable, TimeRange.ORDER_BY_START);
    int start = TimeRange.START_OF_DAY;
    for (TimeRange time : unavailable) {
      if (start + meetingDuration <= time.start()) {
        available.add(TimeRange.fromStartEnd(start, time.start(), false));
      }
      // Check end time to account for nested events
      if (time.end() > start) {
        start = time.end();
      }
    }

    // Add available time after all evenets
    if (start + meetingDuration <= TimeRange.END_OF_DAY) {
      available.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
    }
    
    return available;
  }
}
