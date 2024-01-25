package com.sayan.awesomenote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.sayan.firebaseapp.R;

public class EditNoteActivity extends AppCompatActivity {

    EditText editTextTitle,editTextContent;
    FirebaseFirestore firestore;
    FirebaseUser user;

    FloatingActionButton button;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // Inside your activity onCreate method
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        editTextTitle=findViewById(R.id.titleNoteEtEdit);
        editTextContent = findViewById(R.id.contentNoteEtEdit);
        button=findViewById(R.id.updateNote);
        progressBar=findViewById(R.id.pgBarUpdate);

        firestore=FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        String id = intent.getStringExtra("key");
//        String title = intent.getStringExtra("title");
//        String content = intent.getStringExtra("content");

        // Check internet connectivity
        if (!isInternetConnected()) {
            showInternetAlertDialog();
        }

//        editTextTitle.setText(title);
//        editTextContent.setText(content);
        getNotes(id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String titleData = editTextTitle.getText().toString();
                String contentData = editTextContent.getText().toString();

                firestore.collection("note")
                        .document(user.getUid())
                        .collection("myNotes")
                        .document(id)
                        .update("title",titleData,"content",contentData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(EditNoteActivity.this, "Updated data successfully!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(EditNoteActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    void getNotes(String key){
        firestore.collection("note").document(user.getUid()).collection("myNotes")
                .document(key)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error==null){
                            fireBaseModel model = value.toObject(fireBaseModel.class);

                            editTextTitle.setText(model.getTitle());
                            editTextContent.setText(model.getContent());
                        }
                    }
                });
    }


    // Method to check internet connectivity
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
                finishAffinity(); // Close all activities in the app
                System.exit(0);
            }
        });

        // Show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}