# SpeechReco 
## Multiple change in AndroidManifest.xml, build.gradle, and activity_main.xml.
  Add user permission to access the Internet in AndroidManifest.xml. Add OkHttp3 implementation in gradle file. Add image button and a textview in activity_main.xml.
#### Use ic_mic.xml, ic_mic_black.xml as image resources. in res directory, add network_security_config.xml too access api. 
## In MainActiity.java, function explanation:
  Initialize TextToSpeech function duiring the intialization.
  Once click the image button, app will call google speech recognition funtion. From speech to text, and display text in textView.
  Use OkHttpClient to create web request. Create JSON file by the text just transfered. Then create request with the JSON object, headers, and url link. Use Client.newCall() function to post request to our backend API, and get the answer back. 
  speak() function: speak any String in the textView, can be modified to speak input string.
  
### Issue for now:
    Unable to display answer in textView. Can anly receive the answer in a try catch sentence. Unable to pull the answer text or send to textView within try-catch. The error said that cannot display in the main thread, so guess need multithread? 
