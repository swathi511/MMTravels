package com.hjsoft.mmtravels.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.hjsoft.mmtravels.R;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;

/**
 * Created by hjsoft on 16/5/17.
 */
public class SignatureActivity extends AppCompatActivity {

    TextView tvClear,tvSave;
    Bitmap btImg;
    SignatureView signatureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        signatureView =  (SignatureView) findViewById(R.id.signature_view);
        tvClear=(TextView) findViewById(R.id.as_tv_clear);
        tvSave=(TextView) findViewById(R.id.as_tv_save);

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signatureView.clearCanvas();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btImg = signatureView.getSignatureBitmap();
                // path = saveImage(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                btImg.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //getByteCount()
                int byte_size=byteSizeOf(btImg);
                //System.out.println("byte size of the image is "+byte_size);
                byte[] byteArray = stream.toByteArray();
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                Intent intent=new Intent();
                intent.putExtra("Sign",encodedImage);
                setResult(2,intent);
                finish();//finishing activity
            }
        });
    }

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }
}