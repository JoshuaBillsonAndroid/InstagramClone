package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        linearLayout = findViewById(R.id.feedLinearLayout);

        /* Grab All Images Associated With User */
        ParseQuery<ParseObject> query = new ParseQuery<>("Image");
        query.whereEqualTo("user", getIntent().getStringExtra("username"));
        query.orderByAscending("createdAt");

        /* Add All Images To Linear Layout */
        query.findInBackground((List<ParseObject> objects, ParseException e) -> {
            if (e == null && objects.size() > 0) {
                for (ParseObject object : objects) {
                    ParseFile parseFile = object.getParseFile("image");
                    if (parseFile != null) {
                        parseFileToImage(parseFile);
                    }
                }
            }
        });
    }

    /* Convert A ParseFile To An Image View */
    public void parseFileToImage(ParseFile file) {
        file.getDataInBackground((byte[] data, ParseException parseException) -> {
            if (parseException == null && data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                addImageView(bitmap);
            } else if (parseException != null) {
                parseException.printStackTrace();
            }
        });

    }

    /* Add Image View To Linear Layout */
    public void addImageView(Bitmap bitmap) {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(0, 0, 0, 75);
        imageView.setImageBitmap(bitmap);
        linearLayout.addView(imageView);
    }
}