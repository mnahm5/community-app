package com.mnahm5.community;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private boolean flag = false;
    private TextView tvLoading;
    private EditText etFullName;
    private ImageView ivProfilePic;
    private Button btEdit;
    private Button btCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        redirect();
        Iconify.with(new FontAwesomeModule());
        setTitle("Profile");
        Iconify.with(new FontAwesomeModule());

        tvLoading = (TextView) findViewById(R.id.tvLoading);
        etFullName = (EditText) findViewById(R.id.etFullName);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        btEdit = (Button) findViewById(R.id.btEdit);
        btCancel = (Button) findViewById(R.id.btCancel);

        if (ParseUser.getCurrentUser().get("profilePic") == null) {
            tvLoading.setText("{fa-user}");
        }
        else {
            ParseFile file = (ParseFile) ParseUser.getCurrentUser().get("profilePic");
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ivProfilePic.setImageBitmap(bitmap);
                        ivProfilePic.setVisibility(View.VISIBLE);
                        tvLoading.setVisibility(View.INVISIBLE);
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) etFullName.getLayoutParams();
                        lp.addRule(RelativeLayout.BELOW, ivProfilePic.getId());
                        etFullName.setLayoutParams(lp);
                    }
                }
            });
        }
        etFullName.setText(ParseUser.getCurrentUser().get("name").toString());
        etFullName.setVisibility(View.VISIBLE);
        etFullName.setEnabled(false);
        btEdit.setVisibility(View.VISIBLE);
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
                ivProfilePic.setImageBitmap(image);
                tvLoading.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) etFullName.getLayoutParams();
                lp.addRule(RelativeLayout.BELOW, ivProfilePic.getId());
                etFullName.setLayoutParams(lp);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void redirect()
    {
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    public void EditProfile(View view)
    {
        if (!flag) {
            etFullName.setEnabled(true);
            btEdit.setText(R.string.save_changes);
            btCancel.setVisibility(View.VISIBLE);
            flag = true;
        }
        else {
            saveChanges();
        }
    }

    public void SetProfilePic(View view)
    {
        if (flag) {
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
    }

    public void UpdateProfilePic(View view)
    {
        SetProfilePic(view);
    }

    public void getPhotos()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void saveChanges()
    {
        // Getting the image and converting it in a parse object
        Bitmap image = ((BitmapDrawable) ivProfilePic.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile file = new ParseFile("image.png", byteArray);

        ParseUser user = ParseUser.getCurrentUser();
        user.put("profilePic", file);
        user.put("name", etFullName.getText().toString());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Profile Updated",
                            Toast.LENGTH_SHORT
                    ).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
