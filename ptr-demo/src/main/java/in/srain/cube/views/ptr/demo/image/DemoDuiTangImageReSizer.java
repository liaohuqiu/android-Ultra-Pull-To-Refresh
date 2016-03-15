package in.srain.cube.views.ptr.demo.image;

import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageReSizer;
import in.srain.cube.util.CLog;

/**
 * Created by srain on 8/19/14.
 */
public class DemoDuiTangImageReSizer extends DefaultImageReSizer {


    private static DemoDuiTangImageReSizer sInstance;

    public static DemoDuiTangImageReSizer getInstance() {
        if (sInstance == null) {
            sInstance = new DemoDuiTangImageReSizer();
        }
        return sInstance;
    }

    private static String TAG = "thumb";
    private static String SP = "_";
    private static String DOT = ".";

    private static final int[] CDN_FIX_WIDTH_SIZE = {110, 150, 170, 220, 240, 290, 450, 580, 620, 790};

    @Override
    public String getRemoteUrl(ImageTask imageTask) {
        String url = imageTask.getOriginUrl();
        int size = findBestCDNSize(CDN_FIX_WIDTH_SIZE, imageTask.getRequestSize().x, true);
        url = url.replace(TAG, TAG + DOT + size + SP + size);
        CLog.d("test", "getRemoteUrl: %s %s", imageTask.getRequestSize(), url);
        return url;
    }

    private static int binarySearch(int[] srcArray, int des, boolean higher) {
        int low = 0;
        int high = srcArray.length - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            if (des == srcArray[middle]) {
                return middle;
            } else if (des < srcArray[middle]) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        if (high < 0)
            return 0;
        if (higher) {
            if (des > srcArray[high] && high + 1 <= srcArray.length - 1) {
                high = high + 1;
            }
        } else {
            if (des < srcArray[high] && high - 1 >= 0)
                high = high - 1;
        }
        return high;
    }

    private static int findBestCDNSize(int[] array, int size, boolean higher) {

        if (size >= array[array.length - 1]) {
            return array[array.length - 1];
        }

        int pos = binarySearch(array, size, higher);
        return array[pos];
    }
}
