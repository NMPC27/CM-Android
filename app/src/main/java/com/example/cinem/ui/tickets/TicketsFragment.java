package com.example.cinem.ui.tickets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cinem.Menu;
import com.example.cinem.R;
import com.example.cinem.databinding.FragmentTicketsBinding;
import com.example.cinem.ui.cinemas.SelectMovie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

public class TicketsFragment extends Fragment {

    private FragmentTicketsBinding binding;
    LinearLayout layout;
    String user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Menu activity = (Menu) getActivity();
        user = activity.getUser();

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_tickets, container, false);
        layout = root.findViewById(R.id.container);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tickets/tunerding2@gmail.com/tickets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addCard(
                                        (String) document.get("name"),
                                        (String) document.get("cinema"),
                                        (String) document.get("schedule"),
                                        Integer.parseInt(String.valueOf(document.get("numTickets"))),
                                        (String) document.get("qrCode"),
                                        (String) document.get("filme")
                                );
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


        return root;
    }



    private void addCard(String filme, String cinema, String sessao, int numBilhetes, String qrCode, String filme_id) {
        final View view = getLayoutInflater().inflate(R.layout.list_tickets, null);

        TextView name_movie = view.findViewById(R.id.ticket_list);
        TextView description = view.findViewById(R.id.description);

        name_movie.setText(filme);
        description.setText("Cinema: "+cinema+" - "+sessao);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag","fazr dialog");
            }
        });

        layout.addView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}