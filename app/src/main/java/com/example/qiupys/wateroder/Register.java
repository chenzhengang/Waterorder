package com.example.qiupys.wateroder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity{
    private EditText userName;
    private EditText userPwd;
    private EditText recPwd;

    private Button signUp;
    private TextView login;

    private UserDataManager userDataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        userName=findViewById(R.id.input_username);
        userPwd=findViewById(R.id.input_password);
        recPwd=findViewById(R.id.input_password2);

        signUp=findViewById(R.id.sign_up);
        signUp.setOnClickListener(listener);

        login=findViewById(R.id.login);
        String text="已有账号？登录";
        SpannableString ss=new SpannableString(text);
        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        },0,text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(ss);
        login.setMovementMethod(LinkMovementMethod.getInstance());

        if (userDataManager==null){
            userDataManager=new UserDataManager(this);
            userDataManager.openDatabase();
        }
    }
    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.sign_up:
                    register();
                    break;
                default:
                    break;
            }
        }
    };

    private void register() {
        if (isUserNameAndPassword()){
            String name=userName.getText().toString().trim();
            String password=userPwd.getText().toString().trim();
            String rec_password=recPwd.getText().toString().trim();


            if (!password.equals(rec_password)){
                Toast.makeText(this,"Password should be same!",Toast.LENGTH_SHORT).show();
                return;
            }

            if (userDataManager.findUserByName(name)){
                Toast.makeText(this,"Sorry, name has been registered!",Toast.LENGTH_SHORT).show();
                return;
            }

            UserData nUser=new UserData(name,password);
            long result=userDataManager.insertUserData(nUser);
            if (result>0){
                Toast.makeText(this, "Register Successfully!",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Register.this,Login.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(this, "Register Failed, Please Try Again!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isUserNameAndPassword() {
        if (userName.getText().toString().trim().equals("")){
            Toast.makeText(this, getString(R.string.username_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (userPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (recPwd.getText().toString().trim().equals("")){
            Toast.makeText(this,getString(R.string.rec_empty),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
