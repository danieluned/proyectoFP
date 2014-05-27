# webrtc
WebRTC is a realtime standard for HTML5 (supported by a variety of modern browsers). This demo uses websockets, on cloudbees, to set up your own html5 based video/voice and text chat.

#  webrtc.io clickstart

This clickstart is based on the <a href="https://github.com/webRTC/webrtc.io-demo">webrtc.io-demo</a> and the CloudBees node.js support.
The Node.js app runs on CloudBees with continuous deployment. Jenkins runs tests via npm and deploys.

package.json is used to specify what packages are needed - main.js is the default server app
(you can set main_js via bees config:set to override it to something else)

Who wouldn't want their own video/text chat: 

<a href="https://grandcentral.cloudbees.com/?CB_clickstart=https://raw.github.com/michaelneale/webrtc.io-clickstart/master/clickstart.json"><img src="https://d3ko533tu1ozfq.cloudfront.net/clickstart/deployInstantly.png"/></a>


# To run manually locally

0. Install Node.js
1. Clone this repo
2. cd app
2. npm install -d
3. node main.js

