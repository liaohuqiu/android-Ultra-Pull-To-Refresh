## [中文版文档](https://github.com/liaohuqiu/android-CLog/blob/master/README-cn.md)

# Ultra Pull To Refresh
`API LEVEL >= 8`

* Supports all of the view: ListView, GridView, ScrollView, FrameLayout, or Even a single TextView.
    <div><img src='http://srain-github.qiniudn.com/ultra-ptr/contains-all-of-views.gif' width="300px"/></div>

* Supports all of the refresh types.
    * pull to refresh
        <div><img src='http://srain-github.qiniudn.com/ultra-ptr/pull-to-refresh.gif' width="300px"/></div>
    * release to refresh
        <div><img src='http://srain-github.qiniudn.com/ultra-ptr/release-to-refresh.gif' width="300px"/></div>
    * keep header when refresh
        <div><img src='http://srain-github.qiniudn.com/ultra-ptr/keep-header.gif' width="300px"/></div>
    * hide header when refresh
        <div><img src='http://srain-github.qiniudn.com/ultra-ptr/hide-header.gif' width="300px"/></div>
    * auto refresh
        <div><img src='http://srain-github.qiniudn.com/ultra-ptr/auto-refresh.gif' width="300px"/></div>

* Easy to use and custiomize

* Multiple build-in style:

    * classic style with last update time
    * StoreHouse style

## Multiple build in style:

* Classic Style
* 
* StoreHouse Style

    <div class='row'>
        <div class='col-md-4'>
            <p><img src='http://srain-github.qiniudn.com/ultra-ptr/store-house-string.gif' width="300px"/></p>
            <p>using string</p>
        </div>
        <div class='col-md-4'>
            <p><img src='http://srain-github.qiniudn.com/ultra-ptr/store-house-string-array.gif' width="300px"/></p>
            <p>using string</p>
        </div>
    </div>

# Usage

#### Maven

```xml
<dependency>
    <groupId>in.srain.cube</groupId>
    <artifactId>ultra-ptr</artifactId>
    <type>apklib</type>
    <version>1.0.2</version>
</dependency>
```

#### Config

There are 6 properties:

* Resistence

    This is the resistence while you are moving the frame, default is: `1.7f`.

* Ratio of the Height of the Header to Refresh

    The ratio of the height of the header to trigger refresh, default is: `1.2f`.

* Duration to Close

    The duration for moving from the position you relase the view to the height of header, default is `200ms`.

* Duration to Close Header

    The default value is `1000ms`

* Keep Header while Refreshing

    The default value is `true`.

* Pull to Refresh / Release to Refresh

    The default value is Pull to Refresh.

##### Config in xml

```xml
<in.srain.cube.views.ptr.PtrFrameLayout
    android:id="@+id/store_house_ptr_frame"
    xmlns:cube_ptr="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"

    cube_ptr:ptr_resistance="1.7"
    cube_ptr:ptr_ratio_of_header_height_to_refresh="1.2"
    cube_ptr:ptr_duration_to_close="300"
    cube_ptr:ptr_duration_to_close_header="2000"
    cube_ptr:ptr_keep_header_when_refresh="true"
    cube_ptr:ptr_pull_to_fresh="false" >

    <LinearLayout
        android:id="@+id/store_house_ptr_image_content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/cube_mints_333333"
        android:clickable="true"
        android:padding="10dp">

        <in.srain.cube.image.CubeImageView
            android:id="@+id/store_house_ptr_image"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    </LinearLayout>

</in.srain.cube.views.ptr.PtrFrameLayout>
```

#### Config in java code

```java
// the following are default settings
mPtrFrame.setResistance(1.7f);
mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
mPtrFrame.setDurationToClose(200);
mPtrFrame.setDurationToCloseHeader(1000);
// default is false
mPtrFrame.setPullToRefresh(false);
// default is true
mPtrFrame.setKeepHeaderWhenRefresh(true);
```


# License

Apache 2

# Contact & Help

Please fell free to contact me if there is any problem when using the library.

* srain@php.net
* twitter: https://twitter.com/liaohuqiu
* weibo: http://weibo.com/liaohuqiu
* blog: http://www.liaohuqiu.net
* QQ tribe: 271918140
