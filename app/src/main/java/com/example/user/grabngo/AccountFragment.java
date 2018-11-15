package com.example.user.grabngo;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Customer;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private ImageButton btnPurchaseHistory, btnEditProfile, btnForgetPassword, btnLogout;
    private TextView textViewName, textViewGender, textViewEmail, textViewAddress, textViewUploadPic;
    private ImageView imageViewProfilePic;
    private ProgressBar progressBarAccount;
    private LinearLayout linearLayoutAccount;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseStorage mFirebaseStorage;
    private DocumentReference mDocumentReference;
    private StorageReference mStorageReference;

    public HomeActivity homeActivity;

    Context thiscontext;

    private static final int RC_PHOTO_PICKER = 2;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
        {
            Uri seletedImageUri = data.getData();
            final StorageReference photoref = mStorageReference.child(seletedImageUri.getLastPathSegment());
            Toast.makeText(getContext(),"Uploading", Toast.LENGTH_LONG).show();
            photoref.putFile(seletedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return photoref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        mDocumentReference.update("profilePic",downloadUri.toString());
                        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Customer customer = documentSnapshot.toObject(Customer.class);
                                Glide.with(getActivity()).load(customer.getProfilePic()).into(imageViewProfilePic);
                                textViewUploadPic.setText("Choose other Picture");
                            }
                        });
                    }
                    else{
                        Toast.makeText(getActivity(),"Upload Failed: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        setHasOptionsMenu(true);
        thiscontext = container.getContext();
        String id = SaveSharedPreference.getID(getContext());
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDocumentReference = mFirebaseFirestore.document("Customer/" + id);
        mStorageReference = mFirebaseStorage.getReference().child("profile_photo");

        textViewName = (TextView) v.findViewById(R.id.textViewName);
        textViewGender = (TextView) v.findViewById(R.id.textViewGender);
        textViewEmail = (TextView) v.findViewById(R.id.textViewEmail);
        textViewAddress = (TextView) v.findViewById(R.id.textViewAddress);
        textViewUploadPic = (TextView) v.findViewById(R.id.textViewUploadPic);
        imageViewProfilePic = (ImageView) v.findViewById(R.id.imageViewProfilePic);
        btnPurchaseHistory = (ImageButton) v.findViewById(R.id.btn_purchase_history);
        btnEditProfile = (ImageButton) v.findViewById(R.id.btn_edit_profile);
        btnForgetPassword = (ImageButton) v.findViewById(R.id.btn_forget_password);
        btnLogout = (ImageButton) v.findViewById(R.id.btn_logout);
        progressBarAccount = (ProgressBar) v.findViewById(R.id.progressBarAccount);
        linearLayoutAccount = (LinearLayout) v.findViewById(R.id.linearLayoutAccount);

        final FragmentManager fm = getFragmentManager();
        homeActivity = (HomeActivity) getActivity();
        if (homeActivity.tag != null) {
            Fragment forgetPasswordFragment = new ForgetPasswordFragment();
            fm.beginTransaction().replace(R.id.fragment_container, forgetPasswordFragment).commit();
        } else {
            progressBarAccount.setVisibility(View.VISIBLE);
            linearLayoutAccount.setVisibility(View.GONE);

            mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Customer customer = documentSnapshot.toObject(Customer.class);
                    textViewName.setText("Name: " + customer.getName());
                    textViewGender.setText("Gender: " + customer.getGender());
                    textViewEmail.setText("Email: " + customer.getEmail());
                    textViewAddress.setText("Address: " + customer.getAddress());
                    if (customer.getProfilePic().equals("")) {
                        imageViewProfilePic.setImageResource(R.drawable.user);
                    } else {
                        Glide.with(thiscontext).load(customer.getProfilePic()).into(imageViewProfilePic);
                        textViewUploadPic.setText("Choose other Picture");
                    }
                    progressBarAccount.setVisibility(View.GONE);
                    linearLayoutAccount.setVisibility(View.VISIBLE);
                }
            });
        }

        imageViewProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete the action using"), RC_PHOTO_PICKER);
            }
        });

        btnPurchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PurchaseHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment editProfileFragment = new EditProfileFragment();
                fm.beginTransaction().replace(R.id.fragment_container, editProfileFragment).commit();
            }
        });

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment forgetPasswordFragment = new ForgetPasswordFragment();
                fm.beginTransaction().replace(R.id.fragment_container, forgetPasswordFragment).commit();
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
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
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


        return v;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(Build.VERSION.SDK_INT > 11) {
            getActivity().invalidateOptionsMenu();
            MenuItem itemCart = menu.findItem(R.id.cart);
            LayerDrawable icon = (LayerDrawable)itemCart.getIcon();
            setBadgeCount(getActivity(),icon, GlobalVars.cartCount+"");
        }
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                startActivity(new Intent(getActivity(),CartActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cart,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);

        return;
    }

}
