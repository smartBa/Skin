package c.skin.Util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import c.skin.Listener.ILoaderListener;

/**
 * Created by hasee on 2017/7/5.
 */

public class SkinUtil {
    private static volatile SkinUtil mInstance;
    private Context context;
    private String skinPackageName;
    private String skinPath;
    private Resources mResources;
    public void init(Context ctx) {
        context = ctx.getApplicationContext();
    }
    public int getColorPrimaryDark() {
        if (mResources != null) {
            int identify = mResources.getIdentifier("colorPrimaryDark", "color", skinPackageName);
            return mResources.getColor(identify);
        }
        return -1;
    }
    public int getColor(int Id){
        if(mResources!=null){
            int color=mResources.getColor(Id);
            return color;
        }
        return -1;
    }
    public Drawable getDrawable(int resId) {
        Drawable originDrawable = context.getResources().getDrawable(resId);
        if (mResources == null) {
            return originDrawable;
        }
        String resName = context.getResources().getResourceEntryName(resId);

        int trueResId = mResources.getIdentifier(resName, "drawable", skinPackageName);

        Drawable trueDrawable = null;
        try {
            //L.i("SkinManager getDrawable", "SDK_INT = " + android.os.Build.VERSION.SDK_INT);
            if (android.os.Build.VERSION.SDK_INT < 22) {
                trueDrawable = mResources.getDrawable(trueResId);
            } else {
                trueDrawable = mResources.getDrawable(trueResId, null);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            trueDrawable = originDrawable;
        }

        return trueDrawable;
    }
    public static SkinUtil getInstance() {
        if (mInstance == null) {
            synchronized (SkinUtil.class) {
                if (mInstance == null) {
                    mInstance = new SkinUtil();
                }
            }
        }
        return mInstance;
    }
    public void load(){
        String skinPath=PathUtil.getCustomSkinPath(context);
        load(skinPath,null);
    }
    public void load(String skinPackagePath, final ILoaderListener callback) {

        new AsyncTask<String, Void, Resources>() {

            protected void onPreExecute() {
                if (callback != null) {
                    callback.onStart();
                }
            }

            @Override
            protected Resources doInBackground(String... params) {
                try {
                    if (params.length == 1) {
                        String skinPkgPath = params[0];
                        Log.i("loadSkin", skinPkgPath);
                        File file = new File(skinPkgPath);
                        if (file == null || !file.exists()) {
                            return null;
                        }

                        PackageManager mPm = context.getPackageManager();
                        PackageInfo mInfo = mPm.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES);
                        skinPackageName = mInfo.packageName;

                        AssetManager assetManager = AssetManager.class.newInstance();
                        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                        addAssetPath.invoke(assetManager, skinPkgPath);


                        Resources superRes = context.getResources();
                        Resources skinResource = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());

                        PathUtil.saveSkinPath(context, skinPkgPath);

                        skinPath = skinPkgPath;
                        Log.e("123",skinPath);
                        return skinResource;
                    }
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(Resources result) {
                mResources = result;
                if (mResources != null) {
                    if (callback != null)
                    {
                        callback.onSuccess();
                    }
                    notifySkinUpdate();
                } else {
                    if (callback != null)
                    {
                        callback.onFailed();
                    }
                }
            }

        }.execute(skinPackagePath);
    }
    public void notifySkinUpdate(){

    }
}
