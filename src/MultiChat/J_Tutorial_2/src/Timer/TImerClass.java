package Timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bahuu on 08/09/2017.
 */
public class TImerClass {


    public static void main(String[] args){
        Timer timer = new Timer();
        Date date = new Date();
        date.setTime(System.currentTimeMillis()+10000);

        Thread th1 =  new Thread(()->{
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Active");
                    date.setTime(System.currentTimeMillis() + 10000);
                    try {
                        Thread.sleep(100);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, date);
        });
        th1.start();

        while (true){

        }

    }
}
