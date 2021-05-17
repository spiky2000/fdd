package com.example.fatiguedriverdetection;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private static int VIDEO_REQUEST=101;
    private Uri videoUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
    }
    public void ClickMenu(View view)
    {
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view)
    {
        closeDrawer(drawerLayout);
    }
    public static void closeDrawer(DrawerLayout drawerLayout)
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view)
    {
        recreate();
    }

    public void Detectface(View view)
    {
        detect(this,detectface.class);
    }

    public static void detect(Activity activity,Class aclass) {
        Intent intent=new Intent(activity,aclass);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
    public void Profile(View view)
    {
        prof(this,EditProfile.class);
    }
    public static void prof(Activity activity,Class aclass)
    {
        Intent intent=new Intent(activity,aclass);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
    public void record(View view)
    {
        rec(this,Recordv.class);
    }
    public static void rec(Activity activity,Class aclass)
    {
        Intent intent=new Intent(activity,aclass);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    public void captureVideo(View view)
    {

        Intent videoIntent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(videoIntent,1111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==VIDEO_REQUEST && resultCode==RESULT_OK)
        {
            videoUri = data.getData();

        }
    }
    public void playVideo(View view)
    {
        Intent playIntent=new Intent(this,VideoPlayActivity.class);
        //playIntent.putExtra("videoUri",videoUri.toString());
        startActivity(playIntent);
    }
}