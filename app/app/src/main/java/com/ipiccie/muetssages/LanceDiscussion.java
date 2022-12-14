package com.ipiccie.muetssages;

import static android.content.ContentValues.TAG;
import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;
import com.ipiccie.muetssages.client.Utilisateur;

import java.util.Locale;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LanceDiscussion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanceDiscussion extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int drapeau = 0;

    public LanceDiscussion() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LanceDiscussion.
     */
    // TODO: Rename and change types and number of parameters
    public static LanceDiscussion newInstance(String param1, String param2) {
        LanceDiscussion fragment = new LanceDiscussion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ImageView image = view.findViewById(R.id.qr_code);
        TextToSpeech textToSpeech = new TextToSpeech(this.getContext(), status -> {});
        textToSpeech.setLanguage(Locale.FRANCE);
        textToSpeech.setSpeechRate(1.3F);

        view.findViewById(R.id.vers_activite_discussion).setOnClickListener(v->{
            Intent intention = new Intent(this.getContext(),ActiviteDiscussion.class);
            intention.putExtra("code_discussion","plouf");
            startActivity(intention);
        } );
        TextView msg = view.findViewById(R.id.message_haut_Qr);
        msg.setText(getArguments().getString("msg_ecrit"));
        view.findViewById(R.id.lire_texte).setOnClickListener(v->{
            textToSpeech.speak(getArguments().getString("msg_lu"),TextToSpeech.QUEUE_FLUSH,null);
            Toast.makeText(this.getContext(),"Lecture en cours",Toast.LENGTH_SHORT).show();
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            QRGEncoder qrgEncoder = new QRGEncoder(firebaseUser.getUid(), null, QRGContents.Type.TEXT,Math.min(height,width));
            image.setImageBitmap(qrgEncoder.getBitmap());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://vidar-9e8ac-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Users").child(firebaseUser.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (drapeau == 0){// 1??re lecture
                        drapeau = 1;
                    }else{
                        Utilisateur uti = snapshot.getValue(Utilisateur.class);
                        if (uti != null) {
                            Intent intention = new Intent(getContext(),ActiviteDiscussion.class);
                            intention.putExtra("id", uti.getContacts().get(uti.getContacts().size()-1));
                            intention.putExtra("message", getArguments() != null ? getArguments().getString("msg_debut", "Bonjour") : null);
                            startActivity(intention);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Toast.makeText(this.getContext(),"Erreur critique, veuillez red??marer l'application ou contacter le service d'assistance",Toast.LENGTH_LONG).show();
        }

        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lance_discussion, container, false);
    }
}