package com.abyx.loyalty;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This activity is responsible for adding new stores to the userdata. This activity itself
 * asks the user for the store name and then starts a new activity containing a barcode scanner.
 * The barcode scanner delivers its results back to this activity where everything is processed and
 * checked.
 *
 * @author Pieter Verschaffelt
 */
public class AddStoreActivity extends PermissionActivity {
    private EditText storeName;
    private Button scanButton;
    private Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);
        storeName = (EditText) findViewById(R.id.storeName);
        scanButton = (Button) findViewById(R.id.scanButton);
        enterButton = (Button) findViewById(R.id.enterButton);
        PackageManager pm = getApplicationContext().getPackageManager();
        //Remove the "scan barcode" button if the device doesn't have a camera
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            ViewGroup layout = (ViewGroup) scanButton.getParent();
            layout.removeView(scanButton);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * Function called when the user clicks the done button. This function checks whether the
     * input is legal and refuses to go through when this isn't the case.
     */
    public void scanBarcode(View view){
        if (storeName.getText().toString().equals("")) {
            storeName.setError(getString(R.string.empty_store_name));
        } else {
            requestCameraPermissions(AddStoreActivity.this, new ReceivedPermission() {
                @Override
                public void onPermissionGranted() {
                    Intent intent = new Intent(AddStoreActivity.this, ScannerActivity.class);
                    intent.putExtra("STORENAME", storeName.getText().toString());
                    startActivityForResult(intent, Utils.ADD_STORE_SCANNER);
                }
            });
        }
    }

    public void enterBarcode(View view){
        if (storeName.getText().toString().equals("")) {
            storeName.setError(getString(R.string.empty_store_name));
        } else {
            Intent intent = new Intent(AddStoreActivity.this, ManualInputActivity.class);
            intent.putExtra("STORENAME", storeName.getText().toString());
            startActivityForResult(intent, Utils.ADD_STORE_SCANNER);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent response){
        if (requestCode == Utils.ADD_STORE_SCANNER && resultCode == RESULT_OK) {
            //A new store was succesfully created
            Intent intent = new Intent();
            //Retrieve the new data-object created by the FinishActivity
            intent.putExtra("DATA", response.getParcelableExtra("DATA"));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
