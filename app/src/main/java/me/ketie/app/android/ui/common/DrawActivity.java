package me.ketie.app.android.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import me.ketie.app.android.R;
import me.ketie.app.android.utils.DocumentSelector;
import me.ketie.app.android.utils.LogUtil;
import me.ketie.app.android.view.DrawImageLayout;
import me.ketie.app.android.view.DrawImageView;

/**
 * Created by henjue on 2015/4/1.
 */
public class DrawActivity extends ActionBarActivity {
    private DrawImageLayout drawLayout;
    private int SELECT_PIC_KITKAT = 0x1001;
    private int SELECT_PIC = 0x1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_itmap);
        drawLayout = (DrawImageLayout) findViewById(R.id.drawLayout);
//        DrawImageView imageView=new DrawImageView(this,120,160,240,320,0);
//        imageView.setImageBitmap(FileUtil.getImagePath("2014-03big/1393830348113"));
//        drawLayout.addImage(imageView);
//
//        DrawImageView imageView2=new DrawImageView(this,130,190,240,320,0);
//        imageView2.setImageBitmap(FileUtil.getImagePath("2014-03big/1393830348113"));
//        drawLayout.addImage(imageView2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == SELECT_PIC_KITKAT || requestCode == SELECT_PIC)) {
            Uri uri = data.getData();
            String path = DocumentSelector.getPath(this, uri);
            LogUtil.i(DrawActivity.class.getSimpleName(), "requestCode:%s\n\rUri:%s\n\rPath:", requestCode, uri.toString(), path);
            DrawImageView imageView = new DrawImageView(this, 0, 0, 240, 320, 0);
            imageView.setImageBitmap(uri);
            drawLayout.addImage(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "选择图片");
        menu.add(0, 1, 1, "生存图片");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                startActivityForResult(intent, SELECT_PIC_KITKAT);
            } else {
                startActivityForResult(intent, SELECT_PIC);
            }
        } else if (item.getItemId() == 1) {
            buildBitmap();
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildBitmap() {
        drawLayout.buildBitmap(new DrawImageLayout.BuildListener() {
            @Override
            public void onComplete(DrawImageLayout thiz, Bitmap bitmap) {
                OutputStream out = null;
                File file = new File(Environment.getExternalStorageDirectory(), "test.png");
                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                            Toast.makeText(DrawActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file), "image/*");
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFaild(DrawImageLayout thiz) {

            }
        });
    }
}
