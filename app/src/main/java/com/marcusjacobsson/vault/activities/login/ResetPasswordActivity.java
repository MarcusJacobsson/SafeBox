package com.marcusjacobsson.vault.activities.login;

import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.database.DataSource;
import com.marcusjacobsson.vault.dialogfragments.NoInternetConnectionDialogFragment;
import com.marcusjacobsson.vault.util.InternetAvailability;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Marcus Jacobsson on 2015-08-04.
 */
public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSend;
    private EditText etEmail;
    private DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setUpComponents();
    }

    private void setUpComponents() {
        btnSend = (Button) findViewById(R.id.btnResetPasswordSend);
        etEmail = (EditText) findViewById(R.id.etResetPasswordEmail);
        btnSend.setOnClickListener(this);
        this.dataSource = new DataSource(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnResetPasswordSend:
                String backupEmail = PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.pref_backup_email_key), "");
                String inputEmail = etEmail.getText().toString();

                if (inputEmail.equalsIgnoreCase(backupEmail)) {
                    if (InternetAvailability.isOnline()) {
                        //Generate token
                        SecureRandom random = new SecureRandom();
                        String token = new BigInteger(130, random).toString(32);

                        try {
                            dataSource.open();
                            dataSource.createPasswordResetRequest(token);
                            dataSource.close();
                            String[] params = new String[]{token, backupEmail};
                            new SendEmailTask().execute(params);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        NoInternetConnectionDialogFragment fragment = new NoInternetConnectionDialogFragment();
                        fragment.show(getSupportFragmentManager(), "dialog");
                    }
                }

                //Show toast even if the mail address was wrong
                if (InternetAvailability.isOnline())
                    Toast.makeText(this, getResources().getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Invokes a Web Service to send an email to the users registered backup email
     */
    private class SendEmailTask extends AsyncTask<String, Void, Void> {

        private final String NAMESPACE = "http://ottomatech.se/soap/vaultSendMailServiceurn:vaultSendMailService";
        private final String URL = "http://ottomatech.se/marcus/vault_webservice/vaultSendMailService.php?wsdl";
        private final String SOAP_ACTION = "http://ottomatech.se/marcus/vault_webservice/vaultSendMailService.php/sendMail/sendMail";
        private final String METHOD_NAME = "sendMail";

        @Override
        protected Void doInBackground(String... params) {

            String token = params[0];
            String to = params[1];

            System.out.println(token);
            System.out.println(to);

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

            //Property which holds input parameters (token)
            PropertyInfo piToken = new PropertyInfo();
            //Set Name
            piToken.setName("token");
            //Set Value
            piToken.setValue(token);
            //Set dataType
            piToken.setType(String.class);
            //Add the property to request object
            request.addProperty(piToken);


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
