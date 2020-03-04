package glirt.motun.glirt2.GeneralUsers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import glirt.motun.glirt2.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    EditText username, fullname, email2, password;
    ImageButton register;
    Button txt_login;
    ImageView cd;
    AutoCompleteTextView prof, reg2;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;


    //Some variables will gbe gotten from registration such as name, email...
    //I assumed them to be examples
    private String email = "johndoe@gmail.com";
    private String firstName = "John", lastName = "Doe";

    //Public Key, Secret Key and Encryption Key
    //Set each key from flutter wave
    private final String PUBLIC_KEY = "FLWPUBK-2008905ca52a7c53e10e425403e5ea3a-X", ENCRYPTION_KEY = "ece60ecd6eff035039403ebc", SECRET_KEY = "FLWSECK-ece60ecd6effe76572b6fe0a83fa74ec-X";


    //End points
    //End point to verify payment
    private final String VERIFY_PAYMENT_ENDPOINT = "https://ravesandboxapi.flutterwave.com/flwv3-pug/getpaidx/api/v2/verify";
    private final String CURRENT_DATE_ENDPOINT = "https://floating-forest-76407.herokuapp.com/currentDate";
    private final String PAY_WITH_TOKEN_ENDPOINT = "https://api.ravepay.co/flwv3-pug/getpaidx/api/tokenized/charge";


    //Tokens
    //Card Token
    private String cardToken = "";

    //SharedPreferences to store data such as cardToken, email, dateForNextPayment
    private SharedPreferences sharedPreferences;

    //Volley Request Queue
    //To send request to the api
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);

        username = findViewById(R.id.username);
        email2 = findViewById(R.id.email);
        fullname = findViewById(R.id.fullname);
        prof = findViewById(R.id.profession);
        password = findViewById(R.id.Password);
        register = findViewById(R.id.register);
        reg2 = findViewById(R.id.region2);
        txt_login = findViewById(R.id.txt_login);
        cd = findViewById(R.id.cd);
        prof.setThreshold(1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, profession);
        prof.setAdapter(adapter);

        reg2.setThreshold(1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, region2);
        reg2.setAdapter(adapter2);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });


        cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, createacctActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubscribe();

            }
        });

        if (sharedPreferences.getString("cardToken", null) != null) {
            compareDates();
        }

    }


    private void onClickSubscribe() {
        validated();
    }

    private void validated() {
        final boolean[] ret = {true};
        if(!email2.getText().toString().trim().equals("")&&!fullname.getText().toString().trim().equals("")&&!username.getText().toString().equals("")&&!prof.getText().toString().trim().equals("")&&!reg2.getText().toString().trim().equals("")&&!password.getText().toString().trim().equals("")){
            auth.fetchSignInMethodsForEmail(email2.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    boolean isNewUser = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).isEmpty();
                    if (!isNewUser) {
                        ret[0] = false;
                        Toast.makeText(SignupActivity.this, "Email taken", Toast.LENGTH_LONG).show();
                    }else{
                        initializeRave();
                    }
                }
            });
        }else{
            ret[0] = false;
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show();
        }
    }

    //Function to initialize payment with card
    private void initializeRave() {
        email = email2.getText().toString();
        firstName = username.getText().toString();
        lastName = fullname.getText().toString();

        RavePayManager transaction = new RavePayManager(this);
        transaction.setAmount(1000.0);
        transaction.setCurrency("NGN");
        transaction.setEmail(email);
        transaction.setfName(firstName);
        transaction.setlName(lastName);
        transaction.setPublicKey(PUBLIC_KEY);
        transaction.setEncryptionKey(ENCRYPTION_KEY);
        transaction.setTxRef(email);
        transaction.acceptAccountPayments(false);
        transaction.acceptCardPayments(true);
        transaction.acceptMpesaPayments(false);
        transaction.acceptBankTransferPayments(false);
        transaction.acceptUssdPayments(false);
        transaction.acceptBarterPayments(false);
        transaction.onStagingEnv(false);
        transaction.shouldDisplayFee(true);
        transaction.initialize();


    }

    //Get responses of the payment made: Whether it failed, or was cancelled or was successful
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = Arrays.toString(data.getStringArrayExtra("response"));
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                verifyPayment();
                Toast.makeText(this, "SUCCESSFUL " + message, Toast.LENGTH_SHORT).show();

            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {


                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    //Function to verify payment with card. It also generate the card token with the "saveCardAndNextPaymentDate" will work on
    private void verifyPayment() {
        pd = new ProgressDialog(SignupActivity.this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        final HashMap<String, String> params = new HashMap<>();
        params.put("txref", email);
        params.put("SECKEY", SECRET_KEY);

        JsonObjectRequest verifyPaymentJsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                VERIFY_PAYMENT_ENDPOINT,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                JSONObject data = response.getJSONObject("data");
                                cardToken = response.getJSONObject("data").getJSONObject("card").getString("life_time_token");
                                saveCardAndNextPaymentDate();
                            }
                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        proceedToApp();
                        Toast.makeText(SignupActivity.this, "Check Your Internet Connection. Card not saved", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        requestQueue.add(verifyPaymentJsonObjectRequest);
    }


    //Function to save card token and save the date for next payment
    private void saveCardAndNextPaymentDate() {
        //Save the current date from an online api(So if they change the date on their phones, it doesn't affect")
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                CURRENT_DATE_ENDPOINT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int month = Integer.parseInt(response.get("monthValue").toString());
                            int day = Integer.parseInt(response.get("dayOfTheMonth").toString());
                            int year = Integer.parseInt(response.get("year").toString());

                            if (month == 12) {
                                month = 1;
                                year += 1;
                            } else {
                                month += 1;
                            }

                            String dateInString = year + "," + month + "," + day;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("cardToken", cardToken);
                            editor.putString("dateToPay", dateInString);
                            editor.apply();
                            proceedToApp();
                            Toast.makeText(SignupActivity.this, "About to register", Toast.LENGTH_SHORT).show();
                            registerToFireBase();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignupActivity.this, "Check Your Internet Connection and Try Again.", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    //function to compare dates and bill the user if subscription has expired
    @SuppressLint("NewApi")
    private void compareDates() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                CURRENT_DATE_ENDPOINT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int month = Integer.parseInt(response.get("monthValue").toString());
                            int year = Integer.parseInt(response.get("year").toString());
                            int day = Integer.parseInt(response.get("dayOfTheMonth").toString());
                            LocalDate todaysDate = LocalDate.of(year, month, day);

                            LocalDate dateToPay = extractDate(sharedPreferences.getString("dateToPay", "0,1,1"));
                            if (todaysDate.isEqual(dateToPay) || todaysDate.isAfter(dateToPay)) {
                                monthlyCharges();
                            } else {
                                proceedToApp();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @SuppressLint("NewApi")
                    private LocalDate extractDate(String date) {
                        String[] arrayOfData = date.split(",");
                        int year = Integer.parseInt(arrayOfData[0]);
                        int month = Integer.parseInt(arrayOfData[1]);
                        int day = Integer.parseInt(arrayOfData[2]);
                        return LocalDate.of(year, month, day);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    //function to charge the user on a monthly basis
    private void monthlyCharges() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("SECKEY", SECRET_KEY);
        params.put("token", sharedPreferences.getString("cardToken", "nothing yet"));
        params.put("currency", "NGN");
        params.put("email", sharedPreferences.getString("email", "nothing yet"));
        params.put("txRef", sharedPreferences.getString("email", "nothing yet"));
        params.put("amount", 1000);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                PAY_WITH_TOKEN_ENDPOINT,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equalsIgnoreCase("success")
                                    && response.getJSONObject("data").getString("chargeResponseCode").equalsIgnoreCase("00")) {
                                Toast.makeText(
                                        SignupActivity.this,
                                        "Monthly Payment Successful",
                                        Toast.LENGTH_LONG
                                ).show();
                                saveCardAndNextPaymentDate();
                                proceedToApp();
                            }else {
                                initializeRave();
                            }
                        } catch (JSONException e) {
                            initializeRave();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignupActivity.this, "Check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    //A dummy function that is called when every payment is satisfied and true
    private void proceedToApp() {
        Toast.makeText(this, "Worked", Toast.LENGTH_LONG).show();
//        String FIREBASE_DATABASE_URL = "";
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL);
//        firebaseDatabase.getReference().child("").setValue("");
        registerToFireBase();

    }


    private void registerToFireBase() {
        String str_username = username.getText().toString();
        String str_fullname = fullname.getText().toString();
        String str_email = email2.getText().toString();
        String str_password = password.getText().toString();
        String str_prof = prof.getText().toString();
        String str_reg2 = reg2.getText().toString();


        if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_reg2)) {
            Toast.makeText(SignupActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
        } else if (str_password.length() < 6) {
            Toast.makeText(SignupActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();
        } else {
            register(str_username, str_prof, str_fullname, str_email, str_password, str_reg2);
        }
    }


    private static final String[] profession = new String[]{"Artist", "Makeup Artist", "Fashion Designer","Script Writer","Model Agency", "Record Label", "Song Writer", "Dance Group", "Dancer", "Videographer", "Photographer", "Comedian", "Event Planner", "Show Promoter", "Disc Jockey(DJ)", "Video Jockey(VJ)", "Music Producer", "Model", "Vixen", "Movie Producer", "Actor", "Actress", "Talent Manager", "Sound Engineer", "OAP","Stylist", "Public Relation(PR)", "Location Manager", "Content Creator", "Blogger", "Journalist","Client"};
    private static final String[] region2 = new String[]{"Abia",
            "Adamawa",
            "Anambra",
            "Akwa Ibom",
            "Bauchi",
            "Bayelsa",
            "Benue",
            "Borno",
            "Cross River",
            "Delta",
            "Ebonyi",
            "Enugu",
            "Edo",
            "Ekiti",
            "FCT - Abuja",
            "Gombe",
            "Imo",
            "Jigawa",
            "Kaduna",
            "Kano",
            "Katsina",
            "Kebbi",
            "Kogi",
            "Kwara",
            "Lagos",
            "Nasarawa",
            "Niger",
            "Ogun",
            "Ondo",
            "Osun",
            "Oyo",
            "Plateau",
            "Rivers",
            "Sokoto",
            "Taraba",
            "Yobe",
            "Zamfara"};

    public void register(final String username, final String prof, final String fullname, String email, String password, final String region2) {
        Toast.makeText(this, "In REGISTER", Toast.LENGTH_SHORT).show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()) {
                            Toast.makeText(SignupActivity.this, "Completed Task", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            HashMap<String, Object> map = new HashMap<>();

                            map.put("id", userID);
                            map.put("username", username.toLowerCase());
                            map.put("profession", prof);
                            map.put("fullname", fullname);
                            map.put("status", "offline");
                            map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/kirk-30f6a.appspot.com/o/avatar-1577909_1280.png?alt=media&token=b1b3e744-b96b-48b5-9d10-588acbcbbf7b");
                            map.put("bio", "");
                            map.put("location", region2);
                            map.put("search", username.toLowerCase());
                            map.put("recent_chat", "" );

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(SignupActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

