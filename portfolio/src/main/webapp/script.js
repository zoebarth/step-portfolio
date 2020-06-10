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
    messagesEl.append(createListElement(message));
  }
}

/**
 * Creates a <div> element containing author and comment.
 * @param {string} text The text to create list element with
 * @return {Element} a div element
 */
function createListElement(text) {
  return $(html`
    <div class="comment-box">
      <p class="comment-name">test@example.com</p>
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
    $('#logout-container').html(
      `<p>You are currently logged in as ${login.userEmail}. Click <a href=${login.logoutUrl}>here</a> to sign out.<\p>`
    );
  } else {
    $('#login-here').show();
    $('#login-container').html(
      `<a href=${login.loginUrl} class="btn btn-dark">Login Here</a>`
    );
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

$('#comment-form').submit(handleSubmit);
