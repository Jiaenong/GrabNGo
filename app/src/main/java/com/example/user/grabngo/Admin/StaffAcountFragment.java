package com.example.user.grabngo.Admin;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Staff;
import com.example.user.grabngo.LoginActivity;
import com.example.user.grabngo.R;
import com.example.user.grabngo.SaveSharedPreference;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffAcountFragment extends Fragment {

    private final int PICK_IMAGE_REQUEST = 71;

    private NavigationView navigationView;
    private ImageView imageViewProfile;
    private EditText editTextName, editTextPhone, editTextAddress, editTextOldPassword, editTextNewPassword, editTextRetypePassword;
    private CardView cardViewProfile, cardViewEdit, cardViewChangePassword;
    private ImageButton btnEditProfile, btnChangePassword, btnLogout;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutAccount;
    private TextView textViewName, textViewEmail, textViewPhone, textViewAddress;
    private Button btnEditCancel, btnEditSave, btnChangeCancel, btnChangeSave;
    private Staff staff;
    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference mDocumentReference;
    private ProgressDialog pDialog;
    private Uri filePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_staff_acount, container, false);

        getActivity().setTitle("Account");
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(4).setChecked(true);

        imageViewProfile = (ImageView)v.findViewById(R.id.imageViewProfile);
        textViewName = (TextView)v.findViewById(R.id.textViewName);
        textViewEmail = (TextView)v.findViewById(R.id.textViewEmail);
        textViewPhone = (TextView)v.findViewById(R.id.textViewPhone);
        textViewAddress = (TextView)v.findViewById(R.id.textViewAddress);
        editTextName = (EditText)v.findViewById(R.id.editTextName);
        editTextPhone = (EditText)v.findViewById(R.id.editTextPhone);
        editTextAddress = (EditText)v.findViewById(R.id.editTextAddress);
        editTextOldPassword = (EditText)v.findViewById(R.id.editTextOldPassword);
        editTextNewPassword = (EditText)v.findViewById(R.id.editTextNewPassword);
        editTextRetypePassword = (EditText)v.findViewById(R.id.editTextRetypePassword);
        btnEditCancel = (Button) v.findViewById(R.id.btn_cancel);
        btnEditSave = (Button) v.findViewById(R.id.btn_save);
        btnChangeCancel = (Button)v.findViewById(R.id.btn_pass_cancel);
        btnChangeSave = (Button)v.findViewById(R.id.btn_pass_save);
        btnEditProfile = (ImageButton)v.findViewById(R.id.btn_edit_profile);
        btnChangePassword = (ImageButton)v.findViewById(R.id.btn_forget_password);
        btnLogout = (ImageButton)v.findViewById(R.id.btn_logout);
        cardViewEdit = (CardView)v.findViewById(R.id.cardviewEdit);
        cardViewProfile = (CardView)v.findViewById(R.id.cardviewProfile);
        cardViewChangePassword = (CardView)v.findViewById(R.id.cardviewChangePassword);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBarAccount);
        linearLayoutAccount = (LinearLayout)v.findViewById(R.id.linearLayoutAccount);

        progressBar.setVisibility(View.VISIBLE);
        linearLayoutAccount.setVisibility(View.GONE);

        String id = SaveSharedPreference.getID(getContext());
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mDocumentReference = mFirebaseFirestore.document("Staff/"+id);

        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                staff = documentSnapshot.toObject(Staff.class);
                Glide.with(getActivity()).load(staff.getProfileUrl()).into(imageViewProfile);
                textViewName.setText("Name  : "+staff.getName());
                textViewPhone.setText("Phone : "+staff.getPhone());
                textViewEmail.setText("Email : "+staff.getEmail());
                textViewAddress.setText("Address: "+staff.getAddress());
                progressBar.setVisibility(View.GONE);
                linearLayoutAccount.setVisibility(View.VISIBLE);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewEdit.setVisibility(View.VISIBLE);
                cardViewProfile.setVisibility(View.GONE);
                cardViewChangePassword.setVisibility(View.GONE);
                editTextName.setText(staff.getName());
                editTextPhone.setText(staff.getPhone());
                editTextAddress.setText(staff.getAddress());
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewEdit.setVisibility(View.GONE);
                cardViewProfile.setVisibility(View.GONE);
                cardViewChangePassword.setVisibility(View.VISIBLE);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Do you want to log out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaveSharedPreference.clearUser(getContext());
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btnEditCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewEdit.setVisibility(View.GONE);
                cardViewProfile.setVisibility(View.VISIBLE);
            }
        });

        btnChangeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextOldPassword.setText("");
                editTextNewPassword.setText("");
                editTextRetypePassword.setText("");
                cardViewChangePassword.setVisibility(View.GONE);
                cardViewProfile.setVisibility(View.VISIBLE);
            }
        });

        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Saving...");
                pDialog.setCancelable(false);
                pDialog.show();

                final String name = editTextName.getText().toString();
                final String phone = editTextPhone.getText().toString();
                final String address = editTextAddress.getText().toString();

                mDocumentReference.update("name",name,
                        "phone", phone,
                        "address",address).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),"Changes has been save",Toast.LENGTH_SHORT).show();
                        staff.setName(name);
                        staff.setPhone(phone);
                        staff.setAddress(address);
                        textViewName.setText("Name  : "+staff.getName());
                        textViewPhone.setText("Phone : "+staff.getPhone());
                        textViewEmail.setText("Email : "+staff.getEmail());
                        textViewAddress.setText("Address: "+staff.getAddress());
                        cardViewEdit.setVisibility(View.GONE);
                        cardViewProfile.setVisibility(View.VISIBLE);
                        pDialog.dismiss();

                    }
                });
            }
        });

        btnChangeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String oldPass = editTextOldPassword.getText().toString();
                final String newPass = editTextNewPassword.getText().toString();
                final String retypePass = editTextRetypePassword.getText().toString();

                if(oldPass.equals("")||newPass.equals("")||retypePass.equals("")) {
                    Toast.makeText(getActivity(),"Please fill in all the field to proceed",Toast.LENGTH_SHORT).show();
                    return;
                }else if(!oldPass.equals(staff.getPassword())){
                    Toast.makeText(getActivity(),"Old Password is incorrect",Toast.LENGTH_SHORT).show();
                    return;
                }else if(!newPass.equals(retypePass)){
                    Toast.makeText(getActivity(),"Confirm New Password does not match",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Saving...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    mDocumentReference.update("password",newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            staff.setPassword(newPass);
                            Toast.makeText(getActivity(),"Password has been changed",Toast.LENGTH_SHORT).show();
                            editTextOldPassword.setText("");
                            editTextNewPassword.setText("");
                            editTextRetypePassword.setText("");
                            pDialog.dismiss();
                            cardViewChangePassword.setVisibility(View.GONE);
                            cardViewProfile.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            filePath = data.getData();

            if (filePath != null) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Uploading...");
                pDialog.setCancelable(false);
                pDialog.show();

                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("profile_photo/" + UUID.randomUUID().toString());

                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data1 = baos.toByteArray();

                //uploading the image
                UploadTask uploadTask2 = ref.putBytes(data1);
                uploadTask2.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            mDocumentReference.update("profileUrl",downloadUri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pDialog.dismiss();
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                                        imageViewProfile.setImageBitmap(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {
                            // Handle failures
                            Toast.makeText(getActivity(), "GetDownloadURL fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }

}
