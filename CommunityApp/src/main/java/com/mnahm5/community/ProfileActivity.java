package com.mnahm5.community;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.parse.ParseUser;
import com.parse.starter.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        redirect();
        Iconify.with(new FontAwesomeModule());
        setTitle("Profile");
        Iconify.with(new FontAwesomeModule());

        final TextView tvLoading = (TextView) findViewById(R.id.tvLoading);
        final EditText etFullName = (EditText) findViewById(R.id.etFullName);
        final ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        final Button btEdit = (Button) findViewById(R.id.btEdit);

        if (ParseUser.getCurrentUser().get("profilePic") == null) {
            ivProfilePic.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_user)
                    .actionBarSize());
            ivProfilePic.setVisibility(View.VISIBLE);
        }
        etFullName.setText(ParseUser.getCurrentUser().get("name").toString());
        etFullName.setVisibility(View.VISIBLE);
        btEdit.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.INVISIBLE);
    }

    public void redirect()
    {
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
