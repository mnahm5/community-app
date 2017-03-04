package com.mnahm5.community;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommunityDetailsActivity extends AppCompatActivity {

    private ImageView ivProfilePic;
    private TextView tvInstruction;
    private TextView tvProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_community_details);

        setTitle("Create your own Community");
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        tvInstruction = (TextView) findViewById(R.id.tvInstruction);
        tvProfilePic = (TextView) findViewById(R.id.tvProfilePic);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhotos();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Toast.makeText(
                        getApplicationContext(),
                        "Photo Received",
                        Toast.LENGTH_SHORT
                ).show();
                tvProfilePic.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvInstruction.getLayoutParams();
                lp.addRule(RelativeLayout.BELOW, ivProfilePic.getId());
                tvInstruction.setLayoutParams(lp);
                ivProfilePic.setVisibility(View.VISIBLE);
                ivProfilePic.setImageBitmap(image);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ChangeProfilePic(View view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            else {
                getPhotos();
            }
        }
        else {
            getPhotos();
        }
    }

    public void getPhotos()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void AddCommunity(View view)
    {
        Bitmap image = ((BitmapDrawable) ivProfilePic.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile file = new ParseFile("image.png", byteArray);

        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etDescription = (EditText) findViewById(R.id.etDescription);

        List<String> adminList = new ArrayList<>();
        adminList.add(ParseUser.getCurrentUser().getUsername());
        List<String> followingList = new ArrayList<>();
        followingList.add(ParseUser.getCurrentUser().getUsername());

        ParseObject community = new ParseObject("Community");
        community.put("name", etName.getText().toString());
        community.put("description", etDescription.getText().toString());
        community.put("image", file);
        community.put("admins", adminList);
        community.put("following", followingList);

        community.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Community Created",
                            Toast.LENGTH_SHORT
                    ).show();
                    Intent intent = new Intent(getApplicationContext(), CommunitiesListActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(
                            getApplicationContext(),
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }
}
