package com.example.user.grabngo.Admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.R;
import com.example.user.grabngo.ScanBarcodeActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAPTURE_BARCODE = 2;
    private final int PICK_IMAGE_REQUEST = 71;
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    public static final int ADDPRODUCTACTIVITY = 101;

    private ImageButton imageButtonBarcode;
    private ProgressDialog pDialog;
    private ImageView imageViewProduct;
    private Button btnSubmit, btnCamera, btnGallery;
    private Uri filePath;
    private String pictureFilePath;
    private EditText editTextName, editTextPrice, editTextProducer, editTextExpiredDate, editTextAmount, editTextLocation, editTextBarcode;
    private Spinner spinnerCategory;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        imageViewProduct = (ImageView)findViewById(R.id.product_image);
        btnSubmit = (Button)findViewById(R.id.buttonSubmit);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnGallery = (Button) findViewById(R.id.btn_gallery);
        spinnerCategory = (Spinner)findViewById(R.id.spinner_category);
        editTextName = (EditText)findViewById(R.id.editTextProductName);
        editTextPrice = (EditText)findViewById(R.id.editTextPrice);
        editTextAmount = (EditText)findViewById(R.id.editTextAmount);
        editTextExpiredDate = (EditText)findViewById(R.id.editTextExpiry);
        editTextLocation = (EditText)findViewById(R.id.editTextLocation);
        editTextProducer = (EditText)findViewById(R.id.editTextProducer);
        editTextBarcode = (EditText)findViewById(R.id.editTextBarcode);
        imageButtonBarcode = (ImageButton)findViewById(R.id.imageButtonBarcode);

        AddProductActivity.this.setTitle("Add new product");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddProductActivity.this ,R.array.product_category, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        editTextExpiredDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date());

                dialog.show(manager,DIALOG_DATE);
            }
        });

        imageButtonBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProductActivity.this, ScanBarcodeActivity.class);
                intent.putExtra("callingActivity",ADDPRODUCTACTIVITY);
                startActivityForResult(intent,CAPTURE_BARCODE);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                    File pictureFile = null;
                    try {
                        pictureFile = getPictureFile();
                    } catch (IOException ex) {
                        Toast.makeText(AddProductActivity.this, "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (pictureFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(AddProductActivity.this,
                                "com.example.user.grabngo",
                                pictureFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "PRODUCT_IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            filePath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(AddProductActivity.this.getContentResolver(), filePath);
                imageViewProduct.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            File imgFile = new  File(pictureFilePath);
            if(imgFile.exists())            {
                filePath = Uri.fromFile(imgFile);
                imageViewProduct.setImageURI(Uri.fromFile(imgFile));
            }
        }else if(requestCode == CAPTURE_BARCODE && resultCode == RESULT_OK){

            String barcode = data.getStringExtra("result");
            Toast.makeText(AddProductActivity.this, barcode, Toast.LENGTH_SHORT).show();
            editTextBarcode.setText(barcode);

        }
    }

    private void uploadImage() {

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if(filePath != null)
        {
            pDialog = new ProgressDialog(AddProductActivity.this);
            pDialog.setMessage("Saving...");
            pDialog.setCancelable(false);
            pDialog.show();

            final StorageReference ref = mStorageReference.child("product_photo/"+ UUID.randomUUID().toString());

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            //uploading the image
            UploadTask uploadTask2 = ref.putBytes(data);
            uploadTask2.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProductActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            Task<Uri> urlTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddProductActivity.this);

                        Product product = new Product(editTextBarcode.getText().toString(),
                                editTextName.getText().toString(),
                                editTextProducer.getText().toString(),
                                editTextPrice.getText().toString(),
                                spinnerCategory.getSelectedItem().toString(),
                                editTextExpiredDate.getText().toString(),
                                editTextLocation.getText().toString(),
                                Integer.parseInt(editTextAmount.getText().toString()),
                                downloadUri.toString(),
                                preferences.getString("username","DEFAULT"),
                                null);

                        mCollectionReference.add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddProductActivity.this, "Product successfully added", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddProductActivity.this, "Unable to upload to firestore", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        // Handle failures
                        Toast.makeText(AddProductActivity.this, "GetDownloadURL fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
