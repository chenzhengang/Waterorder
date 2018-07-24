package com.example.qiupys.wateroder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private EditText userName;
    private EditText userPwd;

    private UserDataManager userDataManager;

    private SharedPreferences loginSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userName=findViewById(R.id.input_username);
        userPwd=findViewById(R.id.input_password);

        loginSP=getSharedPreferences("userInfo",0);

        Button ulogin = findViewById(R.id.sign_in);
        ulogin.setOnClickListener(listener);

        TextView usignUp = findViewById(R.id.sign_up);
        String text="没有账号？注册";
        SpannableString ss=new SpannableString(text);
        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        }, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        usignUp.setText(ss);
        usignUp.setMovementMethod(LinkMovementMethod.getInstance());

        if (userDataManager == null) {
            userDataManager = new UserDataManager(this);
            userDataManager.openDatabase();
        }
    }
    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.sign_in:
                    userLogin();
                    break;
                default:
                    break;
            }
        }
    };

    private void userLogin() {
        if (isUserNameAndPassword()){
            String name=userName.getText().toString().trim();
            String password=userPwd.getText().toString().trim();

            SharedPreferences.Editor editor=loginSP.edit();

            boolean result=userDataManager.userLogin(name,password);
            if (result){
                editor.putString("USER_NAME",name);
                editor.putString("USER_PWD",password);
                editor.commit();

                Intent intent=new Intent(Login.this,User.class);
                startActivity(intent);
                finish();
                Toast.makeText(this, getString(R.string.login_success),Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, getString(R.string.login_fail),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isUserNameAndPassword() {
        if (userName.getText().toString().trim().equals("")){Toast.makeText(this, getString(R.string.username_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (userPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        if (userDataManager == null) {
            userDataManager = new UserDataManager(this);
            userDataManager.openDatabase();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (userDataManager != null) {
            userDataManager.closeDatabase();
            userDataManager = null;
        }
        super.onPause();
    }
}
