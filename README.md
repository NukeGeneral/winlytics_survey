<p align="center"> 
<img src="http://winlytics.io/assets/images/logo.png">
</p>

<h2 align="center">Winlytics.io Android Gradle Library</h2>

### How to install this library

Add this snippet to your project level gradle:

```gradle
  allprojects {
        repositories {
            maven { url "https://jitpack.io" }
        }
   }
```

And this line to your app level gradle:
```gradle
  compile 'com.github.panjur:winlytics_survey:0.2.1-beta'
```
And to your AndroidManifest.xml file:
```xml
  <uses-permission android:name="android.permission.INTERNET"></uses-permission>
```

### How to use this library

In your calling activity or fragment: 

Override onResume and onPause method like example 

```java
    @Override
    protected void onResume() {
        super.onResume();
        Winlytics.onResume();
    }
```
```java
    @Override
    protected void onPause() {
        super.onPause();
        Winlytics.onPause();
    }
```

And call method below,it will show a Dialog easily.

Required Parameters

     @param surveyId Winlytics survey ID provided in registration
     @param userId To identify users from each other(It should be unique for each user)
     @param userName Optional parameter to see user name in dashboard,it can be empty string
     @param email Optional parameter to see emails in dashboard,it can be empty string
     @param categoryTags This makes easier to analyse dashboard,it's like filter or activity/fragment name
     @param context This is activity or fragment context
     @param isTest is this a test request or not(default false)

```java
  Winlytics.createSurvey(/*surveyId*/,/*userId*/,/*userName*/,/*email*/,/*categoryTags*/,/*context*/,/*isTest*/);
```

