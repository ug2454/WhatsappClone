const functions = require("firebase-functions");
const admin = require('firebase-admin');
const client = require('firebase-tools');
admin.initializeApp();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

   exports.addMessage = functions.https.onRequest(async (req, res) => {
     // Grab the text parameter.

     const original = req.body.data.text;
     console.log(original);
//     if (!(typeof original === 'string') || original.length === 0) {
//       // Throwing an HttpsError so that the client gets the error details.
//       throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
//           'one arguments "text" containing the message text to add.');
//     }
  await client.firestore
            .delete("/message/${original}", {
               project: process.env.GCLOUD_PROJECT,
             recursive: true,
             yes: true
             });
     // Push the new message into Firestore using the Firebase Admin SDK.
     const deleteUsers = await admin.firestore().collection('users').doc(original).delete().then(() => {
        console.log("Document successfully deleted");

     }).catch((err) =>{
        console.error(err);
         });

//     const deleteMessages = await admin.firestore().collection('message').doc(original).delete().then(() => {
//                   console.log("Document successfully deleted");
//         }).catch((err) =>{
//                   console.error(err);
//                   })

      const user = await admin.auth().deleteUser(original).then(()=>{
      console.log("user successfully deleted");
      }).catch((err)=>{
      console.error(err);
      });

      // delete sub collection





     // Send back a message that we've successfully written the message
                res.json({result: `User deleted`});
   });