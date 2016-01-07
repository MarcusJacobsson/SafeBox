package com.marcusjacobsson.vault.activities.mainmenu;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.activities.mainmenu.fragments.ChoosePhotosFragment;
import com.marcusjacobsson.vault.activities.mainmenu.fragments.ChooseSmsFragment;
import com.marcusjacobsson.vault.activities.mainmenu.fragments.ChooseVideosFragment;
import com.marcusjacobsson.vault.activities.mainmenu.fragments.PhotosFragment;
import com.marcusjacobsson.vault.activities.mainmenu.fragments.ShowSmsConvFragment;
import com.marcusjacobsson.vault.activities.mainmenu.fragments.SmsFragment;
import com.marcusjacobsson.vault.activities.mainmenu.fragments.VideosFragment;
import com.marcusjacobsson.vault.dialogfragments.AboutDialogFragment;
import com.marcusjacobsson.vault.dialogfragments.UninstallWarningDialogFragment;
import com.marcusjacobsson.vault.settings.SettingsActivity;

/**
 * Created by Marcus Jacobsson on 2015-08-02.
 */
public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnPhotos, btnVideos, btnSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setUpComponents();
        showWarningIfFirst();
    }

    private void showWarningIfFirst(){
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean first = sharedPref.getBoolean("firstWarning", true);
        if(first){
            UninstallWarningDialogFragment fragment = new UninstallWarningDialogFragment();
            fragment.show(getSupportFragmentManager(), "dialog");
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putBoolean("firstWarning", false);
            edit.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(getString(R.string.app_name));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;

            case R.id.action_about:
                AboutDialogFragment fragment = new AboutDialogFragment();
                fragment.show(getSupportFragmentManager(), "fragment");
                break;

            case R.id.action_photos_add:
                fragmentManager.beginTransaction().replace(android.R.id.content,
                        new ChoosePhotosFragment()).addToBackStack(null).commit();
                break;

            case R.id.action_videos_add:
                fragmentManager.beginTransaction().replace(android.R.id.content,
                        new ChooseVideosFragment()).addToBackStack(null).commit();
                break;

            case R.id.action_sms_add:
                fragmentManager.beginTransaction().replace(android.R.id.content,
                        new ShowSmsConvFragment()).addToBackStack(null).commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setUpComponents() {
        btnPhotos = (ImageButton) findViewById(R.id.btnMainMenuPhotos);
        btnVideos = (ImageButton) findViewById(R.id.btnMainMenuVideos);
        btnSms = (ImageButton) findViewById(R.id.btnMainMenuSms);
        btnPhotos.setOnClickListener(this);
        btnVideos.setOnClickListener(this);
        btnSms.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        boolean supported = true;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;

        switch (v.getId()) {

            case R.id.btnMainMenuPhotos:
                fragment = new PhotosFragment();
                break;

            case R.id.btnMainMenuVideos:
                fragment = new VideosFragment();
                break;

            case R.id.btnMainMenuSms:
                Uri message = Uri.parse("content://sms/");
                ContentResolver cr = getContentResolver();
                Cursor c = cr.query(message, null, null, null, null);
                if(c != null){
                    fragment = new SmsFragment();
                    c.close();
                }else{
                    supported = false;
                }

                break;

        }

        if (fragment != null) {
            fragmentTransaction.replace(android.R.id.content, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }else if(!supported){
            Toast.makeText(this, getResources().getString(R.string.sms_not_supported), Toast.LENGTH_LONG).show();
        }
    }
}
