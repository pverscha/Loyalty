package com.abyx.loyalty.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abyx.loyalty.tasks.APIConnectorCallback;
import com.abyx.loyalty.tasks.APIConnectorTask;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.tasks.DownloadImageTask;
import com.abyx.loyalty.contents.IO;
import com.abyx.loyalty.extra.ProgressIndicator;
import com.abyx.loyalty.R;
import com.abyx.loyalty.tasks.ThumbnailDownloader;
import com.abyx.loyalty.extra.Utils;
import com.google.zxing.BarcodeFormat;

import java.util.List;


public class FinishActivity extends DetailedActivity implements ProgressIndicator, APIConnectorCallback {
    private Card data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        intent = getIntent();
        initStoreData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_finish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            IO io = new IO(getApplicationContext());
            List<Card> allCards = io.load();
            allCards.add(new Card(intent.getStringExtra("STORENAME"),
                    intent.getStringExtra("BARCODE"), BarcodeFormat.valueOf(intent.getStringExtra("FORMAT"))));
            io.save(allCards);
            Intent intent = new Intent(FinishActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setDone(boolean done){
        if (done){
            progress.setVisibility(View.INVISIBLE);
        } else {
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setProgress(double percentage){
        //Nothing to do here
        //This is only relevant for progressbars which support percentages
    }

    protected void initStoreData() {
        new APIConnectorTask(this, this).execute(intent.getStringExtra("STORENAME"));
    }

    @Override
    public void onAPIReady(String url){
        data = new Card(intent.getStringExtra("STORENAME"),
                intent.getStringExtra("BARCODE"), url, BarcodeFormat.valueOf(intent.getStringExtra("FORMAT")));
        DownloadImageTask tempDownloader = new DownloadImageTask(logoView, this, data.getImageLocation(), data, true);
        tempDownloader.setProgressIndicator(this);
        tempDownloader.execute(data.getImageURL());
        new ThumbnailDownloader(this, data.getImageLocation(), data).execute(data.getImageURL());
        barcodeImage.setImageBitmap(encodeAsBitmap(data.getBarcode(), data.getFormat()));
        barcodeView.setText(data.getBarcode());
        setTitle(data.getName());
    }

    @Override
    public void onAPIException(String title, String message){
        Utils.showInformationDialog(title, message, this, Utils.createDismissListener());
        data = new Card(intent.getStringExtra("STORENAME"),
                intent.getStringExtra("BARCODE"), BarcodeFormat.valueOf(intent.getStringExtra("FORMAT")));
        DownloadImageTask tempDownloader = new DownloadImageTask(logoView, this, data.getImageLocation(), data, true);
        tempDownloader.setProgressIndicator(this);
        tempDownloader.execute(data.getImageURL());
        new ThumbnailDownloader(this, data.getImageLocation(), data).execute(data.getImageURL());
        barcodeImage.setImageBitmap(encodeAsBitmap(data.getBarcode(), data.getFormat()));
        barcodeView.setText(data.getBarcode());
        setTitle(data.getName());
    }
}
