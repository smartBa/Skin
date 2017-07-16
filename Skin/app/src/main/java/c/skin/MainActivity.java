package c.skin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import c.skin.Listener.ILoaderListener;
import c.skin.Util.FileUtils;
import c.skin.Util.SkinUtil;

public class MainActivity extends AppCompatActivity {

    //private int id;
    private ImageView imageView;
    private  String SKIN_DIR;
    private Button button1;
    private Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                if(name=="ImageView"){
                    for (int i = 0; i < attrs.getAttributeCount(); i++) {//遍历当前View的属性
                        String attrValue = attrs.getAttributeValue(i);//属性值
                        if (attrValue.startsWith("@")) {//也就是引用类型，形如@color/red
                            try {
                               // id = Integer.parseInt(attrValue.substring(1));//资源的id
                                //Log.e("123","id="+id);
                            }
                            catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return null;
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        imageView = (ImageView) findViewById(R.id.image);
        SKIN_DIR = FileUtils.getSkinDirPath(getApplicationContext());
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT);
                String skinFullName = SKIN_DIR + File.separator + "skin.skin";
                FileUtils.moveRawToDir(getApplicationContext(), "skin.skin", skinFullName);
                File skin = new File(skinFullName);
                if (!skin.exists()) {
                    Toast.makeText(getApplicationContext(), "请检查" + skinFullName + "是否存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                SkinUtil.getInstance().load(skin.getAbsolutePath(),
                        new ILoaderListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "切换成功", Toast.LENGTH_SHORT).show();
                                changeStatusColor();
                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(getApplicationContext(), "切换失败", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backColor();
            }
        });
    }

//    @Override
//    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        Log.e("123","name="+name);
//        //if (name == "ImageView") {
//            for (int i = 0; i < attrs.getAttributeCount(); i++) {//遍历当前View的属性
//                String attrName = attrs.getAttributeName(i);//属性名
//                String attrValue = attrs.getAttributeValue(i);//属性值
//                Log.e("123","attrName="+attrName);
//                Log.e("123","attrValue="+attrValue);
//                if (attrValue.startsWith("@")) {//也就是引用类型，形如@color/red
//                    try {
//                        id = Integer.parseInt(attrValue.substring(1));//资源的id
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//                }
//           // }
//        }
//        return super.onCreateView(name, context, attrs);
//    }

    public void addView(View view,String name,int id){
        if(name.equals("ImageView")){
            ((ImageView) view).setImageDrawable(SkinUtil.getInstance().getDrawable(id));
        }
        if(name.equals("TextView")){
            ((TextView) view).setTextColor(SkinUtil.getInstance().getColor(id));
        }
    }
    public void changeStatusColor() {
        //如果当前的Android系统版本大于4.4则更改状态栏颜色
        int color = SkinUtil.getInstance().getColorPrimaryDark();
        Log.e("123",color+"");
        ColorDrawable drawable = new ColorDrawable(color);
        getSupportActionBar().setBackgroundDrawable(drawable);
        Log.e("123",imageView.getId()+"");
        imageView.setImageDrawable(SkinUtil.getInstance().getDrawable(R.drawable.one));
    }
    public void backColor(){
        int identify = getResources().getIdentifier("colorPrimary", "color", this.getPackageName());
        ColorDrawable drawable = new ColorDrawable(getResources().getColor(identify));
        getSupportActionBar().setBackgroundDrawable(drawable);
    }
}
