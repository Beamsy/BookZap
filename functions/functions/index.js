const functions = require('firebase-functions');

 // Create and Deploy Your First Cloud Functions
 // https://firebase.google.com/docs/functions/write-firebase-functions

//    exports.helloWorld = functions.https.onRequest((request, response) => {
//    response.send("Hello from BookZap!");
//    });

    exports.addBook = functions.firestore.document('books/{isbn}')
        .onCreate(event => {
            var newBook = event.data.data();
            
            if (newBook.ISBN == null || newBook.coverUriString == null || newBook.pageCount == null || newBook.title == null) {
                return event.data.ref.set({
                    'functioned': true
                });
            }
    });