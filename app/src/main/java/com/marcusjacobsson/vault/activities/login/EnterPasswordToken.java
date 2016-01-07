package com.marcusjacobsson.vault.activities.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.database.DataSource;
import com.marcusjacobsson.vault.pojos.PasswordResetRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Marcus Jacobsson on 2015-08-05.
 */
public class EnterPasswordToken extends AppCompatActivity implements View.OnClickListener {

    private Button btnResetPassword;
    private EditText etPasswordResetToken;
    private DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password_token);
        setUpComponents();
    }

    private void setUpComponents() {
        btnResetPassword = (Button) findViewById(R.id.btnEnterPasswordTokenReset);
        etPasswordResetToken = (EditText) findViewById(R.id.etEnterPasswordToken);
        btnResetPassword.setOnClickListener(this);
        dataSource = new DataSource(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnterPasswordTokenReset:
                String token = etPasswordResetToken.getText().toString();
                if (!token.equals("")) {
                    try {
                        dataSource.open();
                        PasswordResetRequest request = dataSource.getPasswordResetRequest(token);
                        dataSource.close();
                        String timeOfRequest = request.getTime();

                        //Check so that the request has not been used (i.e. removed from the DB earlier)
                        if (timeOfRequest != null && !timeOfRequest.equals("")) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date requestDate = sdf.parse(timeOfRequest);
                            Date now = new Date();

                            //Check if the request is still valid (must have been made within 15 mins of entering the token)
                            if (requestDate.getTime() + 900000 > now.getTime()) {

                                //Remove the PasswordResetRequest from DB, invalidating it
                                dataSource.open();
                                dataSource.deletePasswordResetRequest(token);
                                dataSource.close();

                                /*
                                To reset the password, set set the "firstLogin" pref value to true and
                                launch MainActivity. This will force the user to pick a new password.
                                 */
                                SharedPreferences sharedPref = getSharedPreferences(

                                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sharedPref.edit();
                                edit.putBoolean("firstLogin", true);
                                edit.putBoolean("passwordReset", true);
                                edit.apply();

                                Intent i = new Intent(this, MainActivity.class);
                                //Flags to clear the back stack
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            } else {
                                //Not valid - expired
                                Toast.makeText(this, getResources().getString(R.string.password_reset_request_not_valid), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //Not valid - Not found in DB
                            Toast.makeText(this, getResources().getString(R.string.password_reset_request_not_valid), Toast.LENGTH_LONG).show();
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Not valid - Token input field empty
                    Toast.makeText(this, getResources().getString(R.string.empty_token), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
