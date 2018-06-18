package com.cvvid.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Timeshow
{

    public String timeage(String date)
    {
        String timeage = "";
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date past = format.parse(date);
            Date now = new Date();
            long seconds=TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
//
//          System.out.println(TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + " milliseconds ago");
//          System.out.println(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minutes ago");
//          System.out.println(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
//          System.out.println(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");

            if(seconds<60)
            {
                timeage = seconds+" seconds ago";
                System.out.println(seconds+" seconds ago");
            }
            else if(minutes<60)
            {
                timeage = minutes+" minutes ago";
                System.out.println(minutes+" minutes ago");
            }
            else if(hours<24)
            {
                timeage = hours+" hours ago";
                System.out.println(hours+" hours ago");
            }
            else
            {
                timeage = days+" days ago";
                System.out.println(days+" days ago");
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }

        return timeage;
    }

}