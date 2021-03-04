const functions = require("firebase-functions");
const admin = require('firebase-admin');
const firebase_tools = require('firebase-tools');
admin.initializeApp();


   exports.addMessage = functions.https.onRequest(async (req, res) => {
     // Grab the text parameter.

     const original = req.body.data.text;
     console.log(original);


     // Push the new message into Firestore using the Firebase Admin SDK.
//     const deleteUsers = await admin.firestore().collection('users').doc(original).delete().then(() => {
//        console.log("Document successfully deleted");
//
//     }).catch((err) =>{
//        console.error(err);
//         });


      const user = await admin.auth().deleteUser(original).then(()=>{
      console.log("user successfully deleted");
      }).catch((err)=>{
      console.error(err);
      });

     // Send back a message that we've successfully written the message
                res.json({result: `User deleted`});
   });

   exports.recursiveDelete = functions
     .runWith({
       timeoutSeconds: 540,
       memory: '2GB'
     })
     .https.onCall(async (data, context) => {
       // Only allow admin users to execute this function.


       const path = data.path;
       console.log(
         `User ${context.auth.uid} has requested to delete path ${path}`
       );

       // Run a recursive delete on the given document or collection path.
       // The 'token' must be set in the functions config, and can be generated
       // at the command line by running 'firebase login:ci'.
       await firebase_tools.firestore
         .delete(path, {
           project: process.env.GCLOUD_PROJECT,
           recursive: true,
           yes: true,

         });

       return {
         path: path
       };
     });