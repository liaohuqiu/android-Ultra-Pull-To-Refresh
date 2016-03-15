package in.srain.cube.views.ptr.demo.data;

import in.srain.cube.request.*;

public class DemoRequestData {

    public static void getImageList(final RequestFinishHandler<JsonData> requestFinishHandler) {

        CacheAbleRequestHandler requestHandler = new CacheAbleRequestJsonHandler() {
            @Override
            public void onCacheAbleRequestFinish(JsonData data, CacheAbleRequest.ResultType type, boolean outOfDate) {
                requestFinishHandler.onRequestFinish(data);
            }
        };

        CacheAbleRequest<JsonData> request = new CacheAbleRequest<JsonData>(requestHandler);
        String url = "http://cube-server.liaohuqiu.net/api_demo/image-list.php";
        request.setCacheTime(3600);
        request.setTimeout(1000);
        request.getRequestData().setRequestUrl(url);
        request.setAssertInitDataPath("request_init/demo/image-list.json");
        request.setCacheKey("image-list-1");
        request.send();
    }
}
