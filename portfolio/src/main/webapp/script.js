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
  const response = await fetch('/data');
  const messages = await response.json();
  const messagesElement = document.getElementById('content-container');
  messagesElement.innerHTML = '';
  messagesElement.appendChild(createListElement(messages[0]));
  messagesElement.appendChild(createListElement(messages[1]));
  messagesElement.appendChild(createListElement(messages[2]));
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
