package com.cyclelution.RCTZebraBTPrinter;

import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Nullable;

import android.app.Activity;

import android.util.Log;
import android.util.Base64;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.Callback;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;

import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import static com.cyclelution.RCTZebraBTPrinter.RCTZebraBTPrinterPackage.TAG;

@SuppressWarnings("unused")
public class RCTZebraBTPrinterModule extends ReactContextBaseJavaModule {
  private Connection printerConnection;

  public RCTZebraBTPrinterModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @ReactMethod
  public void connect(String userPrinterSerial, Promise promise) {
    try {
      if (printerConnection == null) {
        printerConnection = new BluetoothConnection(userPrinterSerial);
        printerConnection.open();
      }
      promise.resolve(true);
    } catch (ConnectionException e) {
      printerConnection = null;
      promise.reject(e);
    }
  }

  @ReactMethod
  public void printLabel(String cpclCommand, Promise promise) {
    if (printerConnection == null) {
      promise.reject(new Error("Not connected"));
      return;
    }
    try {
      printerConnection.write(cpclCommand.getBytes());
      promise.resolve(true);
    } catch (ConnectionException e) {
      try {
        printerConnection.close();
      } catch (ConnectionException e2) {
        /* do nothing */
      }
      printerConnection = null;
      promise.reject(e);
    }
  }

  @Override
  public String getName() {
    return "RCTZebraBTPrinter";
  }

}
