# Winlytics Android Gradle Library


# Usage


## How to install this library

Add this snippet to your project level gradle:

```javascript
  allprojects {
        repositories {
            maven { url "https://jitpack.io" }
        }
   }
```

And this line to your app level gradle:
```javascript
  implementation 'com.io.winlytics:1.0.0'
```
And to your AndroidManifest.xml file:
```javascript
  <uses-permission android:name="android.permission.INTERNET"></uses-permission>
```

## How to use this library

Just call in your activity or fragment,it will show a Dialog easily
Context can be Activity or Fragment context
```javascript
  Winlytics.createSurvey("replace your survey id here"),context);
```

