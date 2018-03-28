package com.paymentgatway.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int DROP_IN_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonPaypal).setOnClickListener(MainActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DROP_IN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String paymentMethodNonce = result.getPaymentMethodNonce().getNonce();
                // send paymentMethodNonce to your server
                Toast.makeText(MainActivity.this, "Payment successful", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // canceled
                Toast.makeText(MainActivity.this, "Payment canceled", Toast.LENGTH_SHORT).show();
            } else {
                // an error occurred, checked the returned exception
                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPaypal:
                clickPaypal();
                break;
        }
    }

    /**
     * Click on paypal button
     */
    private void clickPaypal() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken("sandbox_3nwwtzbr_zm2qtbx9kbb9n8v3");
        startActivityForResult(dropInRequest.getIntent(MainActivity.this), DROP_IN_REQUEST);
    }


}
