package com.example.heechang.attendence;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

/**
 * Created by heechang on 2017-11-04.
 */


public class Login extends AppCompatActivity {

    private EditText login_id_editText;
    private EditText login_pw_editText;
    private Button login_enter_button;
    private Button login_quit_button;
    private Context context;
    public static boolean logined = false;
    private String result;


    public static final Person P = new Person();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_id_editText = (EditText)findViewById(R.id.login_id_editText);
        login_pw_editText = (EditText)findViewById(R.id.login_pw_editText);

        login_pw_editText.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
        login_pw_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        context = this;
    }

    public void onLogin(View v)
    {
        String IDtemp = login_id_editText.getText().toString();
        String Passwordtemp = md5(login_pw_editText.getText().toString());
        InsertData task = new InsertData(context, new InsertData.AsyncResponse() {
            @Override
            public void getResult(String mJsonString) {
                result = mJsonString;
            }
        });
        task.execute("http://220.230.117.98/se/login.php", "Memid=" + IDtemp + "&Mempassword=" + Passwordtemp);

        if(result.equals("failure"))
        {
            Toast.makeText(getApplicationContext(), "ID Password 오류 다시 확인해주세요", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StringTokenizer strToken =  new StringTokenizer(result," ");

            P.id = IDtemp;
            P.name = strToken.nextToken();
            P.sex = strToken.nextToken();
            P.department = strToken.nextToken();
            P.status = strToken.nextToken(); //P(Person)객체에 정보삽입-> 앞으로사용자 정보가 필요할때 사용
            logined = true;
            if(P.status.equals("학생"))
            {
                Intent i = new Intent(Login.this, Attendance_Student.class);
                startActivity(i); //학생창 띄움
            }
            else
            {
                Intent i = new Intent(Login.this, Request_Professor.class);
                startActivity(i); //교수창 띄움
            }
        }

    }

    public void onQuit(View v)
    {
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}