const functions = require("firebase-functions");
const admin = require('firebase-admin');
const firebase_tools = require('firebase-tools');
const express = require('express');
const {RtcTokenBuilder, RtcRole} = require('agora-access-token');

const PORT = 8080;
const APP_ID = process.env.APP_ID;
const APP_CERTIFICATE = process.env.APP_CERTIFICATE;

admin.initializeApp();


   exports.addMessage = functions.https.onRequest(async (req, res) => {
     // Grab the text parameter.

     const original = req.body.data.text;
     console.log(original);



      const user = await admin.auth().deleteUser(original).then(()=>{
      console.log("user successfully deleted");
      }).catch((err)=>{
      console.error(err);
      });

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




       exports.onCreateActivityFeedItem = functions.firestore
           .document('/messageNotification/{userId}/message/{messageId}')
           .onCreate(async(snapshot, context) => {
                    console.log("Activity feed item created", snapshot.data());

                    //1) get the user connected to the feed

                    const userId = context.params.userId;
                    const userRef = admin.firestore().doc(`users/${userId}`);

                    const doc = await userRef.get();

                    //2 once we have the user, check if they have notification token, send notification if they have a token
                    const androidNotificationToken = doc.data().androidNotificationToken;
                    console.log(androidNotificationToken);
                    const createdActivityFeedItem = snapshot.data();
                    if (androidNotificationToken) {
                      //send notification
                      sendNotification(androidNotificationToken, createdActivityFeedItem);
                    } else {
                      console.log("No token for user, cannot user notification");
                    }

                    function sendNotification(androidNotificationToken, activityFeedItem) {
                      let body;


                      //switch body value based off of notification type
                      body = `${activityFeedItem.nickname}:  ${activityFeedItem.message}`;
                      console.log(body);

                      //create message for push notifications
                      const message = {
                        notification: { body },
                        token: androidNotificationToken,
                        data: { recipient: userId },
                      };

                      //send message with admin.messaging()
                      admin
                        .messaging()
                        .send(message)
                        .then((response) => {
                          console.log("Successfully sent message", response);
                        })
                        .catch((err) => {
                          console.log("Error sending message", err);
                        });
                    }
           });


    const app = express();



    const nocache = (req, resp, next) => {
     resp.header('Cache-Control', 'private, no-cache, no-store, must-revalidate');
     resp.header('Expires', '-1');
     resp.header('Pragma', 'no-cache');
     next();
    }


    exports.generateToken = functions.https.onRequest(async(req, resp) => {
     // set response header
     resp.header('Acess-Control-Allow-Origin', '*');
     // get channel name
     const channelName = req.body.data.channelName;
     if (!channelName) {
       return resp.status(500).json({ 'error': 'channel is required' });
     }
     // get uid
//     let uid = req.query.uid;
//     if(!uid || uid == '') {
//       uid = 0;
//     }
     uid=0;
     // get role
     let role = RtcRole.SUBSCRIBER;
     if (req.query.role == 'publisher') {
       role = RtcRole.PUBLISHER;
     }
     // get the expire time
     let expireTime = req.query.expireTime;
     if (!expireTime || expireTime == '') {
       expireTime = 3600;
     } else {
       expireTime = parseInt(expireTime, 10);
     }
     // calculate privilege expire time
     const currentTime = Math.floor(Date.now() / 1000);
     const privilegeExpireTime = currentTime + expireTime;
     // build the token
     const token = RtcTokenBuilder.buildTokenWithUid(APP_ID, APP_CERTIFICATE, channelName, uid, role, privilegeExpireTime);
     // return the token
     return resp.json({ 'token': token });
    });


