package com.example.user.grabngo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Customer;
import com.example.user.grabngo.Class.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {} interface
 * to handle interaction events.
 * Use the {@link CustomerForumFragment#} factory method to
 * create an instance of this fragment.
 */
public class CustomerForumFragment extends Fragment {
    private RecyclerView recyclerViewPost;
    private FloatingActionButton btnAddPost;
    private ProgressBar progressBarForum;

    private FirebaseFirestore nFirebaseFirestore;
    private CollectionReference mCollectionReference;

    private List<Post> pList;
    private PostAdapter adapter;

    public CustomerForumFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Forum");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum2, container, false);
        setHasOptionsMenu(true);
        nFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = nFirebaseFirestore.collection("Post");

        recyclerViewPost = (RecyclerView)view.findViewById(R.id.recycleViewPost);
        btnAddPost = (FloatingActionButton)view.findViewById(R.id.btnAddPost);
        progressBarForum = (ProgressBar)view.findViewById(R.id.progressBarForum);
        pList = new ArrayList<>();

        progressBarForum.setVisibility(View.VISIBLE);
        recyclerViewPost.setVisibility(View.GONE);

        mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Post post = documentSnapshot.toObject(Post.class);
                    String customerKey = post.getCustomerKey();
                    String content = post.getContent();
                    String image = post.getPostImage();
                    Date time = post.getPostDate();
                    Post post1 = new Post(customerKey, image, content, time);
                    pList.add(post1);
                }
                adapter = new PostAdapter(pList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerViewPost.setLayoutManager(mLayoutManager);
                recyclerViewPost.setItemAnimator(new DefaultItemAnimator());
                recyclerViewPost.setAdapter(adapter);
                progressBarForum.setVisibility(View.GONE);
                recyclerViewPost.setVisibility(View.VISIBLE);
            }
        });

        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PostActivity.class);
                startActivity(intent);
            }
        });
        return view;
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
        return;
    }

    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>{
        private List<Post> postList;
        private FirebaseFirestore mFirebaseFirestore;
        private DocumentReference mDocumentReference;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view, parent, false);
            return new MyViewHolder(postView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            mFirebaseFirestore = FirebaseFirestore.getInstance();
            final Post post = postList.get(position);
            if(!(post.getPostImage()==null))
            {
                holder.imgView_postPic.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(post.getPostImage()).into(holder.imgView_postPic);
            }
            holder.textViewPostContent.setText(post.getContent());
            SimpleDateFormat sdformat = new SimpleDateFormat("HH:mm:ss");
            String time = sdformat.format(post.getPostDate());
            holder.textViewTime.setText(time);
            holder.imageViewMenu.setVisibility(View.GONE);
            String id = SaveSharedPreference.getID(getContext());
            if(id.equals(post.getCustomerKey()))
            {
                holder.imageViewMenu.setVisibility(View.VISIBLE);
            }
            mDocumentReference = mFirebaseFirestore.document("Customer/"+post.getCustomerKey());
            mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Customer customer = documentSnapshot.toObject(Customer.class);
                    String name = customer.getName();
                    String image = customer.getProfilePic();
                    holder.textViewCustomerName.setText(name);
                    if(!(customer.getProfilePic().equals("")))
                    {
                        Glide.with(getActivity()).load(customer.getProfilePic()).into(holder.imageViewPicture);
                    }
                }
            });
            holder.imageViewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_post, popup.getMenu());
                    popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
                    popup.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return postList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView textViewCustomerName, textViewTime, textViewPostContent;
            private ImageView imageViewPicture, imgView_postPic, imageViewMenu;

            public MyViewHolder(View view)
            {
                super(view);
                textViewCustomerName = (TextView)view.findViewById(R.id.textViewCustomerName);
                textViewTime = (TextView)view.findViewById(R.id.textViewTime);
                textViewPostContent = (TextView)view.findViewById(R.id.textViewPostContent);
                imageViewPicture = (ImageView)view.findViewById(R.id.imageViewPicture);
                imgView_postPic = (ImageView)view.findViewById(R.id.imgView_postPic);
                imageViewMenu = (ImageView)view.findViewById(R.id.imageViewMenu);
            }
        }

        public PostAdapter(List<Post> post)
        {
            postList = post;
        }

        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener{
            private FirebaseFirestore pFirebaseFirestore;
            private CollectionReference nCollectionReference;
            public MyMenuItemClickListener()
            {

            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.edit:
                        Toast.makeText(getContext(),"Edit", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.delete:
                        Toast.makeText(getContext(),"Delete",Toast.LENGTH_LONG).show();
                        return true;
                    default:
                }
                return false;
            }
        }
    }
}
