package c.skin;

import android.app.Application;

import c.skin.Util.SkinUtil;

/**
 * Created by hasee on 2017/7/5.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SkinUtil.getInstance().init(this);
        SkinUtil.getInstance().load();
    }
}
