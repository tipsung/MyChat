package com.example.tipsung.mychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = (TextView) findViewById(R.id.textview);

        //เป็นการเขียนข้อมูล
        //connect ไปที่ mRootRef
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference mUsersRef = mRootRef.child("users");
        DatabaseReference mMessagesRef = mRootRef.child("messages");

        mUsersRef.child("id-12345").setValue("Tipsung");
        mMessagesRef.child("Hello Word");

        final FriendlyMessage friendlyMessage = new FriendlyMessage("Hello World!", "Tipsung");
        mMessagesRef.push().setValue(friendlyMessage);

        // push เป็นการ generate $postid ของ object ชื่อ posts ออกมาก่อนเพื่อใช้ใน // /user-posts/$userid/$postid
        String key = mMessagesRef.push().getKey();  //สร้างคียขึ้นมาเพื่อเตียมที่จะ put

        //ไม่จำเป็นต้องเป็น class เดียวกัน เตียมที่จะ put  ค้า  username text ขึ้นไป
        HashMap<String, Object> postValues = new HashMap<>();
        postValues.put("username", "Photjjana");   // string แล้วตามด้วย object เป็น object ที่มี class เป็น string
        postValues.put("text", "Hello World!");

        // เตียมทำการ  Updates ข้อมูล ส้รางข้อมูลใหม่เเพือรับค่าที่ใส่ข้อมูล
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/messages/" + key, postValues);
        childUpdates.put("/user-messages/Photjjana/" + key, postValues); //ทำการ  Updates ข้อมูล

        mRootRef.updateChildren(childUpdates);


        //การอ่านข้อมูล ValueEventListener รอรับเหตุการณ์ใดเหตุการณ์หนึ่ง มี 2 สถานะ
        mMessagesRef.addValueEventListener(new ValueEventListener() {
            @Override  // dataSnapshot คดึง่าที่เกิดตอนนอีเวนนันั้น  onDataChange จะถูกเรียกตอนเริ่ม และถูกเรียกทุกครั้งที่ข้อมูลภายใต้ path ที่เราอ้างถึงมีการเปลี่ยนแปลง
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = "";
                for (DataSnapshot messages : dataSnapshot.getChildren()){
                    FriendlyMessage message = messages.getValue(FriendlyMessage.class);
                    text += message.getUsername() + "-" + message.getText() + "\n";
                }
                textview.setText(text);
            }
            @Override   //onCancelled จะถูกเรียกเมื่อไม่สามารถอ่านข้อมูลจาก database
            public void onCancelled(DatabaseError databaseError) {
                textview.setText("Failed: " + databaseError.getMessage());
            }
        });
    }

}
