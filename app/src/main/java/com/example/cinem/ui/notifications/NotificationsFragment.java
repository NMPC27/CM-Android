package com.example.cinem.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cinem.Menu;
import com.example.cinem.R;
import com.example.cinem.databinding.FragmentNotificationsBinding;
import com.example.cinem.databinding.FragmentTicketsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificationsFragment extends Fragment {

    private FragmentTicketsBinding binding;
    LinearLayout layout;
    String user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Menu activity = (Menu) getActivity();
        user = activity.getUser();

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_notifications, container, false);
        layout = root.findViewById(R.id.container);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addCard((String) document.get("notification"), document.getId());
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


        return root;
    }

    private void addCard(String notifi,String id) {
        final View view = getLayoutInflater().inflate(R.layout.list_notifications, null);

        TextView name_movie = view.findViewById(R.id.list_notf);
        Button del = view.findViewById(R.id.del_not);

        name_movie.setText(notifi);

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout.removeView(view);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(user).document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error deleting document", e);
                            }
                        });
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