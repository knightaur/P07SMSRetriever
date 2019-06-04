package sg.edu.rp.c346.p07_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentSecond extends Fragment {

    Button btnRetrieve, btnEmail;
    EditText et1;
    TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tv = view.findViewById(R.id.tv);
        btnRetrieve = view.findViewById(R.id.btnRetrieve1);
        btnEmail = view.findViewById(R.id.btnEmail);
        et1 = view.findViewById(R.id.et1);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a;
                String[] array;
                if(et1.getText().toString().length() > 0){
                        a = et1.getText().toString();
                        array = a.split("\\s+");
                    int permissionCheck = PermissionChecker.checkSelfPermission
                            (getActivity(), Manifest.permission.READ_SMS);

                    if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_SMS}, 0);
                        // stops the action from proceeding further as permission not
                        //  granted yet
                        return;
                    }

                    Uri uri = Uri.parse("content://sms");
                    String[] reqCols = new String[]{"date", "address", "body", "type"};
                    // Get Content Resolver object from which to
                    //  query the content provider
                    ContentResolver cr = getActivity().getContentResolver();
                    // The filter String
                    String filter="body LIKE ?";
                    // The matches for the ?
                    String[] args = new String[array.length];
                    for(int i = 0; i < array.length ; i++){
                            args[i] = "%" + array[i] + "%";
                            filter += " OR body LIKE ?";
                    }
                    // Fetch SMS Message from Built-in Content Provider
                    Cursor cursor = cr.query(uri, reqCols, filter, args, null);
                    String smsBody = "";

                    if (cursor.moveToFirst()) {
                        do {
                            long dateInMillis = cursor.getLong(0);
                            String date = (String) DateFormat
                                    .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                            String address = cursor.getString(1);
                            String body = cursor.getString(2);
                            String type = cursor.getString(3);
                            if (type.equalsIgnoreCase("1")) {
                                type = "Inbox:";
                            } else {
                                type = "Sent:";
                            }
                            smsBody += type + " " + address + "\n at " + date
                                    + "\n\"" + body + "\"\n\n";
                        } while (cursor.moveToNext());
                    }
                    tv.setText(smsBody);
                }
                else {
                    tv.setText("Enter Something");
                    return;
                }
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"francisanthonytoha@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "SMS");
                email.putExtra(Intent.EXTRA_TEXT, tv.getText().toString());
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });


        return view;
    }
}