package edu.miracostacollege.cs134.petprotector;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView petImageView;
    public static final int RESULT_LOAD_IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect the petImage view to the layout
        // set Image URI on the patImageView
        petImageView = findViewById(R.id.petImageView);
        petImageView.setImageURI(getUriToResource(this, R.drawable.none));

        petImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPetImage(v);
            }
        });
    }

    private static Uri getUriToResource(Context ctx, int id) {

        Resources res = ctx.getResources();

        String uri = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + res.getResourcePackageName(id) + "/"
                    + res.getResourceTypeName(id) + "/"
                    + res.getResourceEntryName((id));

        return Uri.parse(uri);

    }

    public void selectPetImage(View v) {


        // 1) Make a list of permissions
        // 2) As user grants them, add each permission to the list
        List<String> permsList = new ArrayList<>();
        int permReqCode = 100;
        int hasCameraPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        // Check to see if camera access is denied
        // If it is, add it to the list of permissions required
        if (hasCameraPerm == PackageManager.PERMISSION_DENIED) {
            permsList.add(Manifest.permission.CAMERA);
        }

        int hasExtStoragePerm =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        // do the same thing here and check to see if the user denied access to their external storage
        if (hasExtStoragePerm == PackageManager.PERMISSION_DENIED) {
            permsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // now to do it with writing permissions
        int hasWriteToExtStoragePerm =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteToExtStoragePerm == PackageManager.PERMISSION_DENIED) {
            permsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        // Now that we've built the list, let's ask the user
        if (permsList.size() > 0) {

            // convert the list to an array
            String[] perms = new String[permsList.size()];
            permsList.toArray(perms);

            // request permissions with backwards compatibility
            ActivityCompat.requestPermissions(this, perms, permReqCode);

        }




        // AFTER REQUESTING PERMISSION -
        //      Find out which ones the user granted
        //      Check to see if ALL permissions were granted
        if (hasCameraPerm == PackageManager.PERMISSION_GRANTED &&
            hasExtStoragePerm == PackageManager.PERMISSION_GRANTED &&
            hasWriteToExtStoragePerm == PackageManager.PERMISSION_GRANTED)
        {
            // Open the Gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        }
        else {
            // Toast informing user that we need the permissions to continue
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_LOAD_IMAGE) {
            Uri uri = data.getData();
            petImageView.setImageURI(uri);
        }

    }
}
