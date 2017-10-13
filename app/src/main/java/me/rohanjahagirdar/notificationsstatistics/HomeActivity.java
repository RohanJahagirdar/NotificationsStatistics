package me.rohanjahagirdar.notificationsstatistics;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private TextView txtView;
    //private NotificationReceiver nReceiver;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        txtView = (TextView) findViewById(R.id.textView);
         context = this;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(nReceiver);
    }


    public void buttonClicked(View v){

        if(v.getId() == R.id.btnCreateNotify){
            NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
            ncomp.setContentTitle("My Notification");
            ncomp.setContentText("Notification Listener Service Example");
            ncomp.setTicker("Notification Listener Service Example");
            ncomp.setSmallIcon(R.drawable.ic_launcher);
            ncomp.setAutoCancel(true);
            nManager.notify((int)System.currentTimeMillis(),ncomp.build());
        }
        else if(v.getId() == R.id.btnClearNotify){
            Intent i = new Intent("me.rohanjahagirdar.notificationsstatistics.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command","clearall");
            sendBroadcast(i);
        }
        else if(v.getId() == R.id.btnListNotify){
            Intent i = new Intent("me.rohanjahagirdar.notificationsstatistics.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command","list");
            sendBroadcast(i);
        }
    }


    class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") + "\n" +
                    txtView.getText() + intent.getStringExtra("notification_event_description") + "\n";
            txtView.setText(temp);
        }
    }

    public class NotificationsListener extends NotificationListenerService {

        SharedPreferences sharedPref;

        private String TAG = this.getClass().getSimpleName();
        private NLServiceReceiver nlservicereciver;

        @Override
        public void onCreate() {
            super.onCreate();
            nlservicereciver = new NLServiceReceiver();
            sharedPref = ((HomeActivity)context).getPreferences(Context.MODE_PRIVATE);
            IntentFilter filter = new IntentFilter();
            //filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            registerReceiver(nlservicereciver,filter);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(nlservicereciver);
        }

        @Override
        public void onNotificationPosted(StatusBarNotification statusBarNotification) {
            Log.i(TAG,"ID :" + statusBarNotification.getId() + "\t" + statusBarNotification.getNotification().tickerText + "\t" + statusBarNotification.getPackageName() + "" + statusBarNotification.describeContents());
            Intent i = new  Intent("me.rohanjahagirdar.notificationsstatistics.NotificationsListener");
            i.putExtra("notification_event","onNotificationPosted :" + statusBarNotification.getPackageName() + "\n");
            updateNotificationStatistics(statusBarNotification);
            sendBroadcast(i);
        }

        @Override
        public void onNotificationRemoved(StatusBarNotification sbn) {
            Log.i(TAG,"********** onNOtificationRemoved");
            Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName() + "" + sbn.describeContents());
            Intent i = new  Intent("me.rohanjahagirdar.notificationsstatistics.NotificationsListener");
            i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");
            sendBroadcast(i);
        }

        class NLServiceReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("command").equals("clearall")) {
                    NotificationsListener.this.cancelAllNotifications();

                } else if (intent.getStringExtra("command").equals("list")) {
                    Intent i1 = new Intent("me.rohanjahagirdar.notificationsstatistics.NotificationsListener");
                    i1.putExtra("notification_event", "=====================");
                    sendBroadcast(i1);
                    int i = 1;
                    for (StatusBarNotification sbn : NotificationsListener.this.getActiveNotifications()) {
                        Intent i2 = new Intent("me.rohanjahagirdar.notificationsstatistics.NotificationsListener");
                        i2.putExtra("notification_event", i + " " + sbn.getPackageName() + "\n");
                        i2.putExtra("notification_event_description", i + " " + sbn.describeContents() + "\n");
                        sendBroadcast(i2);
                        i++;
                    }
                    Intent i3 = new Intent("me.rohanjahagirdar.notificationsstatistics.NotificationsListener");
                    i3.putExtra("notification_event", "===== Notification List ====");
                    sendBroadcast(i3);
                }
            }
        }
        private void updateNotificationStatistics(StatusBarNotification statusBarNotification) {
            try {
                HashMap<String, Integer> statistics = new HashMap<>();
                JSONObject json = new JSONObject();
                json = new JSONObject(sharedPref.getString("statistics", ""));

                if (!json.equals("")) {
                    if (json.has(statusBarNotification.getPackageName())) {
                        int count = json.getInt(statusBarNotification.getPackageName());
                        count++;
                        json.put(statusBarNotification.getPackageName(), count);
                    } else {
                        json.put(statusBarNotification.getPackageName(), 1);
                    }
                    sharedPref.edit().putString("statistics", json.toString());
                }
            }catch(JSONException ex){
                ex.printStackTrace();
            }

        }
    }
}
