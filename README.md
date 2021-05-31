# Access Manager Demo

Article and code updated on 31/05/21

This project is a demonstration of how the Access Manager can be used within an application.

Access Manager provides the ability to restrict access to various part of an application, or even the whole application itself.

This access is restricted using a digit code defined when launching Access Manager for the first time.

Tested under NAOqi version 2.9.5


## 1.Prerequisites

1. Go to the Access Manager page on Command Center Store, then add the application to your missions by clicking the "Add to My Missions" button.

2. Then in the Client Tab on Command Center, click on the robot you want and add Access Manager version 2.1.2 to your robot.

3. On your Pepper, start the "Update SoftBank Robotics applications" app then Check update now.

4. Start Access Manager, then create a Digit Code. Write this Digit Code somewhere safe and the associated Recovery Code too (that will be displayed after you clicked Save Digit Code). If you lose both codes, you can get the recovery code on the robot's page on Command Center.

5. You can now create applications using Access Manager digit code.


## 2.Set up

To restrict an access, just write down this piece of code (kotlin) right before opening the activity/fragment/application (set your own AUTHENTICATION_CODE) : 

```
val intent = Intent()
intent.component = ComponentName(ACCESS_MANAGER_PACKAGE, AUTHENTICATION_ACTIVITY_PACKAGE)
val list: List<*> = this.packageManager.queryIntentActivities(intent,0)
if (list.isNotEmpty()) {
    startActivityForResult(intent,AUTHENTICATION_CODE)
}

```

with :

```
private val ACCESS_MANAGER_PACKAGE = "com.softbankrobotics.accessmanager"
    private val AUTHENTICATION_ACTIVITY_PACKAGE = "com.softbankrobotics.accessmanager.ui.authentication.AuthenticationActivity"

```

Then, in onActivityResult, handle the various cases, depending on the result, and grant the restricted access or deny it (-1 (RESULT_OK) if the code is correct, 0 otherwise).
In this demo, if the code is correct, we set the fragment Granted, and the fragment Denied otherwise.


## 3.Notes

When calling the Intent Access Manager, Pepper will lose the focus of the demo app. Therefore, if you decide to open Access Manager and then go back to the application, you will have to handle the fact that Pepper will gain the focus again. Moreover, you will need to rebuild all actions which can be run(like Chat, Say, Animate,...) as they can only be run using the current qiContext.
We suggest you to check if objects (Topic, QiChatBot, Animation, ...) are null in order to build only the necessary ones to avoid loosing time when focus is gained.


## 4.License

This project is licensed under the BSD 3-Clause "New" or "Revised" License - see the [COPYING](COPYING.md) file for details.



