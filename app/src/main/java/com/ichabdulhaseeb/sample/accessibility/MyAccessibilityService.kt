package com.ichabdulhaseeb.sample.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.*

class MyAccessibilityService : AccessibilityService() {

    private val info = AccessibilityServiceInfo()

    override fun onInterrupt() {
        Log.d(TAG, "Service: Interrupt")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val source = event!!.source ?: return
        val allNodes = flattenTree(source)
        val result = getTextFromSource(allNodes)

        Log.d(TAG, "TextFound: $result")
    }

    override fun onServiceConnected() {
        info.apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK

            // If you only want this service to work with specific applications, set their
            // package names here. Otherwise, when the service is activated, it will listen
            // to events from all applications.
            //packageNames = arrayOf("com.soundcloud.android")

            // Set the type of feedback your service will provide.
           // feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN

            // Default services are invoked only if no package-specific ones are present
            // for the type of AccessibilityEvent generated. This service *is*
            // application-specific, so the flag isn't necessary. If this was a
            // general-purpose service, it would be worth considering setting the
            // DEFAULT flag.

             //flags = AccessibilityServiceInfo.DEFAULT;

            notificationTimeout = 100
        }

        this.serviceInfo = info

        Log.d(TAG, "Service: Connected")

    }

    private fun flattenTree(rootNode: AccessibilityNodeInfo): List<AccessibilityNodeInfo> {
        val allNodes: MutableList<AccessibilityNodeInfo> = ArrayList()
        val q: Queue<AccessibilityNodeInfo> = LinkedList()
        q.add(rootNode)
        while (!q.isEmpty()) {
            try {
                val node: AccessibilityNodeInfo = q.poll() as AccessibilityNodeInfo
                allNodes.add(node)
                for (i in 0 until node.childCount) {
                    val child = node.getChild(i)
                    q.add(child)
                }
            }
            catch (e:Exception){

            }
        }
        return allNodes
    }

    private fun getTextFromSource(nodes:List<AccessibilityNodeInfo>): String {
        var result = String()
        for(n in nodes){
            try{
                if(n.className.equals("android.widget.TextView")) {
                    result = result.plus(n.text)
                    result = result.plus("\n")
                }
        }
            catch (e:Exception){
                e.printStackTrace()

            }
        }
        return result
    }

    companion object{
        var TAG = "accessibility_service_log_tag"
    }

}