package com.example.qiupys.wateroder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity{
    private TextView profilename;
    private EditText phone;
    private EditText address;
    private EditText bankcard;

    private Button submit;
    private ImageView back;

    private UserDataManager userDataManager;

    private SharedPreferences sp;
    private SharedPreferences up;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        up=getSharedPreferences("userUpdate",0);
        sp=getSharedPreferences("userInfo",0);
        if (userDataManager==null){
            userDataManager=new UserDataManager(this);
            userDataManager.openDatabase();
        }

        submit=findViewById(R.id.submit);
        back=findViewById(R.id.back);

        submit.setOnClickListener(listener);
        back.setOnClickListener(listener);

        profilename=findViewById(R.id.profile_name);
        String username=sp.getString("USER_NAME",null);
        profilename.setText(username);

        phone=findViewById(R.id.profile_phone);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String num = "[1][345678]\\d{9}";

                if (TextUtils.isEmpty(s.toString())){
                    //Toast.makeText(EditProfile.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                }else if(s.length()!=11||s.toString().matches(num)==false){
                    //Toast.makeText(EditProfile.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                }
            }
        });
        address=findViewById(R.id.profile_address);
        bankcard=findViewById(R.id.profile_card);
        bankcard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())){
                    //Toast.makeText(EditProfile.this, "银行卡号不能为空", Toast.LENGTH_SHORT).show();
                }else if(editable.length()<17){
                    //Toast.makeText(EditProfile.this, "请输入正确的银行卡号", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Cursor cursor=userDataManager.fetchUserData(username);
        Cursor cursor_bank=userDataManager.fetchCardData(username);
        if (cursor!=null&&cursor_bank!=null){
            int phone_index=cursor.getColumnIndex("Phone");
            int address_index=cursor.getColumnIndex("Address");
            int bankcard_index=cursor_bank.getColumnIndex("Card_Number");

            String sphone=cursor.getString(phone_index);
            String saddress=cursor.getString(address_index);
            String sbank=cursor_bank.getString(bankcard_index);

            if (sphone!=null){
                phone.setText(sphone);
            }
            if (saddress!=null){
                address.setText(saddress);
            }
            if (sbank!=null){
                bankcard.setText(sbank);
            }
        }
    }

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.submit:
                    infosubmit();
                    break;
                case R.id.back:
                    Intent intent=new Intent(EditProfile.this,User.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void infosubmit() {
        if (isFilled()){
            String username=sp.getString("USER_NAME",null);
            String phone_new=phone.getText().toString().trim();
            String address_new=address.getText().toString().trim();
            String card_new=bankcard.getText().toString().trim();

            SharedPreferences.Editor editor=up.edit();

            HashMap<String,String> data=new HashMap<>();
            data.put("PHONE",phone_new);
            data.put("ADDRESS",address_new);
            data.put("BANK",card_new);
            boolean result=userDataManager.updateUserDataByName(data,username);
            if (result){
                editor.putString("USER_NAME",username);
                editor.putString("PHONE",phone_new);
                editor.putString("ADDRESS",address_new);
                editor.putString("BANK",card_new);
                editor.commit();

                Intent intent=new Intent(EditProfile.this,User.class);
                startActivity(intent);
                finish();
                Toast.makeText(this, "update success!",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "update failed!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isFilled() {
        if (phone.getText().toString().trim().equals("")){
            Toast.makeText(this, "Phone can't be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (address.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Address can't be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (bankcard.getText().toString().trim().equals("")){
            Toast.makeText(this, "Bankcard can't be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
