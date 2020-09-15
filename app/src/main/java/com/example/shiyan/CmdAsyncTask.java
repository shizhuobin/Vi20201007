package com.example.shiyan;

import android.os.AsyncTask;
import android.util.Log;

public class CmdAsyncTask extends AsyncTask {

    int hz;

    public CmdAsyncTask(int hz)
    {
        this.hz=hz;
    }

    @Override
    protected Object doInBackground(Object[] objects){
//        RootCmd.adjustFrequencyTwiceInTime("960000",250,"1036800");

        SetCPU setCPU = SetCPU.getInstance();
        //setCPU.runAllCores("echo 300000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
        setCPU.runAllCores(hz);
        //System.out.println("echo "+String.valueOf(hz)+"000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
        Log.d("cpuhz","echo "+String.valueOf(hz)+"000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
        return 0;
    }
}