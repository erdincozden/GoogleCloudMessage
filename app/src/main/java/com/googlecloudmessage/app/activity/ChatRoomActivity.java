package com.googlecloudmessage.app.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.googlecloudmessage.app.R;
import com.googlecloudmessage.app.adapter.ChatRoomThreadAdapter;
import com.googlecloudmessage.app.model.Message;

import java.util.ArrayList;

/**
 * Created by erdinc on 2/22/16.
 */
public class ChatRoomActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private String chatRoomID;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter chatRoomThreadAdapter;
    private ArrayList<Message> messagesList;
    private BroadcastReceiver broadcastReceiver;
    private EditText edtMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtMessage = (EditText) findViewById(R.id.edtMessage);
        btnSend = (Button) findViewById(R.id.btnSend);

        Intent intent = getIntent();
        chatRoomID = intent.getStringExtra("chat_room_id");
        String title = intent.getStringExtra("name");

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (chatRoomID == null) {
            //Toast.makeText(getApplicationContext(),"Chat room")
        }

    }

}
















