package com.rrdl.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rrdl.chatroom.model.Message;
import com.rrdl.chatroom.model.User;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.rrdl.chatroom.model.Dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Rooms extends BaseActivity {


    User user ;
    ArrayList<User> users = new ArrayList<User>();
    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;
    DialogsList dialogsListView;
    FirebaseDatabase database;
    DatabaseReference messagesDb;
    DialogsListAdapter dialogsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);


        Intent intent = getIntent();
        boolean fromLogin = intent.getBooleanExtra("fromLogin", false);
        //if user is not from login (login bypass attempt) kill the activity
        if(!fromLogin)
            finish();


        user = (User) intent.getSerializableExtra("user");
        users.add(user);

        //setup firebase db
        database = FirebaseDatabase.getInstance();
        messagesDb = database.getReference("messages");



        dialogsListView = findViewById(R.id.dialogsList);
        dialogsListAdapter = new DialogsListAdapter<>(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        });

        dialogsListView.setAdapter(dialogsListAdapter);
        showLoading();
        messagesDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("onDataChange", "onDataChange == " + dataSnapshot.getKey());
                dialogsListAdapter.clear();
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    Dialog dialog = new Dialog();
                    dialog.setDialogName(item.getKey());
                    dialog.setUsers(users);
                    if(item.getChildrenCount() > 0){
                        for(DataSnapshot data : item.getChildren()) {
                            dialog.setLastMessage(data.getValue(Message.class));
                         }
                    }else{
                        Message message = new Message();
                        String uniqueID = UUID.randomUUID().toString();
                        message.setId(uniqueID);
                        message.setUser(user);
                        message.setCreatedAt(new Date());
                        message.setText("");
                        dialog.setLastMessage(message);
                    }
                    dialogsListAdapter.addItem(dialog);
                }
                hideLoading();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("value", "Failed to read value.", error.toException());
            }
        });

        dialogsListAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClick(IDialog dialog) {
                Intent intent = new Intent(Rooms.this, MainActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("room", dialog.getDialogName());
                startActivity(intent);
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("Chat")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

}