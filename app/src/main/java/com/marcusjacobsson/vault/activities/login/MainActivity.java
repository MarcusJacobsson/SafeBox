package com.marcusjacobsson.vault.activities.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.activities.mainmenu.MainMenuActivity;
import com.marcusjacobsson.vault.dialogfragments.WarningSentDialogFragment;
import com.marcusjacobsson.vault.dialogfragments.WelcomeDialogFragment;
import com.marcusjacobsson.vault.util.InternetAvailability;
import com.marcusjacobsson.vault.util.PasswordHash;
import com.marcusjacobsson.vault.util.PicassoHelper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PasswordHash.PasswordHashListener {

    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnOk;
    private ImageButton btnUndo;
    private EditText etPassword;
    private final StringBuilder stringBuilder = new StringBuilder();
    private String firstInput, secondInput;
    private boolean firstAttempt = true;
    private PasswordHash passwordHash;
    private int nbrOfLogInAttemptsCount = 0;
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setUpComponents();

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean first = sharedPref.getBoolean("firstLogin", true);
        boolean passwordReset = sharedPref.getBoolean("passwordReset", false);
        if (first && !passwordReset) {
            WelcomeDialogFragment fragment = new WelcomeDialogFragment();
            fragment.show(getSupportFragmentManager(), "dialog");
        }

        if (passwordReset) {
            Toast.makeText(this, getResources().getString(R.string.password_reset), Toast.LENGTH_LONG).show();
        }

        Intent i = new Intent(this, MainMenuActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (passwordHash != null) {
            passwordHash.cancelIfNeeded();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.action_forgotten_password:
                i = new Intent(this, ResetPasswordActivity.class);
                break;

            case R.id.action_enter_password_token:
                i = new Intent(this, EnterPasswordToken.class);
                break;
        }
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    private void setUpComponents() {

        passwordHash = new PasswordHash(this);
        passwordHash.setListener(this);

        btn1 = (Button) findViewById(R.id.btnLogin1);
        btn2 = (Button) findViewById(R.id.btnLogin2);
        btn3 = (Button) findViewById(R.id.btnLogin3);
        btn4 = (Button) findViewById(R.id.btnLogin4);
        btn5 = (Button) findViewById(R.id.btnLogin5);
        btn6 = (Button) findViewById(R.id.btnLogin6);
        btn7 = (Button) findViewById(R.id.btnLogin7);
        btn8 = (Button) findViewById(R.id.btnLogin8);
        btn9 = (Button) findViewById(R.id.btnLogin9);
        btn0 = (Button) findViewById(R.id.btnLogin0);
        btnUndo = (ImageButton) findViewById(R.id.btnLoginUndo);
        btnOk = (Button) findViewById(R.id.btnLoginOk);

        etPassword = (EditText) findViewById(R.id.etLoginPassword);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn0.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        PicassoHelper.init(this);
    }

    @Override
    public void onClick(View v) {

        vibe.vibrate(20);

        switch (v.getId()) {

            case R.id.btnLogin1:
                etPassword.setText(stringBuilder.append("1"));
                break;

            case R.id.btnLogin2:
                etPassword.setText(stringBuilder.append("2"));
                break;

            case R.id.btnLogin3:
                etPassword.setText(stringBuilder.append("3"));
                break;

            case R.id.btnLogin4:
                etPassword.setText(stringBuilder.append("4"));
                break;

            case R.id.btnLogin5:
                etPassword.setText(stringBuilder.append("5"));
                break;

            case R.id.btnLogin6:
                etPassword.setText(stringBuilder.append("6"));
                break;

            case R.id.btnLogin7:
                etPassword.setText(stringBuilder.append("7"));
                break;

            case R.id.btnLogin8:
                etPassword.setText(stringBuilder.append("8"));
                break;

            case R.id.btnLogin9:
                etPassword.setText(stringBuilder.append("9"));
                break;

            case R.id.btnLogin0:
                etPassword.setText(stringBuilder.append("0"));
                break;

            case R.id.btnLoginUndo:
                if (!etPassword.getText().toString().equals("")) {
                    String str = etPassword.getText().toString();
                    String newStr = str.substring(0, str.length() - 1);
                    etPassword.setText(newStr);
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                break;

            case R.id.btnLoginOk:
                SharedPreferences sharedPref = getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPref.edit();
                boolean firstLogin = sharedPref.getBoolean("firstLogin", true);

                String input = etPassword.getText().toString();

                //Check password length - must be at least 3 numbers
                if (input.length() < 2) {
                    Toast.makeText(this, getString(R.string.login_password_too_short), Toast.LENGTH_SHORT).show();
                    //First login and first password entry
                } else if (firstLogin && firstAttempt) {
                    firstInput = input;
                    stringBuilder.delete(0, stringBuilder.length());
                    etPassword.setText("");
                    Toast.makeText(this, getString(R.string.login_enter_password_again), Toast.LENGTH_SHORT).show();
                    firstAttempt = false;
                    //Second password entry
                } else if (!firstAttempt) {
                    secondInput = etPassword.getText().toString();
                    if (firstInput.equals(secondInput)) {
                        passwordHash.hashPassword(this, input);
                        edit.putBoolean("firstLogin", false);
                        edit.putBoolean("passwordReset", false);
                        edit.apply();
                    } else {
                        Toast.makeText(this, getString(R.string.login_password_mismatch), Toast.LENGTH_SHORT).show();
                    }
                    firstInput = "";
                    secondInput = "";
                    firstAttempt = true;
                    etPassword.setText("");
                    stringBuilder.delete(0, stringBuilder.length());
                    //Check if the password matches, log in if it does
                } else {
                    passwordHash.checkPassword(this, input);
                }
                break;
        }
    }

    private void sendAntiBreakInWarning() {
        /*
        TODO: Take a pic with front camera, send to web service, web service sends mail
         */

        /*
        Show a warning dialog and send an email to the registered user via webservice call
         */
        WarningSentDialogFragment dialog = new WarningSentDialogFragment();
        dialog.show(getSupportFragmentManager(), "dialog");

        String antiBreakInEmail = PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.pref_break_in_email_key), "");
        String timezone = TimeZone.getDefault().getID();

        String[] input = new String[]{antiBreakInEmail, timezone};

        if (InternetAvailability.isOnline())
            new SendEmailTask().execute(input);
    }

    @Override
    public void onPasswordHashCheckCompleted(boolean passwordIsCorrect) {
        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int antiBreakInNbrOfAttempts = Integer.valueOf(defaultPrefs.getString(getResources().getString(R.string.pref_break_in_nbr_of_attempts_key), "3"));
        boolean isAntiBreakInEnabled = defaultPrefs.getBoolean(getResources().getString(R.string.pref_break_in_enabled_key), false);

        if (passwordIsCorrect) {
            logIn();
        } else {
            Toast.makeText(this, getResources().getString(R.string.login_wrong_password), Toast.LENGTH_SHORT).show();
            nbrOfLogInAttemptsCount++;
            if (nbrOfLogInAttemptsCount == antiBreakInNbrOfAttempts && isAntiBreakInEnabled)
                sendAntiBreakInWarning();
        }
    }

    @Override
    public void onPasswordHashCompleted() {
        logIn();
    }

    private void logIn() {
        Intent i = new Intent(this, MainMenuActivity.class);
        startActivity(i);
    }

    /**
     * Invokes a Web Service to send an email to the users registered anti break in email
     */
    private class SendEmailTask extends AsyncTask<String, Void, Void> {

        private final String NAMESPACE = "http://ottomatech.se/soap/vaultSendMailServiceurn:vaultSendMailService";
        private final String URL = "http://ottomatech.se/marcus/vault_webservice/vaultSendMailService.php?wsdl";
        private final String SOAP_ACTION = "http://ottomatech.se/marcus/vault_webservice/vaultSendMailService.php/sendMail/sendWarningMail";
        private final String METHOD_NAME = "sendWarningMail";

        @Override
        protected Void doInBackground(String... params) {

            String to = params[0];
            String timezone = params[1];

            //Create request
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            //Property which holds input parameters (to)
            PropertyInfo piTo = new PropertyInfo();
            //Set Name
            piTo.setName("to");
            //Set Value
            piTo.setValue(to);
            //Set dataType
            piTo.setType(String.class);
            //Add the property to request object
            request.addProperty(piTo);

            //Property which holds input parameters (timezone)
            PropertyInfo piTimeZone = new PropertyInfo();
            //Set Name
            piTimeZone.setName("timezone");
            //Set Value
            piTimeZone.setValue(timezone);
            //Set dataType
            piTimeZone.setType(String.class);
            //Add the property to request object
            request.addProperty(piTimeZone);

            //Create envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //Set output SOAP object
            envelope.setOutputSoapObject(request);
            //Create HTTP call object
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                //Invole web service
                androidHttpTransport.call(SOAP_ACTION, envelope);
                //Get the response

                SoapObject result = (SoapObject) envelope.bodyIn;

                if (result != null) {
                    System.out.println(result.getProperty(0).toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
