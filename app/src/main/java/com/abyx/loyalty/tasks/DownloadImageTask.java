/*
 * Copyright 2017 Abyx (https://abyx.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abyx.loyalty.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.tasks.ImageTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownloadImageTask extends ImageTask {

    public DownloadImageTask(ImageView bmImage, Context context, String picName, Card data) {
        super(bmImage, context, picName, data);
    }

    public DownloadImageTask(ImageView bmImage, Context context, String picName, Card data, boolean showError){
        super(bmImage, context, picName, data, showError);
    }

    /**
     * Function that downloads the image located at the specified url.
     *
     * @param urls The url(s) at which the image that has to be downloaded is situated
     * @return The image downloaded from the specified url as a Bitmap-object
     * @see Bitmap
     */
    @Nullable
    protected Bitmap doInBackground(String... urls){
        String urldisplay = urls[0];
        Bitmap mIcon11;
        try {
            File file = context.getFileStreamPath(picName);
            if (file == null || !file.exists()) {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                FileOutputStream fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
                mIcon11.compress(Bitmap.CompressFormat.PNG, 75, fos);
            } else {
                FileInputStream fis = context.openFileInput(picName);
                mIcon11 = BitmapFactory.decodeStream(fis);
                fis.close();
            }
        } catch (IOException | IllegalArgumentException | NullPointerException e){
            return null;
        }
        return mIcon11;
    }

}
