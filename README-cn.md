# Ultra Pull To Refresh

支持 `API LEVEL >= 8`

* 先上两张StoreHouse风格的截图! 感谢 [CBStoreHouseRefreshControl](https://github.com/liaohuqiu/CBStoreHouseRefreshControl).
    <div class='row'>
        <img src='http://srain-github.qiniudn.com/ultra-ptr/store-house-string-array.gif' width="300px" style='border: #f1f1f1 solid 1px'/>
        <img src='http://srain-github.qiniudn.com/ultra-ptr/store-house-string.gif' width="300px" style='border: #f1f1f1 solid 1px'/>
    </div>


* **支持所有的View**: 
    ListView, GridView, ScrollView, FrameLayout, or Even a single TextView.
    <div><img src='http://srain-github.qiniudn.com/ultra-ptr/contains-all-of-views.gif' width="300px" style='border: #f1f1f1 solid 1px'/></div>

* 支持各种下拉刷新交互.
    * 下拉刷新(iOS风格)
        <div><img src='http://srain-github.qiniudn.com/ultra-ptr/pull-to-refresh.gif' width="300px" style='border: #f1f1f1 solid 1px'/></div>

    * 释放刷新(经典风格)
        <div><img src='http://srain-github.qiniudn.com/ultra-ptr/release-to-refresh.gif' width="300px" style='border: #f1f1f1 solid 1px'/></div>

    * 刷新时，头部保持(新浪微博)

        <img src='http://srain-github.qiniudn.com/ultra-ptr/keep-header.gif' width="300px"/>

    * 刷新时，头部不保持(微信朋友圈)

        <img src='http://srain-github.qiniudn.com/ultra-ptr/keep-header.gif' width="300px" sytle='border: #f1f1f1 solid 1px'/>

    * 自动刷新，进入界面时自动刷新

        <img src='http://srain-github.qiniudn.com/ultra-ptr/auto-refresh.gif' width="300px" sytle='border: #f1f1f1 solid 1px'/></div>

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

有6个参数可配置:

* 阻尼系数

    默认: `1.7f`，越大，感觉下拉时越吃力。

* 触发刷新时移动的位置比例

    默认，`1.2f`，移动达到头部高度1.2倍时可触发刷新操作。

* 回弹延时

    默认 `200ms`，回弹到刷新高度所用时间

* 头部回弹时间

    默认`1000ms`

* 刷新是保持头部

    默认值 `true`.

* 下拉刷新 / 释放刷新

    默认为释放刷新

##### xml中配置示例

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

### 也可以在java代码中配置

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

## StoreHouse 风格

* 使用字符串, 支持A-Z, 0-7 以及 `-` `.`

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

* 使用资源文件中的路径信息

```java
header.initWithStringArray(R.array.storehouse);
```

资源文件 `res/values/arrays.xml` 内容如下:

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


# License

Apache 2

# Contact & Help

Please fell free to contact me if there is any problem when using the library.

* srain@php.net
* twitter: https://twitter.com/liaohuqiu
* 微博: http://weibo.com/liaohuqiu
* blog: http://www.liaohuqiu.net
* QQ 群: 271918140
