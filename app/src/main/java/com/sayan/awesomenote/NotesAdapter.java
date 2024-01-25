package com.sayan.awesomenote;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sayan.firebaseapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {

    Context context;
    ArrayList<fireBaseModel> allNotesList;

    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    public NotesAdapter(Context context, ArrayList<fireBaseModel> allNotesList) {
        this.allNotesList=allNotesList;
        this.context=context;
    }

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesHolder(LayoutInflater.from(context).inflate(R.layout.notes,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {
        if (allNotesList != null && position >= 0 && position < allNotesList.size()) {
            holder.noteTitle.setText(allNotesList.get(position).getTitle());
            holder.noteContent.setText(allNotesList.get(position).getContent());

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NoteDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("title",allNotesList.get(position).getTitle());
                    intent.putExtra("content",allNotesList.get(position).getContent());
                    intent.putExtra("key",allNotesList.get(position).getId());
                    context.startActivity(intent);
                }
            });
        }
        else {
            // Handle the case where the list is null or the position is out of bounds
            Log.e("onBindViewHolder", "Invalid list or position");
        }
        int colorCode=getRandomColor();

        holder.linearLayout.setBackgroundColor(holder.itemView.getResources().getColor(colorCode,null));

        holder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                popupMenu.setGravity(Gravity.END);
                popupMenu
                        .getMenu()
                        .add("Edit")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                Intent intent = new Intent(context, EditNoteActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("key",allNotesList.get(position).getId());
                                intent.putExtra("title",allNotesList.get(position).getTitle());
                                intent.putExtra("content",allNotesList.get(position).getContent());
                                context.startActivity(intent);
                                return false;
                            }
                        });
                popupMenu.show();
            }


        });
    }

    @Override
    public int getItemCount() {
        return allNotesList.size();
    }

    class NotesHolder extends RecyclerView.ViewHolder {
        TextView noteTitle,noteContent;
        LinearLayout linearLayout;
        ImageView popupButton;
        public NotesHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle=itemView.findViewById(R.id.noteTitle);
            noteContent=itemView.findViewById(R.id.noteContent);
            linearLayout=itemView.findViewById(R.id.note);
            popupButton=itemView.findViewById(R.id.menu_note);
        }
    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.light_blue);
        colorCode.add(R.color.mint_green);
        colorCode.add(R.color.peach);
        colorCode.add(R.color.lavender);
        colorCode.add(R.color.light_coral);
        colorCode.add(R.color.pale_goldenrod);
        colorCode.add(R.color.baby_pink);
        colorCode.add(R.color.light_slate_gray);
        colorCode.add(R.color.honeydew);
        colorCode.add(R.color.light_orchid);

        Random random = new Random();
        int number = random.nextInt(colorCode.size());
        return colorCode.get(number);
    }
}
