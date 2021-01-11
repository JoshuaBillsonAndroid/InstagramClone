package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    ListView userListView;
    static ArrayList<String> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
        userListView = findViewById(R.id.userListView);

        /* Setup On Click Listener For List View Items */
        userListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
            intent.putExtra("username", users.get(position));
            startActivity(intent);
        });


        /* Get List Of Users & Display To List View */
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground((List<ParseUser> objects, ParseException e) -> {
            users.clear();
            if (e == null && objects.size() > 0) {
                for (ParseUser user : objects) {
                    users.add(user.getUsername());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UserList.this, android.R.layout.simple_list_item_1, UserList.users);
                userListView.setAdapter(arrayAdapter);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.share) {
            /* If We Do Not Have Permission To Access Camera Roll, Make A Request */
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                getPhoto();
            }
        } else if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /* In The Event That Permission To Access Camera Roll Is Granted, Get Photos */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoto();
        }
    }

    /* Upon Getting A Photo From The Camera Roll, Update The Image View */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                /* Turn Photo Into Byte Array */
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                /* Create Parse Object */
                ParseFile parseFile = new ParseFile("img.png", byteArray);
                ParseObject object = new ParseObject("Image");
                object.put("image", parseFile);
                object.put("user", ParseUser.getCurrentUser().getUsername());

                /* Upload Image To Parse Server */
                object.saveInBackground((ParseException e) -> {
                    if (e == null) {
                        Toast.makeText(UserList.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserList.this, "Error With Image Upload!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* Transfer Control To The Photo Picker Activity */
    void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
}