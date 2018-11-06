package com.example.user.grabngo.Admin;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.user.grabngo.Class.Refund;
import com.example.user.grabngo.Class.Staff;
import com.example.user.grabngo.LoginActivity;
import com.example.user.grabngo.R;
import com.example.user.grabngo.SaveSharedPreference;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class AddRefundActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String DIALOG_DATE = "DialogDate";
    private final int PICK_IMAGE_REQUEST = 71;

    private EditText editTextProduct, editTextCustomer, editTextReason, editTextDate, editTextTime;
    private ImageView imageViewProduct;
    private Button btnGallery, btnCamera, btnSave;
    private Uri filePath;
    private String pictureFilePath;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private ProgressDialog pDialog;
    private Staff staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_refund);
        AddRefundActivity.this.setTitle("Add New Refund");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextProduct = (EditText)findViewById(R.id.editTextProductName);
        editTextCustomer = (EditText)findViewById(R.id.editTextCustomerName);
        editTextReason = (EditText)findViewById(R.id.editTextReason);
        editTextDate = (EditText)findViewById(R.id.editTextExpiry);
        editTextTime = (EditText)findViewById(R.id.editTextRefundTime);
        imageViewProduct = (ImageView)findViewById(R.id.product_image);
        btnGallery = (Button)findViewById(R.id.btn_gallery);
        btnCamera = (Button)findViewById(R.id.btn_camera);
        btnSave = (Button)findViewById(R.id.buttonSave);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Refund");

        String id = SaveSharedPreference.getID(AddRefundActivity.this);
        FirebaseFirestore.getInstance().collection("Staff").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                staff = documentSnapshot.toObject(Staff.class);

            }
        });

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        editTextDate.setText(df.format(new Date()));
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date());

                dialog.show(manager,DIALOG_DATE);
            }
        });

        final DateFormat df1 = new SimpleDateFormat("hh:mm a");
        editTextTime.setText(df1.format(new Date()));
        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date1 = null;
                try {
                    date1 = df1.parse(editTextTime.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar mcurrentTime = Calendar.getInstance();
                mcurrentTime.setTime(date1);
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddRefundActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String s = "" + selectedHour + ":" + selectedMinute;
                        DateFormat df2 = new SimpleDateFormat("HH:mm");
                        Date date = null;
                        try {
                            date = df2.parse(s);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        editTextTime.setText(df1.format(date));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

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
                        Toast.makeText(AddRefundActivity.this, "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (pictureFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(AddRefundActivity.this,
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
                bitmap = MediaStore.Images.Media.getBitmap(AddRefundActivity.this.getContentResolver(), filePath);
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
        }
    }

    private void uploadImage() {

        if(!validation()){
            return;
        }

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if(filePath != null)
        {
            pDialog = new ProgressDialog(AddRefundActivity.this);
            pDialog.setMessage("Saving...");
            pDialog.setCancelable(false);
            pDialog.show();

            final StorageReference ref = mStorageReference.child("refund_photo/"+ UUID.randomUUID().toString());

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
                    Toast.makeText(AddRefundActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        String dateString = editTextDate.getText().toString() + " " + editTextTime.getText().toString();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                        Calendar calendar = Calendar.getInstance();
                        try {
                            Date date = dateFormat.parse(dateString);
                            calendar.setTime(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        Refund refund = new Refund(editTextCustomer.getText().toString(),
                                downloadUri.toString(),
                                null,
                                staff.getName(),
                                editTextProduct.getText().toString(),
                                editTextReason.getText().toString(),
                                calendar.getTime());

                        mCollectionReference.add(refund).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddRefundActivity.this, "Refund successfully added", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                finish();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddRefundActivity.this, "Unable to upload to firestore", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        // Handle failures
                        Toast.makeText(AddRefundActivity.this, "GetDownloadURL fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public boolean validation(){
        boolean valid = false;

        if(editTextProduct.getText().toString().equals("") || editTextCustomer.getText().toString().equals("") || editTextReason.getText().toString().equals("")){
            Toast.makeText(AddRefundActivity.this,"All field cannot be empty",Toast.LENGTH_SHORT).show();
        }else if(filePath==null){
            Toast.makeText(AddRefundActivity.this,"Please insert an image",Toast.LENGTH_SHORT).show();
        }else {
            valid=true;
        }

        return valid;
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
