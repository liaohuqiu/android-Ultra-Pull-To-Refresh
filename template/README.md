###### Welcome to follow me on GitHub or Twitter

GitHub: https://github.com/liaohuqiu

Twitter: https://twitter.com/liaohuqiu

---

[![Build Status](https://travis-ci.org/liaohuqiu/android-Ultra-Pull-To-Refresh.svg?branch=master)](https://travis-ci.org/liaohuqiu/android-Ultra-Pull-To-Refresh)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Ultra%20Pull%20To%20Refresh-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1180)

#### [中文版文档](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh/blob/master/README-cn.md)

Wanna auto-load-more? This will be what you want: https://github.com/liaohuqiu/android-cube-app

# Ultra Pull To Refresh

It's a replacement for the deprecated pull to refresh solution. It can contain any view you want.

It's easy to use and more powerful than SwipeRefreshLayout.

It's well designed, you can customize the UI effect you want as easy as adding a headview to ListView.

Support `API LEVEL >= 8`, all snapshots are taken from Genymotion, 2.3.7.

[Download APK](https://raw.githubusercontent.com/liaohuqiu/android-Ultra-Pull-To-Refresh/master/ptr-demo.apk)

* StoreHouse Style first! Thanks to [CBStoreHouseRefreshControl](https://github.com/coolbeet/CBStoreHouseRefreshControl).
    <div class='row'>
        <img src='http://srain-github.qiniudn.com/ultra-ptr/store-house-string-array.gif' width="300px" style='border: #f1f1f1 solid 1px'/>
        <img src='http://srain-github.qiniudn.com/ultra-ptr/store-house-string.gif' width="300px" style='border: #f1f1f1 solid 1px'/>
    </div>

* Material Style, added @ 2014-12-09. **There is a beautiful shadow which looks terrible in gif snapshot. Please Check out the DEMO.**
    <div class='row'>
        <img src='http://srain-github.qiniudn.com/ultra-ptr/material-style.gif' width="300px"/>
    </div>

* **Supports all of the views**: 
    ListView, GridView, ScrollView, FrameLayout, or Even a single TextView.
    <div><img src='http://srain-github.qiniudn.com/ultra-ptr/contains-all-of-views.gif' width="300px" style='border: #f1f1f1 solid 1px'/></div>

* Supports all of the refresh types.
    * pull to refresh
        <div><img src='http://srain-github.qiniudn.com/ultra-ptr/pull-to-refresh.gif' width="300px" style='border: #f1f1f1 solid 1px'/></div>
    * release to refresh
        <div><img src='http://srain-github.qiniudn.com/ultra-ptr/release-to-refresh.gif' width="300px" style='border: #f1f1f1 solid 1px'/></div>

    * keep header when refresh 

        <img src='http://srain-github.qiniudn.com/ultra-ptr/keep-header.gif' width="300px"/>

    * hide header when refresh

        <img src='http://srain-github.qiniudn.com/ultra-ptr/hide-header.gif' width="300px" sytle='border: #f1f1f1 solid 1px'/>

    * auto refresh

        <img src='http://srain-github.qiniudn.com/ultra-ptr/auto-refresh.gif' width="300px" sytle='border: #f1f1f1 solid 1px'/></div>

# Usage

#### Maven Central

This project has been pushed to Maven Central, both in `aar` and `apklib`.

The latest version: `{ptr_lib_version}`, has been published to: https://oss.sonatype.org/content/repositories/snapshots, in gradle:

```
maven {
    url 'https://oss.sonatype.org/content/repositories/snapshots'
}
```

The stable version: `{ptr_lib_stable_version}`, https://oss.sonatype.org/content/repositories/releases, in gradle:

```
mavenCentral()
```

pom.xml, latest version:

```xml
<dependency>
    <groupId>in.srain.cube</groupId>
    <artifactId>ultra-ptr</artifactId>
    <type>aar</type>
    <!-- or apklib format, if you want -->
    <!-- <type>apklib</type> -->
    <version>{ptr_lib_version}</version>
</dependency>
```

pom.xml, stable version:

```xml
<dependency>
    <groupId>in.srain.cube</groupId>
    <artifactId>ultra-ptr</artifactId>
    <type>aar</type>
    <!-- or apklib format, if you want -->
    <!-- <type>apklib</type> -->
    <version>{ptr_lib_stable_version}</version>
</dependency>
```

gradle, latest version:

```
compile 'in.srain.cube:ultra-ptr:{ptr_lib_version}'
```

gradle, stable version:

```
compile 'in.srain.cube:ultra-ptr:{ptr_lib_stable_version}'
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

    The default value is Release to Refresh.

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

### Or config in java code

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

### Other Config

*  `setPinContent`. Pin the content, only the `HeaderView` will be moved. 

    This's the the performance of material style in support package v19.

## StoreHouse Style

* Config using string:

```java
// header
final StoreHouseHeader header = new StoreHouseHeader(getContext());
header.setPadding(0, LocalDisplay.dp2px(15), 0, 0);

/**
 * using a string, support: A-Z 0-9 - .
 * you can add more letters by {@link in.srain.cube.views.ptr.header.StoreHousePath#addChar}
 */
header.initWithString('Alibaba');
```

* Config using string array from xml:

```java
header.initWithStringArray(R.array.storehouse);
```

And in `res/values/arrays.xml`:

```xml
<resources>
    <string-array name="storehouse">
        <item>0,35,12,42,</item>
        <item>12,42,24,35,</item>
        <item>24,35,12,28,</item>
        <item>0,35,12,28,</item>
        <item>0,21,12,28,</item>
        <item>12,28,24,21,</item>
        <item>24,35,24,21,</item>
        <item>24,21,12,14,</item>
        <item>0,21,12,14,</item>
        <item>0,21,0,7,</item>
        <item>12,14,0,7,</item>
        <item>12,14,24,7,</item>
        <item>24,7,12,0,</item>
        <item>0,7,12,0,</item>
    </string-array>
</resources>
```

# Process Refresh

There is a `PtrHandler`, by which you can refresh the data.

```
public interface PtrHandler {

    /**
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     * <p/>
     * {@link in.srain.cube.views.ptr.PtrDefaultHandler#checkContentCanBePulledDown}
     */
    public boolean checkCanDoRefresh(final PtrFrameLayout frame, final View content, final View header);

    /**
     * When refresh begin
     *
     * @param frame
     */
    public void onRefreshBegin(final PtrFrameLayout frame);
}
```

An example:

```java
ptrFrame.setPtrHandler(new PtrHandler() {
    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.refreshComplete();
            }
        }, 1800);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }
});
```


# Customize

You can add a `PtrUIHandler` to `PtrFrameLayout` to implement any UI effect you want.

```java
public interface PtrUIHandler {

    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     *
     * @param frame
     */
    public void onUIReset(PtrFrameLayout frame);

    /**
     * prepare for loading
     *
     * @param frame
     */
    public void onUIRefreshPrepare(PtrFrameLayout frame);

    /**
     * perform refreshing UI
     */
    public void onUIRefreshBegin(PtrFrameLayout frame);

    /**
     * perform UI after refresh
     */
    public void onUIRefreshComplete(PtrFrameLayout frame);

    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, int oldPosition, int currentPosition, float oldPercent, float currentPercent);
}
```

# Q & A

*  work with ViewPager: `disableWhenHorizontalMove()`

*  work with LongPressed, `setInterceptEventWhileWorking()`

# Contact & Help

Please fell free to contact me if there is any problem when using the library.

* srain@php.net
* twitter: https://twitter.com/liaohuqiu
* weibo: http://weibo.com/liaohuqiu
* blog: http://www.liaohuqiu.net

    1. About how to use cube-sdk / Ultra Ptr: 271918140 (cube-sdk)

        This the rule for our tribes, please read it before you request to join: https://github.com/liaohuqiu/qq-tribe-rule

    2. For those who like thinking independently and are good at solving problem independently. Please join us, we are all here on Slack: 
    
        http://join-add1bit.liaohuqiu.net/
