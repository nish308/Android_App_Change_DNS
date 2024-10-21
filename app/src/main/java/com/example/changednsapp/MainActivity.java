package com.example.changednsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerFirst, spinnerSecond;
    private Button btnchngDns, btngetCofig, btnIsConfim;
    private Map<String, UnitData> unitDataMap = new HashMap<>();
    private ArrayList<String> firstDropdownItems = new ArrayList<>();
    private ArrayList<String> secondDropdownItems = new ArrayList<>();
    private static final int SMS_PERMISSION_CODE = 101;
    private List<UnitData> unitDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerFirst = findViewById(R.id.spinnerFirst);
        spinnerSecond = findViewById(R.id.spinnerSecond);
        btnchngDns = findViewById(R.id.buttonChngDns);
        btngetCofig = findViewById(R.id.buttonQuery);
        btnIsConfim = findViewById(R.id.buttonIsConf);

        loadFirstDropdownData();

        /*btnchngDns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedUnitStr = spinnerSecond.getSelectedItem().toString();

                if (selectedUnitStr.equals("Select Unit No.")) {
                    Toast.makeText(MainActivity.this, "Please select a valid Unit No.", Toast.LENGTH_SHORT).show();
                    return;
                }

                UnitData unitData = unitDataMap.get(selectedUnitStr);

                if (unitData != null) {
                    String unitNo = unitData.getUnitNo();
                    String mobileNo = unitData.getMobileNo();
                    String changeDnsCmd = unitData.getChangeDnsCmd();

                    sendSms(mobileNo, changeDnsCmd);

                    new Handler().postDelayed(() -> {
                        insertDnsStatus(unitNo, "0");
                    }, 5000);

                    new Handler().postDelayed(() -> {
                        refreshSpinner();
                    }, 10000);

                } else {
                    Toast.makeText(MainActivity.this, "No data found for the selected unit.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        btnchngDns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = spinnerSecond.getSelectedItemPosition();

                if (selectedPosition == 0) {
                    Toast.makeText(MainActivity.this, "Please select a valid Unit No.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the adapter
                CustomSpinnerAdapter adapter = (CustomSpinnerAdapter) spinnerSecond.getAdapter();

                // Retrieve the UnitData for the selected unit
                UnitData unitData = adapter.getSelectedUnitData(selectedPosition);

                if (unitData != null) {
                    String unitNo = unitData.getUnitNo();
                    String mobileNo = unitData.getMobileNo();
                    String changeDnsCmd = unitData.getChangeDnsCmd();

                    // Proceed with sending the SMS command
                    sendSms(mobileNo, changeDnsCmd);

                    new Handler().postDelayed(() -> {
                        insertDnsStatus(unitNo, "0");
                    }, 5000);

                    new Handler().postDelayed(() -> {
                        refreshSpinner();
                    }, 6000);
                } else {
                    Toast.makeText(MainActivity.this, "No data found for the selected unit.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btngetCofig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = spinnerSecond.getSelectedItemPosition();

                if (selectedPosition == 0) {
                    Toast.makeText(MainActivity.this, "Please select a valid Unit No.", Toast.LENGTH_SHORT).show();
                    return;
                }

                CustomSpinnerAdapter adapter = (CustomSpinnerAdapter) spinnerSecond.getAdapter();

                UnitData unitData = adapter.getSelectedUnitData(selectedPosition);

                if (unitData != null) {
                    String unitNo = unitData.getUnitNo();
                    String mobileNo = unitData.getMobileNo();
                    String getConfig = unitData.getDeviceQueryCmd();

                    sendSms(mobileNo, getConfig);

                    new Handler().postDelayed(() -> {
                        refreshSpinner();
                    }, 8000);
                } else {
                    Toast.makeText(MainActivity.this, "No data found for the selected unit.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnIsConfim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = spinnerSecond.getSelectedItemPosition();

                if (selectedPosition == 0) {
                    Toast.makeText(MainActivity.this, "Please select a valid Unit No.", Toast.LENGTH_SHORT).show();
                    return;
                }

                CustomSpinnerAdapter adapter = (CustomSpinnerAdapter) spinnerSecond.getAdapter();

                UnitData unitData = adapter.getSelectedUnitData(selectedPosition);

                if (unitData != null) {
                    String unitNo = unitData.getUnitNo();

                    insertDnsStatus(unitNo, "1");

                    new Handler().postDelayed(() -> {
                        refreshSpinner();
                    }, 8000);

                } else {
                    Toast.makeText(MainActivity.this, "No data found for the selected unit.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinnerFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                String selectedItem = firstDropdownItems.get(position);
                String selectedModelIdStr = selectedItem.split(" - ")[0];
                loadSecondDropdownData(Integer.parseInt(selectedModelIdStr));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    private void loadFirstDropdownData() {
        //String url = "http://localhost/DhruvTrack.API/api/Login/ChangeDeviceDnsReadValues";
        String url = "http://192.168.1.151/DhruvTrack.API/api/Login/ChangeDeviceDnsReadValues";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray respObj = jsonResponse.getJSONArray("respobj");
                            firstDropdownItems.add("Select Device Model");

                            for (int i = 0; i < respObj.length(); i++) {
                                JSONObject jsonObject = respObj.getJSONObject(i);
                                int modelId = jsonObject.getInt("Tracking_Model_ID");
                                String modelName = jsonObject.getString("Model_Name");

                                unitDataMap.put(String.valueOf(modelId), new UnitData("Unit_No", "Mobile_No", "Change DNS Command", "Device_Query_Cmd", ""));
                                firstDropdownItems.add(modelId + " - " + modelName);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.simple_spinner_item, firstDropdownItems);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerFirst.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error loading data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Tracking_Model_Id", "0");
                params.put("Flag", "0");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void loadSecondDropdownData(int modelId) {
        String url = "http://192.168.1.151/DhruvTrack.API/api/Login/ChangeDeviceDnsReadValues";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray respObj = jsonResponse.getJSONArray("respobj");
                            unitDataMap.clear();
                            unitDataList.clear(); // Ensure to clear previous data
                            secondDropdownItems.clear(); // Clear second dropdown items
                            //secondDropdownItems.add("Select Unit No.");

                            unitDataList.add(new UnitData("Select Unit No.", "","","",""));

                            for (int i = 0; i < respObj.length(); i++) {
                                JSONObject jsonObject = respObj.getJSONObject(i);
                                String unitNo = jsonObject.getString("Unit_No");
                                String mobileNo = jsonObject.getString("Mobile_No");
                                String changeDnsCmd = jsonObject.getString("Change_DNS_Cmd");
                                String deviceQueryCmd = jsonObject.getString("Device_Query_Cmd");
                                String isConfVal = jsonObject.getString("Is_Confirmed");


                                unitDataMap.put(unitNo, new UnitData(unitNo, mobileNo, changeDnsCmd, deviceQueryCmd, isConfVal));
                                unitDataList.add(new UnitData(unitNo, mobileNo, changeDnsCmd, deviceQueryCmd, isConfVal));

                                secondDropdownItems.add(unitNo);

                            }

                            //System.out.println(unitDataList);

                            CustomSpinnerAdapter secondAdapter = new CustomSpinnerAdapter(MainActivity.this, unitDataList);
                            spinnerSecond.setAdapter(secondAdapter);

                           /* ArrayAdapter<String> secondAdapter1 = new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.simple_spinner_item, secondDropdownItems);
                            secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSecond.setAdapter(secondAdapter1);*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error loading data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Tracking_Model_Id", String.valueOf(modelId));
                params.put("Flag", "1");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void sendSms(String mobileNo, String changeDnsCmd) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mobileNo, null, changeDnsCmd, null, null);
        Toast.makeText(this, "SMS sent to " + mobileNo + " with command: " + changeDnsCmd, Toast.LENGTH_SHORT).show();
    }

    private void insertDnsStatus(String Unit_No, String Flag) {
        String url = "http://192.168.1.151/DhruvTrack.API/api/Login/InsertDeviceDnsCommandStatus";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "Command Sent!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error calling API: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Unit_No", Unit_No);
                params.put("Flag", Flag);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void refreshSpinner() {

        spinnerFirst.setSelection(0);
        spinnerSecond.setSelection(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}