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

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //List of unavailable times
    Collection<TimeRange> unavailable = new ArrayList<TimeRange>();
    //List of available times
    Collection<TimeRange> available = new ArrayList<TimeRange>();

    //Meeting info
    Collection<String> meetingAttendees = request.getAttendees();
    long meetingDuration = request.getDuration();

    //Loop through events and if attendees match, then add range to unavailable times
    for (Event e: events) {
      if (!Collections.disjoint(meetingAttendees, e.getAttendees())) {
        unavailable.add(e.getWhen());
      }
    }
    
    //If unavailable is empty, all day will be free
    if (unavailable.isEmpty()) {
      available.add(TimeRange.fromStartEnd(0, 1440, false));
    }
    
    return available;
  }
}
