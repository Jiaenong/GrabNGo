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
import android.widget.Toast;

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
    private Context contextTemp;

    @Override
    public void onReceive(Context context, Intent intent) {

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

}
