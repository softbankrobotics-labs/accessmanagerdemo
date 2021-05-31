package com.softbankrobotics.accessmanagerdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity


open class MainActivity : RobotActivity(), RobotLifecycleCallbacks {

    private val TAG="MainActivity"

    private val AUTHENTICATION_CODE = 47

    private val ACCESS_MANAGER_PACKAGE = "com.softbankrobotics.accessmanager"
    private val AUTHENTICATION_ACTIVITY_PACKAGE = "com.softbankrobotics.accessmanager.ui.authentication.AuthenticationActivity"

    private lateinit var sayNotInstalled: Say


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        QiSDK.register(this,this)

        setFragment(MainFragment())
    }

    override fun onStart() {
        Log.i(TAG,"onStart")
        super.onStart()
    }

    override fun onStop() {
        Log.i(TAG,"onStop")
        super.onStop()
    }

    override fun onDestroy() {
        QiSDK.unregister(this,this)
        super.onDestroy()
    }


    //Check if Access Manager is installed or not
    @SuppressLint("QueryPermissionsNeeded")
    fun accessManager(){
        val intent = Intent()
        intent.component = ComponentName(ACCESS_MANAGER_PACKAGE, AUTHENTICATION_ACTIVITY_PACKAGE)
        val list: List<*> = this.packageManager.queryIntentActivities(intent,0)
        if (list.isNotEmpty()) {
            startActivityForResult(intent,AUTHENTICATION_CODE)
        } else {
            sayNotInstalled.async().run()
        }
    }

    /**
     * Deals with the different returned values of the AuthenticationActivity.
     * If RESULT_OK, go to the granted fragment.
     * If RESULT_RECOVERY_SUCCESS,  we go back to this activity.
     * If the user cancel or fail the authentication, go the denied fragment.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "ActivityResult $requestCode $resultCode")
        if (requestCode == AUTHENTICATION_CODE) {
            when(resultCode) {
                Activity.RESULT_OK -> this.setFragment(AccessGranted())
                Activity.RESULT_CANCELED, Activity.RESULT_FIRST_USER ->this.setFragment(AccessDenied())
                else -> this.setFragment(AccessDenied())
            }
        } else {
            this.setFragment(AccessDenied())
        }
    }

    fun setFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.placeholder,fragment)
            commit()
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        Log.i(TAG,"Focus Gained")

        sayNotInstalled=SayBuilder.with(qiContext)
            .withText("Access Manager is not installed")
            .withLocale(Locale(Language.ENGLISH,Region.UNITED_STATES)).build()
        /*
         * NOTE : onFocusGained is also called when receiving a result from Access Manager Intent
         * You have to handle cases where Pepper retrieve the focus
         * You have to rebuild every runnable action (Say, Chat, Animate, ...)
         * Null Check is suggested to rebuild only the necessary actions
         */
    }

    override fun onRobotFocusLost() {
        Log.i(TAG,"Focus Lost")
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.i(TAG, "Focus Refused $reason")
    }
}