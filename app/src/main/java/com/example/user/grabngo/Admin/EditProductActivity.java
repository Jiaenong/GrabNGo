package com.example.user.grabngo.Admin;

import android.app.ProgressDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.Class.Staff;
import com.example.user.grabngo.Class.Supplier;
import com.example.user.grabngo.R;
import com.example.user.grabngo.SaveSharedPreference;
import com.example.user.grabngo.ScanBarcodeActivity;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EditProductActivity extends AppCompatActivity {

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
    private String pictureFilePath, oldImgUrl, selectedProduct,productName;
    private EditText editTextName, editTextPrice, editTextExpiredDate, editTextAmount, editTextLocation, editTextBarcode;
    private Spinner spinnerCategory, spinnerProducer;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private Staff staff;
    private String supplierKey;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private DocumentReference mDocumentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        EditProductActivity.this.setTitle("Edit Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewProduct = (ImageView)findViewById(R.id.product_image);
        btnSubmit = (Button)findViewById(R.id.buttonSubmit);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnGallery = (Button) findViewById(R.id.btn_gallery);
        spinnerCategory = (Spinner)findViewById(R.id.spinner_category);
        spinnerProducer = (Spinner)findViewById(R.id.spinner_producer);
        editTextName = (EditText)findViewById(R.id.editTextProductName);
        editTextPrice = (EditText)findViewById(R.id.editTextPrice);
        editTextAmount = (EditText)findViewById(R.id.editTextAmount);
        editTextExpiredDate = (EditText)findViewById(R.id.editTextExpiry);
        editTextLocation = (EditText)findViewById(R.id.editTextLocation);

        editTextBarcode = (EditText)findViewById(R.id.editTextBarcode);
        imageButtonBarcode = (ImageButton)findViewById(R.id.imageButtonBarcode);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");

        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditProductActivity.this ,R.array.product_category, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        Intent intent = getIntent();
        selectedProduct = intent.getStringExtra("productID");

        String id = SaveSharedPreference.getID(EditProductActivity.this);

        FirebaseFirestore.getInstance().collection("Staff").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                staff = documentSnapshot.toObject(Staff.class);
            }
        });

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinnerProducer);
            // Set popupWindow height to 500px
            popupWindow.setHeight(600);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        mFirebaseFirestore.collection("Supplier").orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> supplierNameList = new ArrayList<>();
                List<String> supplierRefList = new ArrayList<>();
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Supplier supplier = documentSnapshot.toObject(Supplier.class);
                    supplierNameList.add(supplier.getName());
                    supplierRefList.add(documentSnapshot.getId().toString());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProductActivity.this, R.layout.support_simple_spinner_dropdown_item, supplierNameList);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinnerProducer.setAdapter(adapter);

                spinnerProducer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        supplierKey = supplierRefList.get(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                mCollectionReference.document(selectedProduct).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        Product product = documentSnapshot.toObject(Product.class);

                        productName = product.getProductName();
                        oldImgUrl = product.getImageUrl();
                        editTextBarcode.setText(product.getBarcode());
                        editTextName.setText(product.getProductName());
                        editTextPrice.setText(product.getPrice());
                        for(int i =0; i<spinnerCategory.getAdapter().getCount(); i++){
                            if(spinnerCategory.getItemAtPosition(i).equals(product.getCategory()))
                                spinnerCategory.setSelection(i);
                        }
                        for(int i =0; i<spinnerProducer.getAdapter().getCount(); i++){
                            if(spinnerProducer.getItemAtPosition(i).equals(product.getProducer()))
                                spinnerProducer.setSelection(i);
                        }
                        editTextExpiredDate.setText(product.getExpired());
                        editTextAmount.setText(""+product.getStockAmount());
                        editTextLocation.setText(product.getShelfLocation());
                        Glide.with(EditProductActivity.this).load(product.getImageUrl()).into(imageViewProduct);

                    }
                });
            }
        });



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
                Intent intent = new Intent(EditProductActivity.this, ScanBarcodeActivity.class);
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
                        Toast.makeText(EditProductActivity.this, "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (pictureFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(EditProductActivity.this,
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
                bitmap = MediaStore.Images.Media.getBitmap(EditProductActivity.this.getContentResolver(), filePath);
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
            Toast.makeText(EditProductActivity.this, barcode, Toast.LENGTH_SHORT).show();
            editTextBarcode.setText(barcode);

        }
    }

    private void uploadImage() {

        if(!validation()){
            return;
        }

        mCollectionReference.whereEqualTo("productName",editTextName.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Product product = queryDocumentSnapshots.getDocuments().get(0).toObject(Product.class);

                if(!queryDocumentSnapshots.isEmpty() && !productName.equals(product.getProductName())){
                    Toast.makeText(EditProductActivity.this,"Product name cannot be the same",Toast.LENGTH_SHORT).show();
                }else{
                    pDialog = new ProgressDialog(EditProductActivity.this);
                    pDialog.setMessage("Saving...");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    if (filePath != null) {
                        final StorageReference ref = mStorageReference.child("product_photo/" + UUID.randomUUID().toString());

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
                                Toast.makeText(EditProductActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                    saveChanges(downloadUri.toString());
                                } else {
                                    // Handle failures
                                    Toast.makeText(EditProductActivity.this, "GetDownloadURL fail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        saveChanges(oldImgUrl);
                    }
                }
            }
        });

    }

    public boolean validation(){

        boolean valid = false;

        String barcode = editTextBarcode.getText().toString();
        String productName = editTextName.getText().toString();
        String price = editTextPrice.getText().toString();
        String amount = editTextAmount.getText().toString();
        String location = editTextLocation.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR,0);
        now.set(Calendar.MINUTE,0);
        now.set(Calendar.SECOND,0);

        Calendar expiredDate = Calendar.getInstance();
        try {
            expiredDate.setTime(dateFormat.parse(editTextExpiredDate.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(barcode.equals("") || productName.equals("") || price.equals("") || amount.equals("") || location.equals("")){
            Toast.makeText(EditProductActivity.this,"All field cannot be empty",Toast.LENGTH_SHORT).show();
        }else{
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            if(expiredDate.getTime().equals(now.getTime()) || expiredDate.getTime().before(now.getTime())){
                Toast.makeText(EditProductActivity.this,"Expired date is invalid",Toast.LENGTH_SHORT).show();
            }else {
                valid=true;
            }

        }

        return valid;
    }

    public void saveChanges(String picURL){

        mCollectionReference.document(selectedProduct).update("productName", editTextName.getText().toString(),
                "price", editTextPrice.getText().toString(),
                "barcode", editTextBarcode.getText().toString(),
                "category", spinnerCategory.getSelectedItem().toString(),
                "producer", spinnerProducer.getSelectedItem().toString(),
                "expired", editTextExpiredDate.getText().toString(),
                "imageUrl", picURL,
                "shelfLocation", editTextLocation.getText().toString(),
                "stockAmount", Integer.parseInt(editTextAmount.getText().toString()),
                "modifiedStaffName", staff.getName(),
                "modifiedDate", FieldValue.serverTimestamp(),
                "supplierKey", supplierKey)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProductActivity.this, "Changes has been saved", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                        finish();
                    }
                });

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
