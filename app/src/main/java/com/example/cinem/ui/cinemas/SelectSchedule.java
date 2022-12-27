package com.example.cinem.ui.cinemas;

import static java.lang.String.valueOf;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cinem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class SelectSchedule extends AppCompatActivity {

    String[] horas = {"13h10", "15h50", "18h40", "21h30", "23h20"};

    String filme_id;
    String email;
    int cinema_id;
    LinearLayout layout;
    TextView cinema_name;
    TextView num_tickets;
    Button mais;
    Button menos;
    ImageView img;
    AlertDialog dialog;

    int nticket;
    String movie_name;
    String cinemaName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_schedule);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            filme_id = extras.getString("filme_id");
            cinema_id = extras.getInt("cinema_id");
            email = extras.getString("email");
        }

        layout = findViewById(R.id.container);
        cinema_name = findViewById(R.id.cinemaName);
        num_tickets = findViewById(R.id.numTickets);
        mais = findViewById(R.id.mais);
        menos = findViewById(R.id.menos);
        img = findViewById(R.id.img);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cinemas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (Integer.parseInt(document.getId()) == cinema_id) {
                                    cinema_name.setText((String) document.get("name"));
                                    cinemaName=(String) document.get("name");
                                }

                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("filmes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if ( document.getId().equals(filme_id) ) {
                                    Picasso.get().load((String) document.get("img")).resize(450, 550).into(img);
                                    movie_name=(String) document.get("name");
                                }
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });



        for (int i=0; i<horas.length;i++){
            addCard(horas[i]);
        }

        mais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nticket = Integer.parseInt(num_tickets.getText().toString());
                nticket++;
                num_tickets.setText(valueOf(nticket));
            }
        });

        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nticket = Integer.parseInt(num_tickets.getText().toString());

                if (nticket>0 ){
                    nticket--;
                    num_tickets.setText(valueOf(nticket));
                }

            }
        });


    }

    private void addCard(String horas) {
        final View view = getLayoutInflater().inflate(R.layout.list_horas, null);

        TextView hora = view.findViewById(R.id.ticket_list);

        hora.setText(horas);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog(horas);
                dialog.show();
            }
        });

        layout.addView(view);
    }

    private void buildDialog(String horario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        TextView cinema = view.findViewById(R.id.cinema);
        TextView filme = view.findViewById(R.id.filme);
        TextView sessao = view.findViewById(R.id.sessao);
        TextView bilhetes = view.findViewById(R.id.bilhetes);
        TextView preco = view.findViewById(R.id.preco);

        cinema.setText("Cinema: "+cinemaName);
        filme.setText("Filme: "+movie_name);
        sessao.setText("Sessão: "+horario);
        bilhetes.setText("Bilhetes: "+valueOf(nticket));
        double tmp = nticket*7.10;
        preco.setText("Preço: "+valueOf(tmp)+" €");

        builder.setView(view);
        builder.setTitle("Confirmar")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(SelectSchedule.this, Fingerprint.class)
                                .putExtra("cinemaName",cinemaName)
                                .putExtra("filmeName",movie_name)
                                .putExtra("filmeId",filme_id)
                                .putExtra("hora",horario)
                                .putExtra("numTickets",nticket)
                                .putExtra("email",email)
                                .putExtra("preco",nticket*7.10);

                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dialog = builder.create();
    }
}