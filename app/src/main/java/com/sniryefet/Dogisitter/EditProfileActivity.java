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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    //DEBUG TOOL
    private static final String TAG =  "dEditProfileActivity";

    //IMAGE LOADING VARI' AND DATABASE REFS
    private boolean mProfileImageIndicator=true;
    private ImageView mProfileImage;
    private static final int PICK_IMAGE=1;
    private Uri mImageUri;
    private StorageReference mRefProfileImages;
    private StorageReference mRefDogsImages;
    private DatabaseReference mDatabaseRefProfile;
    private DatabaseReference mDatabaseRefDogs;
    private DatabaseReference mDatabaseRefUserDetails;
    private String mUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();

    // RECYCLER VIEW LIST,ADAPTER AND LINKAGE TO THE XML
    private ArrayList<UploadImage> mUploads;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;

    //TEXT VIEWS AND BUTTONS
    private Button mAddDog;
    private TextView mName;
    private TextView mBirthDate;
    private TextView mEmail;
    private TextView mPhone;
    private TextView mAddress;
    private TextView mInstagram;

    private EditText mNameEditable;
    private EditText mEmailEditable;
    private EditText mPhoneEditable;
    private EditText mAddressEditable;
    private EditText mInstagramEditable;


    //View flipper
    private ViewFlipper mViewFlipper;


    private String userName;
    private String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //LINK DATABASE REFS
        mRefProfileImages = FirebaseStorage.getInstance().getReference("ProfileImages");
        mRefDogsImages = FirebaseStorage.getInstance().getReference("DogsImages");

        mDatabaseRefProfile = FirebaseDatabase.getInstance().getReference("ProfileImages");
        mDatabaseRefDogs = FirebaseDatabase.getInstance().getReference("DogsImages");

        mDatabaseRefUserDetails= FirebaseDatabase.getInstance().getReference("UserProfileDetails").child(mUserID);

        // TEXT && EDIT  VIEW LINKAGE
        mRecyclerView = findViewById(R.id.recyclerView);

        mName= findViewById(R.id.profileName);
        mBirthDate=findViewById(R.id.profileBirthDate);

        mEmail=findViewById(R.id.profileEmail);
        mEmailEditable= findViewById(R.id.profileEmailEdit);

        mPhone=findViewById(R.id.profilePhone);
        mPhoneEditable=findViewById(R.id.profilePhoneEdit);

        mAddress=findViewById(R.id.profileAddress);
        mAddressEditable=findViewById(R.id.profileAddressEdit);

        mInstagram=findViewById(R.id.profileInstagram);
        mInstagramEditable=findViewById(R.id.profileInstagramEdit);


        //CLICKABLE IMAGE VIEWS AND BUTTONS LINKAGE LINKAGE
        //mAddDog=findViewById(R.id.newDog);
        mProfileImage = findViewById(R.id.userProfileImage);
        mViewFlipper = findViewById(R.id.view_flipper);
        //mSaveChanges = findViewById(R.id.SAVE_CHANGES_BUTTON); // PROBABLY NOT NEEDED SINCE ACTIVATED THROUGH XML

        //mRecyclerView.setHasFixedSize(true); suppose to increase performance

        //LOADING THE PAGE WITH THE DATA FROM FIREBASE
        loadRecyclerView();
        loadProfileImage();
        loadUserDetails();

    }

    //This function is called when we picked an image
    @Override
    protected void onActivityResult(int requestCode,  int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);


        if (requestCode== PICK_IMAGE && resultCode == RESULT_OK
                && data !=null && data.getData()!=null){

            mImageUri=data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mImageUri);
                if(mProfileImageIndicator){
                    Log.d(TAG,"Enter loading stage");
                    //change that
                    mProfileImage.setImageBitmap(bitmap);
                    Log.d(TAG,"passed imageBitmap");
//                  Load new Profile image to the storage and database
                    uploadFile(mRefProfileImages,mDatabaseRefProfile);

                }else{
//                    Load new dog to the Storage and database
                    uploadFile(mRefDogsImages,mDatabaseRefDogs);
                }

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

    //onClick
    public void editButton(View v){
        mEmailEditable.setText(mEmail.getText().toString());
        mPhoneEditable.setText(mPhone.getText().toString());
        mAddressEditable.setText(mAddress.getText().toString());
        mInstagramEditable.setText(mInstagram.getText().toString());

        mViewFlipper.setDisplayedChild(1);
    }

    //onClick
    public void saveChanges(View v){
        saveChangesToDatabase();
        mViewFlipper.setDisplayedChild(0);

    }

//    UPDATE DATABASE OF THE USER CHANGES
    private void saveChangesToDatabase(){
        String email=mEmailEditable.getText().toString();
        String phoneNumber=mPhoneEditable.getText().toString();
        String address=mAddressEditable.getText().toString();
        String instagram=mInstagramEditable.getText().toString();
        UserProfileDetails userProfileDetails = new UserProfileDetails(mName.getText().toString(),mBirthDate.getText().toString()
                ,email,phoneNumber,address,instagram);

        //CAUSING ERROR RETURNING ME BACK TO THE ADMIN MAIN
        Log.d(TAG,"BEFORE setValue ");
        mDatabaseRefUserDetails.setValue(userProfileDetails);
        Log.d(TAG,"AFTER setValue");
        //UPDATE TextViews before flipping back
        mEmail.setText(email);
        mPhone.setText(phoneNumber);
        mAddress.setText(address);
        mInstagram.setText(instagram);
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

            final StorageReference fileReference= mStorageRef
                    .child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));

            Log.d(TAG,System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            Log.d(TAG,"PASSED file reference init");

            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG,"Entered OnSuccess StorageReference");

                    mProgressDialog.dismiss ();
                    Toast.makeText(EditProfileActivity.this, "File Uploaded ", Toast. LENGTH_LONG ).show();

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UploadImage uploadImage = new UploadImage(uri.toString());

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
                        }
                    });


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

    private void loadRecyclerView(){
        mUploads = new ArrayList<>();
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layoutManager);

        //retrieve all the owner dogs
        mDatabaseRefDogs.child(mUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,mDatabaseRefDogs+"/"+mUserID);
                for (DataSnapshot dogSnapshot : dataSnapshot.getChildren()){
                    UploadImage upload = dogSnapshot.getValue(UploadImage.class);
                    mUploads.add(upload);
                }
                Log.d(TAG,"manage to upload "+mUploads.size()+" images");
                mAdapter = new RecyclerViewAdapter(mUploads,EditProfileActivity.this);
                mRecyclerView.setAdapter(mAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loadProfileImage(){

        mDatabaseRefProfile.child(mUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String pathToPicture="";
              UploadImage uploadImage = dataSnapshot.getValue(UploadImage.class);

              if(uploadImage!=null) {
                  pathToPicture = uploadImage.getmImageUrl();


                  //check if the user has already an profile image
                  // if so, Load it
                  if (!(pathToPicture.equals(""))) {
                      Log.d(TAG, "snir image view: " + pathToPicture + "");
                      Picasso.get()
                              .load(pathToPicture)
                              .transform(new CircleTransform())
                              .fit()
                              .into(mProfileImage);
                      //mProfileImage.setImageBitmap(BitmapFactory.decodeFile(pathToPicture));
                  }
                  //uses default image
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserDetails(){

//        Check if it is possible when there is no node exists in that name
           DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(mUserID);
           ref.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   userName = dataSnapshot.getValue(UserProfileDetails.class).getName();
                   userEmail = dataSnapshot.getValue(UserProfileDetails.class).getEmail();

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });


           DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("UserProfileDetails");

           userDetails = userDetails.child(mUserID);

           userDetails.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   String name =userName;
                   String birthDate = "";
                   String email = userEmail;
                   String phoneNumber = "";
                   String address = "";
                   String instagram = "";
                   UserProfileDetails userProfileDetails= dataSnapshot.getValue(UserProfileDetails.class);

                   if(userProfileDetails!=null) {
                       //name = userProfileDetails.getName();
                       birthDate = userProfileDetails.getBirthDate();
                       email = userProfileDetails.getEmail();
                       phoneNumber = userProfileDetails.getPhoneNumber();
                       address = userProfileDetails.getAddress();
                       instagram = userProfileDetails.getInstagram();
                   }

                   mName.setText(name);
                   mBirthDate.setText(birthDate);
                   mEmail.setText(email);
                   mPhone.setText(phoneNumber);
                   mAddress.setText(address);
                   mInstagram.setText(instagram);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent first_intent = new Intent(this, AdminMainActivity.class);
        startActivity(first_intent);
    }

}