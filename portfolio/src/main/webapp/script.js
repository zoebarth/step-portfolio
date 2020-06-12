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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings = [
    'I have lived in the same house my whole life!',
    'I hate tomatoes!',
    'I do aerial silks!',
    'I studied abroad twice!',
    'I took the picture above in New Zealand!',
    'I went bunjee jumping and skydiving on the same day!',
    'The picture to the right is from Barcelona!',
  ];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  $('#greeting-container').text(greeting);
}

/**
 * Fetches data from /data and displays it as html on index page.
 */
async function fetchContent() {
  const limit = document.getElementById('limit').value;
  let response;
  if (limit === 'all') {
    response = await fetch('/data');
  } else {
    response = await fetch(`/data?limit=${limit}`);
  }
  const messages = await response.json();
  const messagesEl = $('#comments-container');
  messagesEl.empty();
  for (message of messages) {
    messagesEl.append(createListElement(message.author, message.text));
  }
}

/**
 * Creates a <div> element containing author and comment.
 * @param {string} author The author of the comment
 * @param {string} text The text of the comment
 * @return {Element} a div element
 */
function createListElement(author, text) {
  return $(`
    <div class="comment-box">
      <p class="comment-name">${author}</p>
      <p>${text}</p>
    </div>
  `);
}

/** Tells the server to delete all comments and reloads comments sections. */
async function deleteComments() {
  await fetch('/delete-data', { method: 'POST' });
  const messagesElement = document.getElementById('comments-container');
  messagesElement.innerHTML = '';
}

/** Creates an a element that allows user to login or logout */
async function loginStatus() {
  const response = await fetch('/login');
  const login = await response.json();
  if (login.loggedIn === true) {
    $('#drop-a-comment').show();
    $('#comment-form').show();
    $('#logout-container').html(`
      <p>
        You are currently logged in as ${login.userEmail}. Click
        <a href=${login.logoutUrl}>here</a> to sign out.
      </p>
    `);
  } else {
    $('#login-here').show();
    $('#login-container').html(`
      <a href=${login.loginUrl} class="btn btn-dark">Login Here</a>
    `);
  }
}

/** Prevents page from refreshing when submitting comments */
function handleSubmit(e) {
  e.preventDefault();
  $.ajax({
    type: 'POST',
    url: '/data',
    data: $(this).serialize(),
    success: fetchContent,
  });
  $(this).find('input,textarea').val('');
}

/** Creates a map and adds it to the page. */
function createMap() {
  const map = new google.maps.Map($('#map')[0], {
    center: { lat: 41.8719, lng: 12.5674 },
    zoom: 2,
    mapTypeId: 'hybrid',
  });

  const places = [
    [31.0, -100.0, 'Texas'],
    [36.084621, -96.921387, 'Oklahoma'],
    [36.778259, -119.417931, 'California'],
    [39.41922, -111.950684, 'Utah'],
    [44.5, -89.5, 'Wisconsin'],
    [44.182205, -84.506836, 'Michigan'],
    [32.157435, -82.907123, 'Georgia'],
    [35.759573, -79.0193, 'North Carolina'],
    [37.431573, -78.656894, 'Virginia'],
    [40.058324, -74.405661, 'New Jersey'],
    [43.299428, -74.217933, 'New York'],
    [42.407211, -71.382437, 'Massachusetts'],
    [41.603221, -73.087749, 'Connecticut'],
    [41.203322, -77.194525, 'Pennsylvania'],
    [27.664827, -81.515754, 'Florida'],
    [35.517491, -86.580447, 'Tennessee'],
    [33.836081, -81.163725, 'South Carolina'],
    [40.463667, -3.74922, 'Spain'],
    [46.227638, 2.213749, 'France'],
    [39.399872, -8.224454, 'Portugal'],
    [47.162494, 19.503304, 'Hungary'],
    [49.817492, 15.472962, 'Czech Republic'],
    [39.074208, 21.824312, 'Greece'],
    [-40.900557, 174.885971, 'New Zealand'],
    [-25.274398, 133.775136, 'Australia'],
    [23.634501, -102.552784, 'Mexico'],
    [56.130366, -106.346771, 'Canada'],
    [9.748917, -83.753428, 'Costa Rica'],
  ];

  for (arr of places) {
    const marker = new google.maps.Marker({
      position: new google.maps.LatLng(arr[0], arr[1]),
      map: map,
      title: arr[2],
    });
  }
}

$('#comment-form').submit(handleSubmit);
$(document).ready(() => {
  fetchContent();
  loginStatus();
  createMap();
});
