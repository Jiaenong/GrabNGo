package com.example.user.grabngo.Class;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.user.grabngo.Admin.ManagerHomeActivity;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LowStockNotification extends BroadcastReceiver {
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    public static String NOTIFICATION = "0";
    private Context contextTemp;

    @Override
    public void onReceive(final Context context, Intent intent) {

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");

        contextTemp = context;


    mCollectionReference.whereLessThanOrEqualTo("stockAmount",100).whereEqualTo("lowStockAlert",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            int count = 0;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                count++;
            }
            if(count!=0) {
                Intent resultIntent = new Intent(contextTemp, ManagerHomeActivity.class);
                resultIntent.putExtra("lowStockAlert",true);
                PendingIntent pendingIntent = PendingIntent.getActivity(contextTemp, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(contextTemp, "notifyChannel")
                        .setSmallIcon(R.drawable.ic_info_outline_black_24dp)
                        .setContentTitle("Low Stock Alert")
                        .setSound(alarmSound)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setContentText("You have " + count + " product(s) in low stock condition");

                int notificationId = 101;
                NotificationManager notificationManager = (NotificationManager) contextTemp.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, builder.build());
            }
        }
    });

    }



    public static void showNotification(Context context,Class<?> cls,String title,String content)
    {


        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"channelID")
                .setContentTitle(title)
                .setContentText(content).setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
