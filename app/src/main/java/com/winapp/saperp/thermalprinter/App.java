package com.winapp.saperp.thermalprinter;

import android.app.Application;
import android.content.Context;
import android.os.RemoteException;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.InitPrinterCallback;
import com.imin.printer.PrinterHelper;

/**
 * Created by yechao on 2020/3/26/026.
 * Describe :
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        FirebaseApp.initializeApp(this);

        PrinterHelper.getInstance().initPrinterService(this, new InitPrinterCallback() {
            @Override
            public void onConnected() {
                Toast.makeText(App.this, "Printer connected IMIN Test",
                        Toast.LENGTH_SHORT).show();

                PrinterHelper.getInstance().printerSelfChecking(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {
            }
            @Override
            public void onReturnString(String result) throws RemoteException {
            }
            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {
            }
            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {
            }
        });

            }

            @Override
            public void onDisconnected() {

            }
        });


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        PrinterHelper.getInstance().deInitPrinterService(this);
    }

    public static Context getContext() {
        return mContext;
    }


    // Gloabl declaration of variable to use in whole app

    public static boolean activityVisible; // Variable that will check the
    // current activity state

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused
    }

}
