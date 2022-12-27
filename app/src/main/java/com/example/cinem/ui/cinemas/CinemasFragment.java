package com.example.cinem.ui.cinemas;

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
import com.example.cinem.databinding.FragmentCinemasBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CinemasFragment extends Fragment {

    private FragmentCinemasBinding binding;
    LinearLayout layout;
    String user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Menu activity = (Menu) getActivity();
        user = activity.getUser();

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_cinemas, container, false);
        layout = root.findViewById(R.id.container);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cinemas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addCard((String) document.get("name"), Integer.parseInt(document.getId()));
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


        return root;
    }

    private void addCard(String name, int id) {
        final View view = getLayoutInflater().inflate(R.layout.list_cinema, null);

        TextView name_cinema = view.findViewById(R.id.ticket_list);

        name_cinema.setText(name);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectMovie.class)
                        .putExtra("cinema_id",id)
                        .putExtra("email",user);

                startActivity(intent);
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