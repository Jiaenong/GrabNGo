package com.example.user.grabngo.Admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.Class.Refund;
import com.example.user.grabngo.Class.Supplier;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddSupplierActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final int PICK_IMAGE_REQUEST = 71;

    private EditText editTextName, editTextPhone, editTextEmail, editTextLocation;
    private ImageView imageViewSupplier;
    private Button btnGallery, btnCamera, btnSave;
    private Uri filePath;
    private String pictureFilePath, selectedSupplier, oldImageUrl;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private ProgressBar progressBar;
    private ProgressDialog pDialog;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier);
        AddSupplierActivity.this.setTitle("Add New Supplier");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPhone = (EditText)findViewById(R.id.editTextPhone);
        editTextLocation = (EditText)findViewById(R.id.editTextLocation);
        imageViewSupplier = (ImageView)findViewById(R.id.imageViewSupplier);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        btnGallery = (Button)findViewById(R.id.btn_gallery);
        btnCamera = (Button)findViewById(R.id.btn_camera);
        btnSave = (Button)findViewById(R.id.buttonSave);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Supplier");

        selectedSupplier = getIntent().getStringExtra("supplierID");
        if(selectedSupplier != null){
            linearLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            mCollectionReference.document(selectedSupplier).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Supplier supplier = documentSnapshot.toObject(Supplier.class);
                    oldImageUrl = supplier.getPicUrl();
                    editTextName.setText(supplier.getName());
                    editTextEmail.setText(supplier.getEmail());
                    editTextPhone.setText(supplier.getPhone());
                    editTextLocation.setText(supplier.getLocation());
                    Glide.with(AddSupplierActivity.this).load(supplier.getPicUrl()).into(imageViewSupplier);
                    linearLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
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
                        Toast.makeText(AddSupplierActivity.this, "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (pictureFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(AddSupplierActivity.this,
                                "com.example.user.grabngo",
                                pictureFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
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
                bitmap = MediaStore.Images.Media.getBitmap(AddSupplierActivity.this.getContentResolver(), filePath);
                imageViewSupplier.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            File imgFile = new  File(pictureFilePath);
            if(imgFile.exists())            {
                filePath = Uri.fromFile(imgFile);
                imageViewSupplier.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }

    private void uploadImage() {

        if(editTextName.getText().toString().equals("") || editTextEmail.getText().toString().equals("") || editTextPhone.getText().toString().equals("") || editTextLocation.getText().toString().equals("")){

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddSupplierActivity.this, R.style.AlertDialogCustom));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });
            builder.setTitle("Field cannot be empty");
            builder.setMessage("Please fill in all the textbox provided to proceed");
            AlertDialog alert = builder.create();
            alert.show();
            return;

        }else if(filePath==null && oldImageUrl==null){

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddSupplierActivity.this, R.style.AlertDialogCustom));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });
            builder.setTitle("Image cannot be empty");
            builder.setMessage("Please submit a photo of the supplier logo");
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        pDialog = new ProgressDialog(AddSupplierActivity.this);
        pDialog.setMessage("Saving...");
        pDialog.setCancelable(false);
        pDialog.show();

        if(filePath != null)
        {
            final StorageReference ref = mStorageReference.child("supplier_photo/"+ UUID.randomUUID().toString());

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
                    Toast.makeText(AddSupplierActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        if(selectedSupplier!=null){
                            mStorageReference = mFirebaseStorage.getReferenceFromUrl(oldImageUrl);
                            mStorageReference.delete();
                        }
                        saveChanges(downloadUri.toString());

                    } else {
                        // Handle failures
                        Toast.makeText(AddSupplierActivity.this, "GetDownloadURL fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            saveChanges(oldImageUrl);
        }
    }

    public void saveChanges(String picUrl){

        if(selectedSupplier == null){

            Supplier supplier = new Supplier(editTextName.getText().toString(),
                    editTextEmail.getText().toString(),
                    editTextPhone.getText().toString(),
                    editTextLocation.getText().toString(),
                    picUrl,
                    "");

            mCollectionReference.add(supplier).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(AddSupplierActivity.this, "Supplier successfully added", Toast.LENGTH_SHORT).show();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddSupplierActivity.this, "Unable to upload to firestore", Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{

            mCollectionReference.document(selectedSupplier).update("name",editTextName.getText().toString(),
                    "email",editTextEmail.getText().toString(),
                    "phone",editTextPhone.getText().toString(),
                    "location",editTextLocation.getText().toString(),
                    "picUrl",picUrl)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFirebaseFirestore.collection("Product").whereEqualTo("supplierKey",selectedSupplier).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    WriteBatch batch = mFirebaseFirestore.batch();
                                    for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                        DocumentReference mDocumentReference = documentSnapshot.getReference();
                                        batch.update(mDocumentReference,"producer",editTextName.getText().toString());
                                    }
                                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AddSupplierActivity.this, "Changes has been saved", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });


                        }
                    });

        }

        pDialog.dismiss();
        finish();

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
