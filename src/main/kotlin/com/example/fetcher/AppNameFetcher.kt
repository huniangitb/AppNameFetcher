package com.example.fetcher

import android.content.Context
import android.content.pm.PackageManager
import kotlin.system.exitProcess

/**
 * A command-line tool to fetch an application's name by its package name.
 * Designed to be executed on an Android device via 'app_process'.
 *
 * Usage on device (requires root):
 * su -c 'CLASSPATH=/path/to/your.jar app_process / 4.AppNameFetcher <package_name>'
 */
object AppNameFetcher {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            System.err.println("Usage: AppNameFetcher <package_name>")
            exitProcess(1)
        }

        val packageName = args[0]

        try {
            // Get system context via reflection, essential for app_process environment
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val systemMainMethod = activityThreadClass.getMethod("systemMain")
            val activityThread = systemMainMethod.invoke(null)
            val getSystemContextMethod = activityThreadClass.getMethod("getSystemContext")
            val context = getSystemContextMethod.invoke(activityThread) as Context

            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val appName = pm.getApplicationLabel(appInfo)

            // Success: print the app name to standard output
            println(appName)
            exitProcess(0)

        } catch (e: PackageManager.NameNotFoundException) {
            System.err.println("Error: Package not found: $packageName")
            exitProcess(2)
        } catch (e: Exception) {
            System.err.println("An unexpected error occurred: ${e.message}")
            e.printStackTrace(System.err)
            exitProcess(3)
        }
    }
}
