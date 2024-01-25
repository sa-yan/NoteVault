package com.sayan.awesomenote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sayan.firebaseapp.R;

import java.util.HashMap;
import java.util.Map;

public class NotesCreateActivity extends AppCompatActivity {

    EditText title_et,content_et;
    FloatingActionButton saveNote;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_create);
        // Inside your activity onCreate method
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        title_et=findViewById(R.id.titleNoteEt);
        content_et=findViewById(R.id.contentNoteEt);
        saveNote = findViewById(R.id.saveNote);
        progressBar=findViewById(R.id.pgBar);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        // Check internet connectivity
        if (!isInternetConnected()) {
            showInternetAlertDialog();
        }


        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String title = title_et.getText().toString();
                String content = content_et.getText().toString();
                String uid = firestore
                        .collection("note")
                        .document(user.getUid())
                        .collection("myNotes")
                        .document().getId();

                if(title.isEmpty() || content.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(NotesCreateActivity.this, "Please fill up both fields", Toast.LENGTH_SHORT).show();
                }else{

                    DocumentReference documentReference = firestore
                            .collection("note")
                            .document(user.getUid())
                            .collection("myNotes")
                            .document(uid);

                    Map<String, Object> note = new HashMap<>();
                    note.put("title",title);
                    note.put("content",content);
                    note.put("id",uid);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(NotesCreateActivity.this, "Notes Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(new Intent(getApplicationContext(), MainActivity.class));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(NotesCreateActivity.this, "Failed to create note", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }
        });


    }

    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Method to show alert dialog for no internet connectivity
    public void showInternetAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please connect to the internet.");

        // Add a button to dismiss the dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // You can add additional actions here if needed
                finish();
            }
        });

        // Show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}