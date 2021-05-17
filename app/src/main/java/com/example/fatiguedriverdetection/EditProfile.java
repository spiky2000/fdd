package com.example.fatiguedriverdetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText profileFullName,profileaddress,profilePhone,profiledl;
    ImageView profileImageView;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference userinfo;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        drawerLayout=findViewById(R.id.drawer_layout);

        Intent data = getIntent();
        final String fullName = data.getStringExtra("FullName");
        String Address = data.getStringExtra("Address");
        String phone = data.getStringExtra("Phone");
        String dlno = data.getStringExtra("DL Number");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        profileFullName = findViewById(R.id.profileFullName);
        profileaddress = findViewById(R.id.address);
        profilePhone = findViewById(R.id.profilePhoneNo);
        profileImageView = findViewById(R.id.profileImageView);
        profiledl=findViewById(R.id.dlno);
        saveBtn = findViewById(R.id.saveProfileInfo);

        userinfo= FirebaseDatabase.getInstance().getReference().child("UserInfo");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertintodb();
            }
        });
    }

    private void insertintodb() {

        String name=profileFullName.getText().toString();
        String address=profileaddress.getText().toString();
        String phone=profilePhone.getText().toString();
        String dl=profiledl.getText().toString();

        userfunc u=new userfunc(name,address,phone,dl);

        userinfo.push().setValue(u);
        Toast.makeText(EditProfile.this, "User Data Updated", Toast.LENGTH_SHORT).show();
    }

    private void ClickMenu(View view)
    {
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view)
    {
        MainActivity.closeDrawer(drawerLayout);
    }
    public void ClickHome(View view)
    {
        MainActivity.prof(this,MainActivity.class);
    }
    public void Profile(View view)
    {
        recreate();
    }
}
