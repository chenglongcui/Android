package com.example.android.camera2video;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenglongCUI on 16/9/26.
 */

public class FileManagement {
    private String batteryVoltage;
    private String batteryPercentage;
    private String batteryCurrentNow;
    private String batteryChargingState;
    private String batteryChargeCounter;

    public String ReadFile(String strFilePath) {
        String path = strFilePath;
        String content = "";
        File file = new File(path);
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputReader = new InputStreamReader(instream);
                    BufferedReader buffReader = new BufferedReader(inputReader);
                    String line;
                    while ((line = buffReader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }

    public void readFileFromChip() {
        batteryChargeCounter = ReadFile("/sys/class/power_supply/max170xx_battery/charge_counter_ext");
        batteryChargeCounter = batteryChargeCounter.replace("\n", "");
        batteryVoltage = ReadFile("/sys/class/power_supply/max170xx_battery/voltage_now");
        batteryVoltage = batteryVoltage.replace("\n", "");
        batteryCurrentNow = ReadFile("/sys/class/power_supply/max170xx_battery/current_now");
        batteryCurrentNow = batteryCurrentNow.replace("\n", "");
    }

    public void writeToFile(Context context) {
        String baseFolder;
        // check if external storage is available
        String data = "";
        Date timeInMillis = new Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("HH:mm:ss:SSSS");
        DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        data += System.currentTimeMillis() + ",";
        data += df.format(timeInMillis) + ",";
        data += batteryVoltage + ",";
        data += batteryCurrentNow + ",";
        data += batteryChargeCounter + "\n";
        //System.out.println(data);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            baseFolder = context.getExternalFilesDir(null).getAbsolutePath();
        }
        // use internal instead
        else {
            baseFolder = context.getFilesDir().getAbsolutePath();
        }
        File file = new File(baseFolder + " Camera Experiment.csv");
        if (!file.exists()) {
            data = "System milliseconds," + "time," + "voltage_now," + "current_now," +
                    "charge_counter," + "\n" + data;
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file, true);
            Log.d("file", context.getFilesDir().getAbsolutePath());
            fos.write(data.getBytes());
            fos.close();
        } catch (
                IOException e
                )

        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
