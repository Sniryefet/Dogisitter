package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG =  "dEditProfileActivity";
    private ArrayList<String> mNames= new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private boolean mProfileImageIndicator=true;

    private ImageView mProfileImage;
    private static final int PICK_IMAGE=1;
    private Uri mImageUri;

    private Button mAddDog;

    private StorageReference mRefProfileImages;
    private StorageReference mRefDogsImages;
    private DatabaseReference mDatabaseRefProfile;
    private DatabaseReference mDatabaseRefDogs;
    private String mUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();

    // ~~~~~~~~~~~~~~ ~~~~~~~~~~~~~~~~~~~
    private List<UploadImage> mUploads;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseRef;
    private RecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mRefProfileImages = FirebaseStorage.getInstance().getReference("ProfileImages");
        mRefDogsImages = FirebaseStorage.getInstance().getReference("DogsImages");

        mDatabaseRefProfile = FirebaseDatabase.getInstance().getReference("ProfileImages");
        mDatabaseRefDogs = FirebaseDatabase.getInstance().getReference("DogsImages");

        // look for the user image and animal's images in the database
        //meanwhile loading random images from the web
        initImages();

        mProfileImage = findViewById(R.id.imageView);

        //TO DO:
        //Load the profile image for the current user
        //Load the images of the dogs for the current user
        //Load the details of the current userProfileDetails(new table), name ,age ,shit


        //check if this connections is needed
        //might not be needed since this button activates from the xml
        mAddDog=findViewById(R.id.newDog);


        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        mRecyclerView = findViewById(R.id.recyclerView);
        //mRecyclerView.setHasFixedSize(true); suppose to increase performance


    }

    //This function is called when we picked an image
    @Override
    protected void onActivityResult(int requestCode,  int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);


        if (requestCode== PICK_IMAGE && resultCode == RESULT_OK
                && data !=null && data.getData()!=null){

            mImageUri=data.getData();
            mProfileImage.setImageURI(mImageUri);
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mImageUri);
                if(mProfileImageIndicator){
                    Log.d(TAG,"Enter loading stage");
                    mProfileImage.setImageBitmap(bitmap);
                    Log.d(TAG,"passed imageBitmap");
//                  Load new Profile image to the storage and database
                    uploadFile(mRefProfileImages,mDatabaseRefProfile);

                }else{
//                    Load new dog to the Storage and database
                    uploadFile(mRefDogsImages,mDatabaseRefDogs);
                }
                //add Code here to load the picture to the database storage

            }catch (IOException e){
                e.printStackTrace();
            }

        }

    }


    //onClick Listner from the xml file set on the image view
    public void uploadProfileImage(View view){
        mProfileImageIndicator=true;
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select Picture"),PICK_IMAGE);

    }

    //onClick from the xml for adding new dog pic
    public void addNewDog(View view){
        //change the PICK_IMAGE value
        mProfileImageIndicator=false;
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select Picture"),PICK_IMAGE);

    }


    // onClick from the xml
    public void backButton(View v){
        // need to change it to the right page according to the user permission
        Intent intent = new Intent(EditProfileActivity.this,AdminMainActivity.class);
        finish();
        startActivity(intent);

    }



    //This file returns the extension of the file
    //we picked (https://www.youtube.com/watch?v=lPfQN-Sfnjw)
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    //upload a file based on the references that was given
    //either for the "Profile Images" or the "Dogs Images"
    private void uploadFile(StorageReference mStorageRef,final DatabaseReference mDatabaseRef){


        if(mImageUri!=null){
            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Uploading ...");
            mProgressDialog.show();

            Log.d(TAG,"Enter Upload file");

            StorageReference fileReference= mStorageRef
                    .child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));

            Log.d(TAG,System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            Log.d(TAG,"PASSED file reference init");

            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG,"Entered OnSuccess StorageReference");

                    mProgressDialog.dismiss ();
                    Toast.makeText(EditProfileActivity.this, "File Uploaded ", Toast. LENGTH_LONG ).show();
                    UploadImage uploadImage = new UploadImage(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                    if(mProfileImageIndicator) {
                        mDatabaseRef.child(mUserID).setValue(uploadImage);
                    }else{
                        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ CHECK IF THE KEY IS WORKING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                        String dogPicID = mDatabaseRef.child(mUserID).push().getKey();
                        mDatabaseRef.child(mUserID).child(dogPicID).setValue(uploadImage)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d(TAG,"Add picture to database succeeded");
                                        }else{
                                            Log.d(TAG,"Add picture to database failed");
                                        }
                                    } // END onComplete

                                }); //END addOnCompleteListener
                    }// END if-else of the indicator
                    mProgressDialog.dismiss ();

                }//END onSuccess

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"loading failed");
                    mProgressDialog.dismiss ();
                    Toast.makeText(EditProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG,"entered onProgress");
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    Log.d(TAG , "finished onProgress");
                }
            });

        }else{
            Toast.makeText(this,"No file was selected",Toast.LENGTH_SHORT).show();
        }
    }


    private void initImages(){
        //get the images from the right admin/client
        mImageUrls.add("https://thumbs.dreamstime.com/z/emilia-clarke-cannes-france-may-emilia-clarke-gala-screening-solo-star-wars-story-st-festival-de-cannes-%C2%A9-166810618.jpg");
        mNames.add("Emilia clarke");

        mImageUrls.add("https://thumbs.dreamstime.com/z/majestic-autumn-fall-landscape-red-deer-stag-cervus-elaphus-foreground-vibrant-forest-lake-background-stunning-epic-166661560.jpg");
        mNames.add("deer");

        mImageUrls.add("https://thumbs.dreamstime.com/z/schauzer-sam-166501063.jpg");
        mNames.add("dog");

        mImageUrls.add("https://thumbs.dreamstime.com/z/tourists-skiing-bukovel-bukovel-ivano-frankivsk-ukraine-february-ski-resort-bukovel-carpathian-mountains-ukraine-166427242.jpg");
        mNames.add("ski");

        mImageUrls.add("https://thumbs.dreamstime.com/z/gorilla-dangerous-look-dark-background-166410063.jpg");
        mNames.add("gorilla");

        mImageUrls.add("https://thumbs.dreamstime.com/z/green-snake-zoo-liberec-morelia-viridis-tree-python-best-photo-detail-166406538.jpg");
        mNames.add("snake");

        mImageUrls.add("https://thumbs.dreamstime.com/z/little-red-kitten-wearing-knitted-scarf-sits-snow-winter-little-red-kitten-wearing-knitted-scarf-sits-snow-166301940.jpg");
        mNames.add("cat");

        initRecyclerView();
    }
    private void initRecyclerView(){

        Log.d(TAG,"initRecyeclerView: ");

        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        //call init images
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames,mImageUrls,this);
        recyclerView.setAdapter(adapter);

    }
    private void init(){



    }



}