package com.example.modee.setcpufreq;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    //EditText freq;
    TextView currentFreq;
    Spinner cpuFreqSpinner;
    ArrayAdapter<String> adapter;
    //ArrayList<String> cpuFreq;
    String[] cpuFreq = new String[]{};
    String itemSelected;
    String minFreq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //freq = (EditText) findViewById(R.id.freqEditView);
        currentFreq = (TextView) findViewById(R.id.currentFreqTextView);
        cpuFreqSpinner = (Spinner) findViewById(R.id.spinner_cpufreq);
        //cpuFreq = new ArrayList<>();

        String cpuFreqText = getCPUFreq();
        cpuFreq = cpuFreqText.split(" ");
        for (int i = 0; i < cpuFreq.length; i++) {
            System.out.println(cpuFreq[i]);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cpuFreq);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cpuFreqSpinner.setAdapter(adapter);
        //cpuFreqSpinner.setOnClickListener(new SpinnerSelectedListener());
        cpuFreqSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        cpuFreqSpinner.setVisibility(View.VISIBLE);

        minFreq = getMinFreq();

    }

    class SpinnerSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {

            itemSelected = cpuFreq[arg2];
            if (Integer.parseInt(cpuFreq[arg2]) < Integer.parseInt(minFreq))
            {
                Toast.makeText(getBaseContext(),"The chosen CPU frequency should be higher than " + minFreq.toString(),Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(getBaseContext(), "The selected cpu is" + itemSelected, Toast.LENGTH_SHORT).show();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    public String getCPUFreq() {
        String command;
        String commandResult;
        command = "cat sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
        commandResult = runCommand(new String[]{command});
        return commandResult;

    }

    public String getMinFreq(){
        String command = "cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
        String minFreq = runCommand(new String[]{command});
        return minFreq;
    }

    public void changeMode(View view) {

        String command0 = "echo userspace > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
        String command1 = "echo userspace > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor";
        String command2 = "echo userspace > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor";
        String command3 = "echo userspace > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor";
        String command4 = "cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";

        //command = "cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
        //command = "top -n 1 -d 1";
        //command = "cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq";

        String commandResult = runCommand(new String[]{command0,command1,command2,command3, command4});
        Toast.makeText(getBaseContext(), "scaling governor is " + commandResult, Toast.LENGTH_SHORT).show();
    }

    public void setFreq(View view) {
        //String command = "echo "+freq.getText().toString()+" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed";
        //System.out.println("freq: " + freq.getText().toString());
        String command = "echo " + itemSelected + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed";
        //System.out.println("freq: " + itemSelected);
        String command1 ="echo " + itemSelected + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_setspeed";
        String command2 = "echo " + itemSelected + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_setspeed";
        String command3 = "echo " + itemSelected + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_setspeed";

        String command4 = "cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
        String command5 = "cat /sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq";
        String command6 = "cat /sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq";
        String command7 = "cat /sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq";
        String commandResult = runCommand(new String[]{command, command1,command2,command3,command4,command5,command6,command7});
        //String commandResult = runCommand(new String[]{command1,command5});
        //String commandResult = runCommand(new String[]{command_min, command, command2});
        Toast.makeText(getBaseContext(), "scaling freq is " + commandResult, Toast.LENGTH_SHORT).show();
    }

    public void viewCurrentFreq(View view) {
        String command = "cat /sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq";
        String commandResult = runCommand(new String[]{command});
        currentFreq.setText(commandResult);
    }

    public void changeToDefault(View view) {
        String command;
        command = "echo interactive > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
        String command2 = "cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
        String commandResult = runCommand(new String[]{command, command2});
        Toast.makeText(getBaseContext(), "scaling governor is " + commandResult, Toast.LENGTH_SHORT).show();
    }


    private String runCommand(String[] commands) {
        String commandResult = "";
        try {
            Process process;
            process = Runtime.getRuntime().exec("su");
            String line = null;
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            for (int i = 0; i < commands.length; i++) {
                outputStream.writeBytes(commands[i] + "\n");
            }
            outputStream.writeBytes("exit\n");
            outputStream.flush();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            // read the output from the command

            System.out.println("Here is the standard output of the command:\n");

            while ((line = stdInput.readLine()) != null) {
                System.out.println(line);
                commandResult += line;
                //Log.d("output",line);
            }
            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((line = stdError.readLine()) != null) {
                System.out.println(line);
                //Log.d("any errors", s);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return commandResult;
    }
}
