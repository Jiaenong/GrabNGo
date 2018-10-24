package com.example.user.grabngo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.example.user.grabngo.Class.Comment;
import com.example.user.grabngo.Class.Customer;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class CommentActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 3;

    private RecyclerView recyclerViewComment;
    private EditText messageEditText;
    private Button sendButton;
    private ProgressBar progressBarComment;

    private List<Comment> listComment;
    private CommentAdapter adapter;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;

    private String image;

    @Override
    protected void onStart() {
        super.onStart();
        mCollectionReference.orderBy("postTime",Query.Direction.ASCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                listComment.clear();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(documentSnapshot.exists()) {
                        Comment comment = documentSnapshot.toObject(Comment.class);
                        String commentContent = comment.getCommentContent();
                        String customerKey = comment.getCustomerKey();
                        Date time = comment.getPostTime();
                        Comment comment1 = new Comment(customerKey, time, commentContent);
                        listComment.add(comment1);
                    }else if(e != null)
                    {
                        Log.w("YourTag", "Listen failed.", e);
                        return;
                    }
                }
                adapter = new CommentAdapter(listComment);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CommentActivity.this);
                recyclerViewComment.setLayoutManager(mLayoutManager);
                recyclerViewComment.setItemAnimator(new DefaultItemAnimator());
                recyclerViewComment.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();
        String key = intent.getStringExtra("postKey");

        Log.i("Post Key ", key);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Post").document(key).collection("Comment");

        recyclerViewComment = (RecyclerView)findViewById(R.id.recycleViewComment);
        messageEditText = (EditText)findViewById(R.id.messageEditText);
        sendButton = (Button)findViewById(R.id.sendButton);
        progressBarComment = (ProgressBar)findViewById(R.id.progressBarComment);

        listComment = new ArrayList<>();

        progressBarComment.setVisibility(View.VISIBLE);
        recyclerViewComment.setVisibility(View.GONE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() > 0)
                {
                    sendButton.setEnabled(true);
                }else{
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = SaveSharedPreference.getID(CommentActivity.this);
                String content = messageEditText.getText().toString();
                Calendar calendar = Calendar.getInstance();
                java.sql.Timestamp currentTimeStamp = new java.sql.Timestamp(calendar.getTime().getTime());
                Comment comment = new Comment(id, currentTimeStamp, content);
                mCollectionReference.add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        messageEditText.setText("");
                    }
                });
            }
        });

        mCollectionReference.orderBy("postTime",Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listComment.clear();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Comment comment = documentSnapshot.toObject(Comment.class);
                    String commentContent = comment.getCommentContent();
                    String customerKey = comment.getCustomerKey();
                    Date time = comment.getPostTime();
                    Comment comment1 = new Comment(customerKey, time, commentContent);
                    listComment.add(comment1);
                }
                adapter = new CommentAdapter(listComment);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CommentActivity.this);
                recyclerViewComment.setLayoutManager(mLayoutManager);
                recyclerViewComment.setItemAnimator(new DefaultItemAnimator());
                recyclerViewComment.setAdapter(adapter);
                progressBarComment.setVisibility(View.GONE);
                recyclerViewComment.setVisibility(View.VISIBLE);
            }
        });



    }

    public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{
        private List<Comment> commentList;
        private FirebaseFirestore nFirebaseFirestore;
        private DocumentReference nDocumentReference;
        private CollectionReference nCollectionReference;

        private int pos;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View commentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view, parent, false);
            return new MyViewHolder(commentView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            nFirebaseFirestore = FirebaseFirestore.getInstance();
            String key = SaveSharedPreference.getID(CommentActivity.this);
            Comment comment = commentList.get(position);
            holder.textViewComment.setText(comment.getCommentContent());
            holder.imageViewMenuButton.setVisibility(View.GONE);
            if(key.equals(comment.getCustomerKey()))
            {
                holder.imageViewMenuButton.setVisibility(View.VISIBLE);
            }
            String id = comment.getCustomerKey();
            nDocumentReference = nFirebaseFirestore.document("Customer/"+id);
            nDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Customer customer = documentSnapshot.toObject(Customer.class);
                    holder.textViewCustomerName.setText(customer.getName());
                    if(!(customer.getProfilePic().equals(""))) {
                        Glide.with(CommentActivity.this).load(customer.getProfilePic()).into(holder.imageViewCustomerPic);
                    }
                }
            });
            holder.imageViewMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = holder.getAdapterPosition();
                    PopupMenu popup = new PopupMenu(CommentActivity.this, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_post, popup.getMenu());
                    popup.setOnMenuItemClickListener(new MyMenuClickListener());
                    popup.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView textViewCustomerName, textViewComment;
            private ImageView imageViewCustomerPic, imageViewMenuButton;

            public MyViewHolder(View view)
            {
                super(view);
                textViewCustomerName = (TextView)view.findViewById(R.id.textViewCustomerName);
                textViewComment = (TextView)view.findViewById(R.id.textViewComment);
                imageViewCustomerPic = (ImageView)view.findViewById(R.id.imageViewCustomerPic);
                imageViewMenuButton = (ImageView)view.findViewById(R.id.imageViewMenuButton);
            }
        }

        public CommentAdapter(List<Comment> cList)
        {
            commentList = cList;
        }

        private class MyMenuClickListener implements PopupMenu.OnMenuItemClickListener{
            private FirebaseFirestore qFirebaseFirestore;
            private CollectionReference qCollectionReference;
            private DocumentReference qDocumentReference;
            Dialog myDialog;
            private EditText editTextComment;
            private Button btnOk, btnCancel;
            public MyMenuClickListener()
            {

            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = getIntent();
                final String key = intent.getStringExtra("postKey");
                qFirebaseFirestore = FirebaseFirestore.getInstance();
                switch(item.getItemId())
                {
                    case R.id.edit:
                        Comment comment = commentList.get(pos);
                        final Date time2 = comment.getPostTime();
                        myDialog = new Dialog(CommentActivity.this);
                        myDialog.setContentView(R.layout.customize_edit_dialog);
                        myDialog.setTitle("Edit Comment");
                        editTextComment = (EditText)myDialog.findViewById(R.id.editTextComment);
                        btnOk = (Button)myDialog.findViewById(R.id.btnOk);
                        btnCancel = (Button)myDialog.findViewById(R.id.btnCancel);
                        editTextComment.setText(comment.getCommentContent());
                        qCollectionReference = qFirebaseFirestore.collection("Post").document(key).collection("Comment");
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                qCollectionReference.whereEqualTo("postTime",time2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        String id = "";
                                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                        {
                                            id = documentSnapshot.getId();
                                        }
                                        qDocumentReference = qFirebaseFirestore.document("Post/"+key+"/Comment/"+id);
                                        qDocumentReference.update("commentContent", editTextComment.getText().toString());
                                        Toast.makeText(CommentActivity.this,"Edit Success",Toast.LENGTH_LONG).show();
                                    }
                                });
                                myDialog.cancel();
                            }
                        });
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.cancel();
                            }
                        });
                        myDialog.show();
                        Window window = myDialog.getWindow();
                        window.setLayout(800, 800);
                        return true;
                    case R.id.delete:
                        Comment comment1 = commentList.get(pos);
                        Date time = comment1.getPostTime();
                        qCollectionReference = qFirebaseFirestore.collection("Post").document(key).collection("Comment");
                        qCollectionReference.whereEqualTo("postTime",time).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                String id = "";
                                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                {
                                    id = documentSnapshot.getId();
                                }
                                qDocumentReference = qFirebaseFirestore.document("Post/"+key+"/Comment/"+id);
                                qDocumentReference.delete();
                                Toast.makeText(CommentActivity.this,"Delete Success",Toast.LENGTH_LONG).show();
                            }
                        });
                        commentList.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, commentList.size());
                        return true;
                    default:
                }
                return false;
            }
        }
    }
}
