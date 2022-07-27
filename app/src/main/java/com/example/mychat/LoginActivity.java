package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView txt_signup;
    EditText login_email,login_password;
    TextView signin_btn;
    FirebaseAuth auth;
    EditText a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_signup=findViewById(R.id.txt_signup);
        login_email=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_password);
        signin_btn=findViewById(R.id.signin_btn);


        auth= FirebaseAuth.getInstance();

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=login_email.getText().toString();
                String password=login_password.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                email.trim();
                password.trim();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"Enter Valid Data!",Toast.LENGTH_SHORT).show();
                }else if(!email.matches(emailPattern)){
                    login_email.setError("Invalid Email Address");
                    Toast.makeText(LoginActivity.this,"Invalid Email!",Toast.LENGTH_SHORT).show();
                }else if(password.length()<6){
                    login_password.setError("Invalid Password");
                    Toast.makeText(LoginActivity.this,"Please Enter Valid Password!",Toast.LENGTH_SHORT).show();
                }else {
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            }else{
                                Toast.makeText(LoginActivity.this,"Error in Login!",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }



            }
        });




        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });


    }
}



