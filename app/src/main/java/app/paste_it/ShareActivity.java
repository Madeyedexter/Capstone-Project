package app.paste_it;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import app.paste_it.models.ImageModel;
import app.paste_it.models.Paste;
import app.paste_it.service.ImageImportService;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.etShareTitle)
    EditText etTitle;
    @BindView(R.id.etShareContent)
    EditText etContent;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.buttonSave)
    Button buttonSave;
    @BindView(R.id.buttonDiscard)
    Button buttonDiscard;

    private final String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Uri imageUri;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);

        buttonSave.setOnClickListener(this);
        buttonDiscard.setOnClickListener(this);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }
        else{
            Toast.makeText(this, R.string.unsupported_data,Toast.LENGTH_SHORT).show();
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            etContent.setText(sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Picasso.with(this).load(imageUri).into(ivImage);
        }
    }

    private void savePaste() {
        if(UID == null){
            Toast.makeText(this, R.string.sign_in_message,Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(etTitle.getText().length()==0 && etContent.getText().length()==0 && imageUri==null){
            Toast.makeText(this, R.string.nothing_pasted,Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Paste paste = new Paste();
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        paste.setCreated(System.currentTimeMillis());
        paste.setModified(System.currentTimeMillis());
        paste.setTitle(title);
        paste.setText(content);

        DatabaseReference newPasteRef = FirebaseDatabase.getInstance().getReference("pastes/" + UID).push();
        String pasteId = newPasteRef.getKey();
        paste.setId(pasteId);

        //handle image import
        if(imageUri!=null) {
            ImageModel imageModel = new ImageModel();
            imageModel.setId(String.valueOf(System.currentTimeMillis()));
            imageModel.setPasteId(pasteId);
            imageModel.setFileName(imageModel.getId() + "_"+PasteUtils.getFileName(this, imageUri));
            ImageImportService.startActionImport(this, imageUri, imageModel);
            paste.getUrls().put(imageModel.getId(),imageModel);
        }
        newPasteRef.setValue(paste);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSave: savePaste();
                finish();
                break;
            case R.id.buttonDiscard:
                finish();
                break;
        }
    }
}
