package com.example.fatiguedriverdetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fatiguedriverdetection.Helper.GraphicOverlay;
import com.example.fatiguedriverdetection.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class detectface extends AppCompatActivity {

    Button faceDetectButton;
    GraphicOverlay graphicOverlay;
    CameraView cameraView;
    android.app.AlertDialog alertDialog;
    DrawerLayout drawerLayout;

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detectface);

        drawerLayout=findViewById(R.id.drawer_layout);

        faceDetectButton=findViewById(R.id.detect_face_btn);
        graphicOverlay=findViewById(R.id.graphic_overlay);
        cameraView=findViewById(R.id.camera_view);
        cameraView.toggleFacing();
        alertDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait")
                .setCancelable(false)
                .build();




        faceDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();


//                Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                startActivityForResult(i,1111);


            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                alertDialog.show();
                Bitmap bitmap=cameraKitImage.getBitmap();
                bitmap=Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();

                processFacedetection(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {


            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            VideoView videoView = new VideoView(this);
            videoView.setVideoURI(data.getData());
            videoView.start();
            builder.setView(videoView).show();
        }

    }

    private void processFacedetection(Bitmap bitmap) {

        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions=new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector= FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                getFaceResults(firebaseVisionFaces);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(detectface.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFaceResults(List<FirebaseVisionFace> firebaseVisionFaces) {
        int counter=0;
        for(FirebaseVisionFace face : firebaseVisionFaces)
        {
            Rect rect=face.getBoundingBox();
            float rotY=face.getHeadEulerAngleY();
            float rotZ=face.getHeadEulerAngleZ();

            //EYE
            List<FirebaseVisionPoint> lefteyecontour=face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
            System.out.println("Left eye Contour"+lefteyecontour);
            List<FirebaseVisionPoint> righteyecontour=face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).getPoints();
            System.out.println("Right eye Contour"+righteyecontour);

            if(face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY)
            {
                float rightEyeOpenProbability=face.getRightEyeOpenProbability();
                System.out.println("Right eye"+rightEyeOpenProbability);
            }
            if(face.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY)
            {
                float leftEyeOpenProbability=face.getLeftEyeOpenProbability();
                System.out.println("Left eye"+leftEyeOpenProbability);
            }

            float earl,earr;
            float r1= (float) Math.sqrt(Math.pow((righteyecontour.get(15).getY() - righteyecontour.get(1).getY()),2) + Math.pow((righteyecontour.get(15).getX() - righteyecontour.get(1).getX()),2));
            float r2= (float) Math.sqrt(Math.pow((righteyecontour.get(14).getY() - righteyecontour.get(2).getY()),2) + Math.pow((righteyecontour.get(14).getX() - righteyecontour.get(2).getX()),2));
            float r3= (float) Math.sqrt(Math.pow((righteyecontour.get(13).getY() - righteyecontour.get(3).getY()),2) + Math.pow((righteyecontour.get(13).getX() - righteyecontour.get(3).getX()),2));
            float r4= (float) Math.sqrt(Math.pow((righteyecontour.get(12).getY() - righteyecontour.get(4).getY()),2) + Math.pow((righteyecontour.get(12).getX() - righteyecontour.get(4).getX()),2));
            float r5= (float) Math.sqrt(Math.pow((righteyecontour.get(11).getY() - righteyecontour.get(5).getY()),2) + Math.pow((righteyecontour.get(11).getX() - righteyecontour.get(5).getX()),2));
            float r6= (float) Math.sqrt(Math.pow((righteyecontour.get(10).getY() - righteyecontour.get(6).getY()),2) + Math.pow((righteyecontour.get(10).getX() - righteyecontour.get(6).getX()),2));
            float r7= (float) Math.sqrt(Math.pow((righteyecontour.get(9).getY() - righteyecontour.get(7).getY()),2) + Math.pow((righteyecontour.get(9).getX() - righteyecontour.get(7).getX()),2));
            float r8= (float) Math.sqrt(Math.pow((righteyecontour.get(0).getY() - righteyecontour.get(8).getY()),2) + Math.pow((righteyecontour.get(0).getX() - righteyecontour.get(8).getX()),2));

            earr=(r1+r2+r3+r4+r5+r6+r7)/(2*r8);
            System.out.println("Right Eye Aspect Ratio = "+earr);

            float l1= (float) Math.sqrt(Math.pow((lefteyecontour.get(15).getY() - lefteyecontour.get(1).getY()),2) + Math.pow((lefteyecontour.get(15).getX() - lefteyecontour.get(1).getX()),2));
            float l2= (float) Math.sqrt(Math.pow((lefteyecontour.get(14).getY() - lefteyecontour.get(2).getY()),2) + Math.pow((lefteyecontour.get(14).getX() - lefteyecontour.get(2).getX()),2));
            float l3= (float) Math.sqrt(Math.pow((lefteyecontour.get(13).getY() - lefteyecontour.get(3).getY()),2) + Math.pow((lefteyecontour.get(13).getX() - lefteyecontour.get(3).getX()),2));
            float l4= (float) Math.sqrt(Math.pow((lefteyecontour.get(12).getY() - lefteyecontour.get(4).getY()),2) + Math.pow((lefteyecontour.get(12).getX() - lefteyecontour.get(4).getX()),2));
            float l5= (float) Math.sqrt(Math.pow((lefteyecontour.get(11).getY() - lefteyecontour.get(5).getY()),2) + Math.pow((lefteyecontour.get(11).getX() - lefteyecontour.get(5).getX()),2));
            float l6= (float) Math.sqrt(Math.pow((lefteyecontour.get(10).getY() - lefteyecontour.get(6).getY()),2) + Math.pow((lefteyecontour.get(10).getX() - lefteyecontour.get(6).getX()),2));
            float l7= (float) Math.sqrt(Math.pow((lefteyecontour.get(9).getY() - lefteyecontour.get(7).getY()),2) + Math.pow((lefteyecontour.get(9).getX() - lefteyecontour.get(7).getX()),2));
            float l8= (float) Math.sqrt(Math.pow((lefteyecontour.get(0).getY() - lefteyecontour.get(8).getY()),2) + Math.pow((lefteyecontour.get(0).getX() - lefteyecontour.get(8).getX()),2));

            earl=(l1+l2+l3+l4+l5+l6+l7)/(2*l8);
            System.out.println("Left Eye Aspect Ratio = "+earl);

            //MOUTH

            List<FirebaseVisionPoint> upperLiptopContour=face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).getPoints();
            System.out.println("Upper Lip Top"+upperLiptopContour);
            List<FirebaseVisionPoint> upperLipBottomContour=face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();
            System.out.println("Upper Lip Bottom"+upperLipBottomContour);

            List<FirebaseVisionPoint> lowerLiptopContour=face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).getPoints();
            System.out.println("Lower Lip Top"+lowerLiptopContour);
            List<FirebaseVisionPoint> lowerLipBottomContour=face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).getPoints();
            System.out.println("Lower Lip Bottom"+lowerLipBottomContour);

            if(face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY)
            {
                float smileProb=face.getSmilingProbability();
                System.out.println("Smile Probability"+smileProb);
            }


            //EAR
            FirebaseVisionFaceLandmark leftear=face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
            if(leftear != null)
            {
                FirebaseVisionPoint leftEarPos=leftear.getPosition();
                System.out.println("Left Ear Position"+leftEarPos);
            }

            FirebaseVisionFaceLandmark rightear=face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);
            if(leftear != null)
            {
                FirebaseVisionPoint rightEarPos=rightear.getPosition();
                System.out.println("Right Ear Position"+rightEarPos);
            }

            if(face.getTrackingId() != FirebaseVisionFace.INVALID_ID)
            {
                int id=face.getTrackingId();
            }

            RectOverlay rectOverlay=new RectOverlay(graphicOverlay,rect);
            graphicOverlay.add(rectOverlay);
            counter =counter+1;
        }
        alertDialog.dismiss();
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
        MainActivity.detect(this,MainActivity.class);
    }
    public void Detectface(View view)
    {
        recreate();
    }
}