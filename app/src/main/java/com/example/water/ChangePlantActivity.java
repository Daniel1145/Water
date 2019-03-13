package com.example.water;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import androidx.exifinterface.media.ExifInterface;

import static com.example.water.MainActivity.REQUEST_ID_MULTIPLE_PERMISSIONS;

public class ChangePlantActivity extends AppCompatActivity {

    final int TAKE_PHOTO_CODE = 1;
    final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_plant);
        EditText plantName = findViewById(R.id.new_plant_name);
        EditText plantSpecies = findViewById(R.id.new_plant_species);
        EditText plantWaterSchedule = findViewById(R.id.new_plant_water_schedule);
        ImageView plantPic = findViewById(R.id.new_plant_pic);

        Plant p = getIntent().getParcelableExtra(MainActivity.EXTRA_PLANT);
        plantName.setText(p.getName());
        plantSpecies.setText(p.getSpecies());
        plantWaterSchedule.setText(String.valueOf(p.getWaterSchedule()));

        if (p.getPic() != null) {
            Glide.with(this).load(Uri.parse(p.getPic())).placeholder(R.drawable.ic_launcher_background).into(plantPic);
        }

        position = getIntent().getIntExtra(MainActivity.POSITION, 0);
    }

    public void onClick(View view){
        EditText waterScheduleEditText = findViewById(R.id.new_plant_water_schedule);
        String temp = waterScheduleEditText.getText().toString();
        if (temp.matches("")){
            Toast.makeText(this, "Please enter the plant's watering schedule", Toast.LENGTH_SHORT).show();
        } else {
            int waterSchedule = Integer.parseInt(temp);
            if (waterSchedule <= 0) {
                Toast.makeText(this, "The plant's watering schedule must be at least 1 day", Toast.LENGTH_SHORT).show();
            } else {
                EditText nameEditText = findViewById(R.id.new_plant_name);
                String name = nameEditText.getText().toString();

                EditText speciesEditText = findViewById(R.id.new_plant_species);
                String species = speciesEditText.getText().toString();

                Plant plant = new Plant(name, species, waterSchedule);

                File from = new File(dir, "tempc.jpg");
                File to = new File(dir, name + species + ".jpg");

                if (from.canRead()) {
                    if (to.exists()) to.delete();
                    from.renameTo(to);

                    Uri picUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".com.example.water.provider", to);
                    plant.setPic(picUri.toString());
                    deleteTempPic();
                } else {
                    if (to.exists()){
                        Uri picUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".com.example.water.provider", to);
                        plant.setPic(picUri.toString());
                    }
                }

                Intent output = new Intent();
                output.putExtra(MainActivity.EXTRA_PLANT, plant);
                output.putExtra(MainActivity.POSITION, position);
                setResult(RESULT_OK, output);
                finish();
            }
        }
    }

    public void capture(View view) {
        Uri outputFileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".com.example.water.provider", createImageFile());

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        List<String> permissionList = PermissionsUtil.checkAndRequestPermissions(this);

        if (permissions(permissionList)) {
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            File file = new File(dir, "tempc.jpg");
            Uri outputFileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".com.example.water.provider", file);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),outputFileUri);
                bitmap = rotateImageIfRequired(this, bitmap, outputFileUri);
                ImageView pic = findViewById(R.id.new_plant_pic);
                pic.setImageBitmap(bitmap);

                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {

            }
        }
    }

    public void onClickDelete(View view){
        Intent output = new Intent();
        output.putExtra(MainActivity.POSITION, position);
        setResult(RESULT_OK, output);
        finish();
    }

    private File createImageFile(){
        String file = dir+"tempc.jpg";
        File newFile = new File(file);
        try {
            newFile.createNewFile();
        } catch(IOException e){}

        return newFile;
    }

    private boolean permissions(List<String> listPermissionsNeeded) {
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private void deleteTempPic() {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/picFolder");
        File file = new File(path, "tempc.jpg");
        file.delete();
    }
}
