/*
 * Copyright (C) 2018 The OpenFlutter Organization
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.fb.flutter_social.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import cn.fb.flutter_social.constant.WeChatPluginImageSchema;
import cn.fb.flutter_social.constant.WechatPluginKeys;

import io.flutter.plugin.common.PluginRegistry;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import top.zibin.luban.Luban;

public class WeChatThumbnailUtil {
    public static final int SHARE_MINI_PROGRAM_IMAGE_THUMB_LENGTH = 120 * 1024;
    public static final int SHARE_IMAGE_THUMB_LENGTH = 32 * 1024;
    private static final int COMMON_THUMB_WIDTH = 150;

    private WeChatThumbnailUtil() {
    }

    public static byte[] thumbnailForMiniProgram(String thumbnail, PluginRegistry.Registrar registrar) {
        File file;
        if (thumbnail.startsWith(WeChatPluginImageSchema.SCHEMA_ASSETS)) {
            file = getAssetFile(thumbnail, registrar);
        } else if (thumbnail.startsWith(WeChatPluginImageSchema.SCHEMA_FILE)) {
            String pathWithoutUri = thumbnail.substring(WeChatPluginImageSchema.SCHEMA_FILE.length());
            file = new File(pathWithoutUri);
        } else if (thumbnail.startsWith(WeChatPluginImageSchema.SCHEMA_CONTENT)) {
            file = getFileFromContentProvider(registrar, thumbnail);
        } else {
            file = downloadImage(thumbnail);
        }
        return compress(file, registrar, SHARE_MINI_PROGRAM_IMAGE_THUMB_LENGTH);
    }


    private static byte[] fromAssetForMiniProgram(String thumbnail, PluginRegistry.Registrar registrar) {
        File file;
        if (thumbnail.startsWith(WeChatPluginImageSchema.SCHEMA_ASSETS)) {
            file = getAssetFile(thumbnail, registrar);
        } else if (thumbnail.startsWith(WeChatPluginImageSchema.SCHEMA_FILE)) {
            file = new File(thumbnail);
        } else {
            file = downloadImage(thumbnail);
        }
        return compress(file, registrar, SHARE_MINI_PROGRAM_IMAGE_THUMB_LENGTH);
    }

    public static byte[] thumbnailForCommon(String thumbnail, PluginRegistry.Registrar registrar) {
        File file;
        if (thumbnail.startsWith(WeChatPluginImageSchema.SCHEMA_ASSETS)) {
            file = getAssetFile(thumbnail, registrar);
        } else if (thumbnail.startsWith(WeChatPluginImageSchema.SCHEMA_FILE)) {
            String pathWithoutUri = thumbnail.substring(WeChatPluginImageSchema.SCHEMA_FILE.length());
            file = new File(pathWithoutUri);
        } else if (thumbnail.startsWith(WeChatPluginImageSchema.SCHEMA_CONTENT)) {
            file = getFileFromContentProvider(registrar, thumbnail);
        } else {
            file = downloadImage(thumbnail);
        }
        return compress(file, registrar, SHARE_IMAGE_THUMB_LENGTH);
    }

    private static byte[] compress(File file, PluginRegistry.Registrar registrar, int resultMaxLength) {
        if (file == null) {
            return new byte[]{};
        }


        try {
            File compressedFile = Luban
                    .with(registrar.context())
                    .ignoreBy(resultMaxLength)
                    .setTargetDir(registrar.context().getCacheDir().getAbsolutePath())
                    .get(file.getAbsolutePath());
            if (compressedFile.length() < resultMaxLength) {
                Source source = Okio.source(compressedFile);
                BufferedSource bufferedSource = Okio.buffer(source);
                byte[] bytes = bufferedSource.readByteArray();
                source.close();
                bufferedSource.close();
                return bytes;
            }
            return createScaledBitmapWithRatio(compressedFile, resultMaxLength);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    private static byte[] createScaledBitmapWithRatio(File file, int resultMaxLength) {

        Bitmap originBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Bitmap result = ThumbnailCompressUtil.createScaledBitmapWithRatio(originBitmap, resultMaxLength, true);

        String path = file.getAbsolutePath();
        String suffix = path.substring(path.lastIndexOf("."), path.length());
        return bmpToByteArray(result, suffix, true);


    }

    private static byte[] createScaledBitmap(File file, int resultMaxLength, int scaledWidth) {
        Bitmap originBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        Bitmap result = null;

        int width = scaledWidth;
        while (width > 10) {
            result = ThumbnailCompressUtil.createScaledBitmap(originBitmap, width, false);
            if (result.getByteCount() < resultMaxLength * 1024) {
                break;
            } else {
                width = width - 10;
            }
        }

        originBitmap.recycle();

        return bmpToByteArray(result, ".png", true);
    }

    private static byte[] bmpToByteArray(Bitmap bitmap, String suffix, boolean recycle) {
//        int bytes = bitmap.getByteCount();
//        ByteBuffer buf = ByteBuffer.allocate(bytes);
//        bitmap.copyPixelsToBuffer(buf);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        if (suffix.toLowerCase().equals(".jpg") || suffix.toLowerCase().equals(".jpeg")) {
            format = Bitmap.CompressFormat.JPEG;
        }

        bitmap.compress(format, 10, byteArrayOutputStream);
        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        byte[] result = null;

        if (recycle) {
            bitmap.recycle();
        }
        Source source = Okio.source(inputStream);
        BufferedSource bufferedSource = Okio.buffer(source);
        try {
            result = bufferedSource.readByteArray();
            source.close();
            bufferedSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxkb
     *
     * @param bitmap
     * @param maxKb
     * @return
     */
    public static byte[] bmpToByteArray(Bitmap bitmap, int maxKb) {

        int kb = maxKb * 1024;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > kb && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }

    public static byte[] compressByQuality(final Bitmap src,
                                           final long maxByteSize,
                                           final boolean recycle) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);


        byte[] bytes;

        if (baos.size() <= maxByteSize) {
            bytes = baos.toByteArray();
        } else {
            baos.reset();
            src.compress(Bitmap.CompressFormat.JPEG, 0, baos);
            if (baos.size() >= maxByteSize) {
                bytes = baos.toByteArray();
            } else {
                // find the best quality using binary search
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(Bitmap.CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return bytes;
    }

    private static File getAssetFile(String thumbnail, PluginRegistry.Registrar registrar) {
        File result = null;
        int endIndex = thumbnail.length();
        int indexOfPackage = thumbnail.indexOf(WechatPluginKeys.PACKAGE);
        if (indexOfPackage > 0) {
            endIndex = indexOfPackage;
        }
        String key = thumbnail.substring(WeChatPluginImageSchema.SCHEMA_ASSETS.length(), endIndex);
//        flutter_assets/packages/flutter_gallery_assets/ali_connors.jpg?package=flutter_gallery_assets
        AssetFileDescriptor fileDescriptor = AssetManagerUtil.openAsset(registrar, key, getPackage(thumbnail));

        if (fileDescriptor != null) {
            try {
                result = File.createTempFile(UUID.randomUUID().toString(), getSuffix(key));
                OutputStream outputStream = new FileOutputStream(result);
                BufferedSink sink = Okio.buffer(Okio.sink(outputStream));
                Source source = Okio.source(fileDescriptor.createInputStream());
                sink.writeAll(source);
                source.close();
                sink.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String getPackage(String assetsName) {
        String packageStr = null;
        if (assetsName.contains(WechatPluginKeys.PACKAGE)) {
            int index = assetsName.indexOf(WechatPluginKeys.PACKAGE);
            packageStr = assetsName.substring(index + WechatPluginKeys.PACKAGE.length(), assetsName.length());
        }
        return packageStr;
    }

    private static File downloadImage(String url) {
        File result = null;
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).get().build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (response.isSuccessful() && responseBody != null) {
                result = File.createTempFile(UUID.randomUUID().toString(), getSuffix(url));
                OutputStream outputStream = new FileOutputStream(result);
                BufferedSink sink = Okio.buffer(Okio.sink(outputStream));
                sink.writeAll(responseBody.source());
                sink.flush();
                sink.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    private static File inputStreamToFile(InputStream inputStream, String suffix) {
        File result = null;
        try {
            result = File.createTempFile(UUID.randomUUID().toString(), suffix);
            OutputStream outputStream = new FileOutputStream(result);
            BufferedSink sink = Okio.buffer(Okio.sink(outputStream));
            Source source = Okio.source(inputStream);
            sink.writeAll(source);
            sink.flush();
            sink.close();
            source.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String getSuffix(String path) {
        String suffix = ".jpg";
        if (path.endsWith(".png")) {
            suffix = ".png";
        }
        return suffix;
    }

    private static File getFileFromContentProvider(PluginRegistry.Registrar registrar, String path) {
        Source source = null;
        BufferedSink sink = null;

        File file = null;
        try {
            Context context = registrar.context().getApplicationContext();
            Uri uri = Uri.parse(path);
            String suffix = null;
            String mimeType = context.getContentResolver().getType(uri);
            if (TextUtils.equals(mimeType, "image/jpeg") || TextUtils.equals(mimeType, "image/jpg")) {
                suffix = ".jpg";
            } else if (TextUtils.equals(mimeType, "image/png")) {
                suffix = ".png";
            }


            file = File.createTempFile(UUID.randomUUID().toString(), suffix);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            if (inputStream == null) {
                return null;
            }
            OutputStream outputStream = new FileOutputStream(file);
            sink = Okio.buffer(Okio.sink(outputStream));
            source = Okio.source(inputStream);
            sink.writeAll(source);
            source.close();
            sink.close();
        } catch (IOException e) {
            Log.i("fluwx", "reading image failed:\n" + e.getMessage());
        }

        return file;
    }
}
