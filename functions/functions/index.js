const functions = require('firebase-functions');
const request = require('request-promise');


const WEBHOOK_URL = 'https://requestb.in/vq2568vq';
 // Create and Deploy Your First Cloud Functions
 // https://firebase.google.com/docs/functions/write-firebase-functions

//    exports.helloWorld = functions.https.onRequest((request, response) => {
//    response.send("Hello from BookZap!");
//    });

    exports.addBook = functions.firestore.document('books/{isbn}')
        .onCreate(event => {
            var newBook = event.data.data();
            
            (newBook.ISBN == null || newBook.coverUriString == null || newBook.pageCount == null || newBook.title == null || newBook.author == null) {
                return request({
                    uri: WEBHOOK_URL,
                    method: 'POST',
                    json: true,
                    body: event.data.id,
                    resolveWithFullResponse: true,
                }).then((response) => {
                    if (response.statusCode >= 400) {
                      throw new Error(`HTTP Error: ${response.statusCode}`);
                    }

                });
            }
    });