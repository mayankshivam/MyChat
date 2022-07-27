package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReceiverImage,ReceiverUID,ReceiverName,SenderUID;
    CircleImageView profileImage;
    TextView receiver_Name;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String sImage;
    public static String rImage;

    CardView sendBtn;
    EditText edtMessage;

    String senderRoom,receiverRoom;

    RecyclerView messageAdapter;

    ArrayList<Messages> messagesArrayList;

    MessageAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        ReceiverName=getIntent().getStringExtra("name");
        ReceiverImage=getIntent().getStringExtra("ReceiverImage");
        ReceiverUID=getIntent().getStringExtra("uid");

        messagesArrayList=new ArrayList<>();

        SenderUID=firebaseAuth.getUid();

        senderRoom=SenderUID+ReceiverUID;
        receiverRoom=ReceiverUID+SenderUID;



        profileImage=findViewById(R.id.profile_image);
        receiver_Name=findViewById(R.id.receiver_name);

        messageAdapter=findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter=new MessageAdapter(ChatActivity.this,messagesArrayList);
        messageAdapter.setAdapter(adapter);

        messageAdapter=findViewById(R.id.messageAdapter);

        sendBtn=findViewById(R.id.sendBtn);
        edtMessage=findViewById(R.id.edtMessage);

        Picasso.get().load(ReceiverImage).into(profileImage);
        receiver_Name.setText(""+ReceiverName);

        DatabaseReference reference=database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatreference=database.getReference().child("chats").child(senderRoom).child("messages");

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Messages messages=dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage=snapshot.child("imageUri").getValue().toString();
                rImage=ReceiverImage;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=edtMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Enter your message!", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    edtMessage.setText("");
                    Date date=new Date();

                    Messages messages=new Messages(message,SenderUID,date.getTime());

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                    });


                }

            }
        });

    }
}