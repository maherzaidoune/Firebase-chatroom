package com.rrdl.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rrdl.chatroom.model.Message;
import com.rrdl.chatroom.model.User;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends BaseActivity {

    MessageInput inputView;
    MessagesList messagesList;
    private MessagesListAdapter<Message> messagesAdapter;
    private ArrayList<Message> startupList;
    User messageUser ;
    FirebaseDatabase database;
    DatabaseReference messagesDb;
    protected ImageLoader imageLoader;
    LinearLayout back_button;

    TextView title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //getting data from previous page
        Intent intent = getIntent();

        //set image loader for avatar
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        };

        //get user from login page
        messageUser = (User) intent.getSerializableExtra("user");


        //setup firebase db
        database = FirebaseDatabase.getInstance();
        String room = intent.getStringExtra("room");
        if(room == null)
            room = "other";
        //set title
        title.setText(room);
        //get room db
        messagesDb = database.getReference("messages").child(room);

        inputView = findViewById(R.id.input);

        //setUp adapter with userId, and empty messages list
        messagesAdapter = new MessagesListAdapter<>(messageUser.getId(), imageLoader);
        messagesAdapter.setOnMessageLongClickListener(new MessagesListAdapter.OnMessageLongClickListener<Message>() {
            @Override
            public void onMessageLongClick(final Message message) {
                if(!message.getUser().getId().equals(messageUser.getId())){
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Message");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, message.getText());
                    startActivity(Intent.createChooser(shareIntent, "share"));
                    return;
                }
                Log.i("msg", "onMessageLongClick == " + message.getId());
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete message ? ")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    DatabaseReference msg = messagesDb.child(message.getId());
                                    msg.removeValue();
                                    Toast.makeText(getApplicationContext(), "Message deleted", Toast.LENGTH_LONG).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        messagesList = findViewById(R.id.messagesList);
        messagesList.setAdapter(messagesAdapter);

        //listen fot input
        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //validate and send message
                Message message = new Message();
                message.setText(input.toString());
                message.setCreatedAt(new Date());
                message.setUser(messageUser);
//                messagesAdapter.addToStart(message, true);
                String id = messagesDb.push().getKey();
                message.setId(id);
                messagesDb.child(id).setValue(message);
//                messagesDb.setValue(message);
                return true;
            }
        });

        //listen to db changes

        messagesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message data = snapshot.getValue(Message.class);
//                if(!data.getUser().getId().equals(messageUser.getId())){
                    messagesAdapter.addToStart(data, true);
//                }
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this, "channel_id");
                notificationBuilder.setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentTitle("New Messages")
                        .setContentText(data.getText())
                        .setDefaults(Notification.DEFAULT_ALL);

                notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Notification Channel is required for Android O and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription("channel description");
                    channel.setShowBadge(true);
                    channel.canShowBadge();
                    channel.enableLights(true);
                    channel.enableVibration(true);
                    channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(100, notificationBuilder.build());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message data = snapshot.getValue(Message.class);
                messagesAdapter.delete(data);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}