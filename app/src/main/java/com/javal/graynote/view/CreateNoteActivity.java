package com.javal.graynote.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.javal.graynote.R;
import com.javal.graynote.database.NotesDatabase;
import com.javal.graynote.entities.Note;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {


    // https://github.com/erensogutlu

    private EditText inputNoteTitle,inputNoteSubtitle;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private ImageView imageNote;
    private String selectedNoteColor;
    private String selectedImagePath;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private AlertDialog dialogDeleteNote;

    private Note alreadyAvailableNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageback = findViewById(R.id.imageBack);
        imageback.setOnClickListener(v -> onBackPressed());

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);

        textDateTime.setText(
                new SimpleDateFormat("EEEE , dd MMMM yyyy HH:mm a",Locale.getDefault())
                        .format(new Date())
        );

       ImageView imageSave = findViewById(R.id.imageSave);
       imageSave.setOnClickListener(v -> saveNote());

       selectedNoteColor = "#808080";
       selectedImagePath = "";

        if (getIntent().getBooleanExtra("isViewOrUpdate",false)) {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

       initMiscellaneous();
       setSubtitleIndicatorColor();

    }

    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        textDateTime.setText(alreadyAvailableNote.getImagePath());

        if (alreadyAvailableNote.getImagePath() != null && alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }

    }


    private void saveNote() {

      if (inputNoteTitle.getText().toString().trim().isEmpty()) {
          Toast.makeText(this, "ENTER NOTE TİTLE", Toast.LENGTH_SHORT).show();
          return;
      }else if (inputNoteSubtitle.getText().toString().trim().isEmpty()) {
          Toast.makeText(this, "Note can't be empty", Toast.LENGTH_SHORT).show();
          return;
      }

      final Note note = new Note();
      note.setTitle(inputNoteTitle.getText().toString());
      note.setSubtitle(inputNoteSubtitle.getText().toString());
      note.setDateTime(textDateTime.getText().toString());
      note.setColor(selectedNoteColor);
      note.setImagePath(selectedImagePath);


      if (alreadyAvailableNote != null) {
          note.setId(alreadyAvailableNote.getId());
      }


      @SuppressLint("StaticFieldLeak")
      class SaveNoteTask extends AsyncTask<Void, Void, Void> {

          @Override
          protected Void doInBackground(Void... voids) {
              NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
              return null;
          }

          @Override
          protected void onPostExecute(Void aVoid) {
              super.onPostExecute(aVoid);
              Intent intent = new Intent();
              setResult(RESULT_OK,intent);
              finish();
          }

      }
       new SaveNoteTask().execute();
    }

    private void initMiscellaneous() {
       final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
       final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
       layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(v -> {
           if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
               bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
           }else {
               bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
           }
       });

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(v -> {
            selectedNoteColor = "#808080";
            imageColor1.setImageResource(R.drawable.ic_done);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(v -> {
            selectedNoteColor = "#FFC300";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(R.drawable.ic_done);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(v -> {
            selectedNoteColor = "#C70039";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(R.drawable.ic_done);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(v -> {
            selectedNoteColor = "#6082B6";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(R.drawable.ic_done);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(v -> {
            selectedNoteColor = "#000000";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(R.drawable.ic_done);
            setSubtitleIndicatorColor();
        });

        if (alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()) {
            switch (alreadyAvailableNote.getColor()) {
                case "#FFC300":
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#C70039":
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#6082B6":
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }

        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
               if (ContextCompat.checkSelfPermission(
                       getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
               ) != PackageManager.PERMISSION_GRANTED) {
                   ActivityCompat.requestPermissions(
                           CreateNoteActivity.this,
                           new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                           REQUEST_CODE_STORAGE_PERMISSION
                   );
               } else {
                   selectImage();
               }

            }
        });

        if (alreadyAvailableNote != null) {
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                   showDeleteNoteDialog();
                }
            });
        }

    }

    private void showDeleteNoteDialog() {
        if (dialogDeleteNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    class DeleteNoteTask extends AsyncTask<Void, Void, Void> {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent = new Intent();
                            intent.putExtra("İsNoteDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }

                    new DeleteNoteTask().execute();

                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
       dialogDeleteNote.show();
    }

   private void setSubtitleIndicatorColor() {
       GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
       gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
   }



   private void selectImage() {

       /* Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
      */
   }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (Exception exception) {
                        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri,null,null,null,null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

// https://github.com/erensogutlu

}





