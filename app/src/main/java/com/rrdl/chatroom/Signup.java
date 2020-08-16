package com.rrdl.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rrdl.chatroom.utils.AuthUtils;

public class Signup extends BaseActivity {

    private FirebaseAuth mAuth;
    ImageButton back_signup;
    Button signup;
    EditText password_txt;
    EditText confirm_password_txt;

    EditText email_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        back_signup = findViewById(R.id.back_signup);
        password_txt = findViewById(R.id.password_txt);
        confirm_password_txt = findViewById(R.id.confirm_password_txt);
        email_txt = findViewById(R.id.email_txt);
        mAuth = FirebaseAuth.getInstance();
        back_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                if(!AuthUtils.isValidEmail(email_txt.getText().toString())){
                    Toast.makeText(Signup.this, "Type a valid email",
                            Toast.LENGTH_SHORT).show();
                    hideLoading();
                    return;
                }
                if(password_txt.getText().toString().length() < 6){
                    Toast.makeText(Signup.this, "Type a valid password",
                            Toast.LENGTH_SHORT).show();
                    hideLoading();
                    return;
                }

                if(!confirm_password_txt.getText().toString().equals(password_txt.getText().toString())){
                    Toast.makeText(Signup.this, "Password doesn't match",
                            Toast.LENGTH_SHORT).show();
                    hideLoading();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email_txt.getText().toString(), password_txt.getText().toString())
                        .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Signup.this, "Signup success.",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    hideLoading();
                                    Intent intent = new Intent(Signup.this, SignIn.class);
                                    intent.putExtra("email",email_txt.getText().toString());
                                    intent.putExtra("password",password_txt.getText().toString());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Signup", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Signup.this, "Signup failed.",
                                            Toast.LENGTH_SHORT).show();
                                    hideLoading();
                                }
                            }
                        });
            }
        });
    }
}