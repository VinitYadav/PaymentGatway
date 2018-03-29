package com.paymentgatway.android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int DROP_IN_REQUEST = 1001;
    private ProgressHelper progressHelper;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonPaypal).setOnClickListener(MainActivity.this);
        findViewById(R.id.buttonStripe).setOnClickListener(MainActivity.this);
        init();
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
            case R.id.buttonStripe:
                clickStripe();
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

    /**
     * Click on stripe button
     */
    private void clickStripe() {
        showStripePopUp();
    }

    private void init() {
        progressHelper = new ProgressHelper(MainActivity.this);
    }

    /**
     * Show pop up for stripe
     */
    private void showStripePopUp() {
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_card_details);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        final CardInputWidget cardInputWidget = dialog.findViewById(R.id.card_input_widget);
        Button buttonPay = dialog.findViewById(R.id.buttonPay);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card = cardInputWidget.getCard();
                if (card != null) {
                    getStripeToken(card);
                }
            }
        });

        dialog.show();
    }

    /**
     * Get token from card
     */
    private void getStripeToken(Card card) {
        progressHelper.show();
        Stripe stripe = new Stripe(MainActivity.this, "pk_test_WLtvB683lmSKCYXBHD0Wlqyy");
        stripe.createToken(card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your server
                        dialog.dismiss();
                        progressHelper.dismiss();
                        String tokenID = token.getId();
                        Toast.makeText(MainActivity.this, "Payment successful", Toast.LENGTH_SHORT).show();
                    }

                    public void onError(Exception error) {
                        // Show localized error message
                        dialog.dismiss();
                        progressHelper.dismiss();
                        Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Payment canceled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
