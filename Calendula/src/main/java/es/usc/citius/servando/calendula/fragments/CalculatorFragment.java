package es.usc.citius.servando.calendula.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.persistence.Patient;


public class CalculatorFragment extends Fragment{

    Button btSelectPic;
    ImageView ivPic;

    private int IMAGE_REQUEST_CODE = 0;
    private int RESULT_OK = 1;
    private int RESIZE_REQUEST_CODE = 2;


    public CalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        btSelectPic = view.findViewById(R.id.bt_select_pic);
        ivPic = view.findViewById(R.id.pic);

//        btSelectPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, 0);
//            }
//        });

//        Intent intent = new Intent(
//                Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        // 打开手机相册,设置请求码
//        startActivityForResult(intent, IMAGE_REQUEST_CODE);

        return view;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode,resultCode,data);
//        if (data == null) {
//            return;
//        }
//
//        try {
//            Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            path = cursor.getString(columnIndex);  //获取照片路径
//            cursor.close();
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            iv_photo.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            // TODO Auto-generatedcatch block
//            e.printStackTrace();
//        }
//
//    }
//
//    private void selectPicture() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, REQUEST_CODE_GALLERY);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GALLERY) {
//            Uri pictureUri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),pictureUri);
//                BitmapUtils.compress(bitmap,1024);
//                displayPicture(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK) {
//            return;
//        } else {
//            switch (requestCode) {
//                case IMAGE_REQUEST_CODE:
//                    resizeImage(data.getData());
//                    break;
//
//                case RESIZE_REQUEST_CODE:
//                    if (data != null) {
//                        showResizeImage(data);
//                    }
//                    break;
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public void resizeImage(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        //裁剪的大小
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
//        intent.putExtra("return-data", true);
//        //设置返回码
//        startActivityForResult(intent, RESIZE_REQUEST_CODE);
//    }
//    private void showResizeImage(Intent data) {
//        Bundle extras = data.getExtras();
//        if (extras != null) {
//            Bitmap photo = extras.getParcelable("data");
//            //裁剪之后设置保存图片的路径
//            String path = getFilesDir().getPath() + File.separator + IMAGE_FILE_NAME;
//            //压缩图片
//            ImageUtils.saveImage(photo, path);
//            new BitmapDrawable();
//            Drawable drawable = new BitmapDrawable(photo);
//            iv_photo.setImageDrawable(drawable);
//        }
//    }


    public interface OnUserEditListener {
//        void onUserEdited(Patient p);

        void onUserCreated(Patient p);
    }
}