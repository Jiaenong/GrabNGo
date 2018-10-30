package com.example.user.grabngo;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;

public class PostActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 2;

    private EditText editTextContent;
    private ImageView imageViewPostPic, imageViewSelectPhoto, imageViewUndo;
    private Button btnPost;
    private ProgressBar progressBarPost;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private DocumentReference mDocumentReference;

    private String image, id;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBarPost.setVisibility(View.VISIBLE);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
        {
            Uri selectedImageUri = data.getData();
            final StorageReference photoref = mStorageReference.child(selectedImageUri.getLastPathSegment());
            Toast.makeText(PostActivity.this,"Uploading", Toast.LENGTH_LONG).show();
            photoref.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        image = downloadUri.toString();
                        progressBarPost.setVisibility(View.GONE);
                        imageViewPostPic.setVisibility(View.VISIBLE);
                        Glide.with(PostActivity.this).load(image).into(imageViewPostPic);
                    }else{
                        Toast.makeText(PostActivity.this,"Upload Failed: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        final String time = intent.getStringExtra("time");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("post_photo");
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Post");

        editTextContent = (EditText)findViewById(R.id.editTextContent);
        imageViewPostPic = (ImageView)findViewById(R.id.imageViewPostPic);
        imageViewSelectPhoto = (ImageView)findViewById(R.id.imageViewSelectPhoto);
        imageViewUndo = (ImageView)findViewById(R.id.imageViewUndo);
        btnPost = (Button)findViewById(R.id.btnPost);
        progressBarPost = (ProgressBar)findViewById(R.id.progressBarPost);
        progressBarPost.setVisibility(View.GONE);

        imageViewSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent,"Complete the action using"), RC_PHOTO_PICKER);
            }
        });
        imageViewUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image = null;
                imageViewPostPic.setVisibility(View.GONE);
            }
        });

        if(time != null)
        {
            mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    Log.i("Testing ", "hello");
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    {
                        Log.i("Testing2 ","Hello");
                        Post post = documentSnapshot.toObject(Post.class);
                        String timesss = post.getPostDate()+"";
                        if(time.equals(timesss))
                        {
                            String content = post.getContent();
                            String picture = post.getPostImage();
                            id = documentSnapshot.getId();
                            editTextContent.setText(content);
                            Glide.with(PostActivity.this).load(picture).into(imageViewPostPic);
                            imageViewPostPic.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time != null) {
                    mDocumentReference = mFirebaseFirestore.document("Post/"+id);
                    mDocumentReference.update("content", editTextContent.getText().toString());
                    mDocumentReference.update("postImage",image);
                    finish();
                    Toast.makeText(PostActivity.this,"Edit Successfully",Toast.LENGTH_LONG).show();

                } else {
                    Calendar calendar = Calendar.getInstance();
                    java.sql.Timestamp currentTimeStamp = new java.sql.Timestamp(calendar.getTime().getTime());
                    String id = SaveSharedPreference.getID(PostActivity.this);
                    String content = editTextContent.getText().toString();
                    Post post = new Post(id, image, content, currentTimeStamp);
                    mCollectionReference.add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            finish();
                            Toast.makeText(PostActivity.this, "Post added successfully", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
