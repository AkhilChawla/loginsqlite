package com.example.Blockchain_App.Activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.Blockchain_App.Network.NetworkClient;
import com.example.Blockchain_App.Network.UploadApis;
import com.example.Blockchain_App.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class HomeActivity extends AppCompatActivity
{
    String pathToFile;
    String filePath = "Pictures/";
    private Button btnTakePic, btn_upload;
    private ImageView imageView;
    @RequiresApi(api = Build.VERSION_CODES.M)


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        if (Build.VERSION.SDK_INT >= 23)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        btnTakePic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dispatchPictureTakerAction();
            }
        });


        btn_upload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uploadImage();
            }
        });

    }

    private void uploadImage()
    {
        File file = new File(filePath);
        Retrofit retrofit = NetworkClient.getRetrofit();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("newimage", file.getName(), requestBody);
        RequestBody someData = RequestBody.create(MediaType.parse("text/plain"), "This is a new Image");
        UploadApis uploadApis = retrofit.create(UploadApis.class);

        Call call = uploadApis.uploadImage(parts, someData);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(HomeActivity.this,""+response.message(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(HomeActivity.this,""+ t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void dispatchPictureTakerAction()
    {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();
            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                filePath=pathToFile;
                Uri photoURI = FileProvider.getUriForFile(
                        HomeActivity.this, "com.example.Blockchain_App.fileprovider", photoFile
                );
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePic, 1);
            }
        }
    }

    private File createPhotoFile()
    {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("mylog", "Excep : " + e.toString());
        }
        return image;
    }

    private void init()
    {
        btnTakePic = findViewById(R.id.btnTakePic);
        btn_upload = findViewById(R.id.btn_upload);
        imageView = findViewById(R.id.img_clicked);
    }
}
















