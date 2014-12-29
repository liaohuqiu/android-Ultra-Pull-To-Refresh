package in.srain.cube.views.ptr.demo.app;

import android.app.Application;
import in.srain.cube.Cube;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.request.RequestCacheManager;
import in.srain.cube.util.CLog;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.demo.image.DemoDuiTangImageResizer;

public class CubeDemoApplication extends Application {

    public static CubeDemoApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        String environment = "";

        if (environment.equals("production")) {
            CLog.setLogLevel(CLog.LEVEL_ERROR);
        } else if (environment.equals("beta")) {
            CLog.setLogLevel(CLog.LEVEL_WARNING);
        } else {
            // development
            CLog.setLogLevel(CLog.LEVEL_VERBOSE);
        }

        // Debug.DEBUG_IMAGE = true;
        PtrFrameLayout.DEBUG = true;

        ImageLoaderFactory.setDefaultImageResizer(DemoDuiTangImageResizer.getInstance());
        String dir = "request-cache";
        // ImageLoaderFactory.init(this);
        RequestCacheManager.init(this, dir, 1024 * 10, 1024 * 10);
        Cube.onCreate(this);
    }
}