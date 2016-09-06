package com.kaust.cs.Tools;
import java.util.Date;

/**
 * Created by yangq0a on 16/8/31.
 */
public class TimeCost {

    public static long getTime(){
        long startTime = System.currentTimeMillis();
        return startTime;
    }

    public static long getTimeCost(long startTime, long EndTime){
        long timeCost = (EndTime - startTime);
        return timeCost;
    }
}
