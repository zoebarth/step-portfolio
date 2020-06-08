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
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
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
  const messagesElement = document.getElementById('comments-container');
  messagesElement.innerHTML = '';
  for (message of messages) {
    messagesElement.appendChild(createListElement(message));
  }
}

/**
 * Creates an <li> element containing text.
 * @param {string} text The text to create list element with
 * @return {Element} a list element
 */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Tells the server to delete all comments and reloads comments sections. */
async function deleteComments() {
  await fetch('/delete-data', {method: 'POST'});
  const messagesElement = document.getElementById('comments-container');
  messagesElement.innerHTML = '';
}
