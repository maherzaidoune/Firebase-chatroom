package com.rrdl.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rrdl.chatroom.model.User;
import com.rrdl.chatroom.utils.AuthUtils;

public class SignIn extends BaseActivity {

    Button login;
    Button CreateAccount;
    private FirebaseAuth mAuth;

    EditText password_txt;
    EditText email_txt;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        login = findViewById(R.id.login);
        CreateAccount = findViewById(R.id.CreateAccount);
        password_txt = findViewById(R.id.password_txt_s);
        email_txt = findViewById(R.id.email_txt_s);
        mAuth = FirebaseAuth.getInstance();

//        //if user already connected, user data saved, then no login needed
//        if(user != null && user.getId() != null){
//            Intent intent = new Intent(SignIn.this, Rooms.class);
//            intent.putExtra("fromLogin", true);
//            intent.putExtra("user", user);
//            startActivity(intent);
//            finish();
//        }

        //if from signup, no need to type, have fun :p
        Intent intent = getIntent();
        if(intent.getStringExtra("email") != null){
            email_txt.setText(intent.getStringExtra("email"));
        }

        if(intent.getStringExtra("password") != null){
            password_txt.setText(intent.getStringExtra("password"));
        }

        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(SignIn.this, Signup.class);
                startActivity(signup);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                if(!AuthUtils.isValidEmail(email_txt.getText().toString())){
                    Toast.makeText(SignIn.this, "Type valid email",
                            Toast.LENGTH_SHORT).show();
                    hideLoading();
                    return;
                }
                if(password_txt.getText().toString().length() == 0){
                    Toast.makeText(SignIn.this, "Type password",
                            Toast.LENGTH_SHORT).show();
                    hideLoading();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email_txt.getText().toString(), password_txt.getText().toString())
                        .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser u = mAuth.getCurrentUser();
                                    hideLoading();
                                    user = new User();
                                    user.setId(u.getUid());
                                    user.setName(u.getEmail());
                                    Intent intent = new Intent(SignIn.this, Rooms.class);
                                    intent.putExtra("fromLogin", true);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    hideLoading();
                                    Toast.makeText(SignIn.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}