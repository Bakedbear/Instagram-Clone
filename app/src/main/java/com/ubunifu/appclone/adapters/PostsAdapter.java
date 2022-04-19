package com.ubunifu.appclone.adapters;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubunifu.appclone.CommentsActivity;
import com.ubunifu.appclone.R;
import com.ubunifu.appclone.models.Posts;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    Context mContext;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String currentUserId, postId, id;

    ArrayList<Posts> mPostsArrayList;

    public PostsAdapter(Context mContext, ArrayList<Posts> mPostsArrayList) {
        this.mContext = mContext;
        this.mPostsArrayList = mPostsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.posts_item_list, parent, false);

        return new PostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        //get values from firestore db for every position

        Posts posts = mPostsArrayList.get(position);
        postId = posts.getPost_id().toString();

        holder.caption.setText(posts.getCaption());
        holder.name.setText(posts.getFull_names());
        Glide.with(mContext).load(mPostsArrayList.get(position).getPost_img_url()).into(holder.postImage);
        Glide.with(mContext).load(mPostsArrayList.get(position).getProfile_img_url()).into(holder.profImg);

        holder.saveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document(postId);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                String postImgUrl = documentSnapshot.getString("post_img_url");
                                //Toast.makeText(mContext, postImgUrl, Toast.LENGTH_SHORT).show();
                                HashMap savesMap = new HashMap();
                                savesMap.put("post_img_url", postImgUrl);
                                savesMap.put("liked by", currentUserId);
                                DocumentReference reference = FirebaseFirestore.getInstance().collection("saves")
                                        .document(currentUserId)
                                        .collection("my_saves")
                                        .document();
                                reference.set(savesMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(mContext, "Image saved", Toast.LENGTH_SHORT).show();
                                                } else{
                                                    Toast.makeText(mContext, "Error saving image", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        }

                    }
                });
            }
        });

        holder.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id = db.collection("likes").document().getId();
                HashMap likesMap = new HashMap();
                likesMap.put("likeId", id);
                likesMap.put("postId", postId);
                likesMap.put("userId", currentUserId);

                DocumentReference documentReference = db.collection("likes").document(id);
                documentReference.set(likesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Liked Successfully", Toast.LENGTH_SHORT).show();
                            holder.likeImg.setVisibility(View.GONE);
                            holder.likedImg.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(mContext, "Error Occurred!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });


        holder.likedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("likes").document(id);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                final String like_id = document.getString("likeId");

                                db.collection("likes")
                                        .document(like_id)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    Toast.makeText(mContext, "Like Deleted", Toast.LENGTH_SHORT).show();
                                                    holder.likeImg.setVisibility(View.VISIBLE);
                                                    holder.likedImg.setVisibility(View.GONE);


                                                }else{

                                                    Toast.makeText(mContext, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });


                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

            }
        });

        holder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext , v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit :
                                editPost(posts.getPost_id());
                                return true;

                            case R.id.delete :

                                deletePost();

                                return true;

                            case R.id.report :
                                Toast.makeText(mContext, "Report Sent!", Toast.LENGTH_SHORT).show();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!posts.getPublisher().equals(currentUserId)){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });


        // to comment activity

        holder.commentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postId", posts.getPost_id());
                intent.putExtra("publisherId", posts.getPublisher());
//                Toast.makeText(mContext, "PostId = " + posts.getPost_id() + "Publisher is " + posts.getPublisher(), Toast.LENGTH_LONG).show();
                mContext.startActivity(intent);

            }
        });




    }

    private void deletePost() {
        db.collection("posts")
                .document(postId)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mPostsArrayList.remove(postId);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, "post deleted", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mContext, "error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void editPost(String post_id) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");
        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(layoutParams);
        alertDialog.setView(editText);


        getText(post_id, editText);
        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("caption", editText.getText().toString());

                FirebaseFirestore.getInstance().collection("posts")
                        .document(post_id)
                        .update(hashMap);

                notifyDataSetChanged();
            }
        });

        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
        
    }

    private void getText(String post_id, final EditText editText) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document(post_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        String caption = documentSnapshot.getString("caption");
                        editText.setText(caption);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPostsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Declare your views

        TextView caption, name;
        ImageView postImage, commentImg, saveImg, likeImg, imgMore, likedImg;
        CircleImageView profImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //initialize views

            caption = itemView.findViewById(R.id.description);
            postImage = itemView.findViewById(R.id.post_image);
            commentImg = itemView.findViewById(R.id.comment);
            saveImg = itemView.findViewById(R.id.save);
            likeImg = itemView.findViewById(R.id.like);
            likedImg = itemView.findViewById(R.id.liked);
            name = itemView.findViewById(R.id.username);
            profImg = itemView.findViewById(R.id.image_profile);
            imgMore = itemView.findViewById(R.id.more);




        }
    }
}
