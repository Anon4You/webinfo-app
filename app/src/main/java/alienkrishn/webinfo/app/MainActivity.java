package alienkrishn.webinfo.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String PREF_FIRST_LAUNCH = "firstLaunch";

    private EditText etDomain;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if it's the first launch
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean(PREF_FIRST_LAUNCH, true);

        if (isFirstLaunch) {
            showFirstLaunchDialog();
        }

        // Create the main layout programmatically
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(16, 16, 16, 16);
        mainLayout.setBackgroundColor(Color.BLACK); // Set background to black

        // Create the EditText for the domain input
        etDomain = new EditText(this);
        etDomain.setHint("Enter Domain (e.g., example.com)");
        etDomain.setTextColor(Color.GREEN); // Green text
        etDomain.setHintTextColor(Color.GRAY); // Gray hint text
        etDomain.setTypeface(Typeface.MONOSPACE); // Monospace font
        mainLayout.addView(etDomain);

        // Create a GridLayout for the buttons
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(3); // 3 buttons per row
        gridLayout.setRowCount(4); // 4 rows (including the last row for Exit and Contact buttons)

        // Create buttons for each functionality
        Button btnWhois = createButton("WHOIS Lookup");
        btnWhois.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performWhoisLookup();
                }
            });
        gridLayout.addView(btnWhois);

        Button btnDns = createButton("DNS Lookup");
        btnDns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performDnsLookup();
                }
            });
        gridLayout.addView(btnDns);

        Button btnHost = createButton("Host Lookup");
        btnHost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performHostLookup();
                }
            });
        gridLayout.addView(btnHost);

        Button btnTraceroute = createButton("Traceroute");
        btnTraceroute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performTraceroute();
                }
            });
        gridLayout.addView(btnTraceroute);

        Button btnReverseDns = createButton("Reverse DNS");
        btnReverseDns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performReverseDns();
                }
            });
        gridLayout.addView(btnReverseDns);

        Button btnSslInfo = createButton("SSL Info");
        btnSslInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performSslInfo();
                }
            });
        gridLayout.addView(btnSslInfo);

        Button btnHttpHeaders = createButton("HTTP Headers");
        btnHttpHeaders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performHttpHeaders();
                }
            });
        gridLayout.addView(btnHttpHeaders);

        Button btnPageLinks = createButton("Page Links");
        btnPageLinks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performPageLinks();
                }
            });
        gridLayout.addView(btnPageLinks);

        Button btnHttpStatus = createButton("HTTP Status");
        btnHttpStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performHttpStatus();
                }
            });
        gridLayout.addView(btnHttpStatus);

        // Add the GridLayout to the main layout
        mainLayout.addView(gridLayout);

        // Create a LinearLayout for the Exit and Contact buttons
        LinearLayout bottomLayout = new LinearLayout(this);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setPadding(0, 16, 0, 0);

        // Create the Exit button
        Button btnExit = createButton("Exit");
        btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Close the app
                }
            });
        bottomLayout.addView(btnExit);

        // Create the Contact button
        Button btnContact = createButton("Contact");
        btnContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the link in a browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/nullxvoid"));
                    startActivity(intent);
                }
            });
        bottomLayout.addView(btnContact);

        // Add the bottom layout to the main layout
        mainLayout.addView(bottomLayout);

        // Create a ScrollView for the results
        ScrollView scrollView = new ScrollView(this);
        tvResult = new TextView(this);
        tvResult.setText("Results will appear here");
        tvResult.setTextColor(Color.GREEN); // Green text
        tvResult.setTypeface(Typeface.MONOSPACE); // Monospace font
        tvResult.setPadding(8, 8, 8, 8);
        scrollView.addView(tvResult);
        mainLayout.addView(scrollView);

        // Set the main layout as the content view
        setContentView(mainLayout);
    }

    private void showFirstLaunchDialog() {
        // Create the dialog programmatically
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About This App");
        builder.setMessage("This app provides various network-related tools such as WHOIS lookup, DNS lookup, HTTP headers, and more. It is designed to help users gather information about domains and websites.\n\nAuthor: Alienkrishn\nGitHub: https://github.com/Anon4You");
        builder.setCancelable(false); // Prevent dismissing the dialog by tapping outside

        // Add Agree and Continue button
        builder.setPositiveButton("Agree and Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Set the flag to indicate the dialog has been shown
                    SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(PREF_FIRST_LAUNCH, false);
                    editor.apply();

                    // Dismiss the dialog
                    dialog.dismiss();
                }
            });

        // Add Exit button
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Close the app
                    finish();
                }
            });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Helper method to create buttons with consistent styling
    private Button createButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.GREEN);
        button.setBackgroundColor(Color.DKGRAY);
        button.setTypeface(Typeface.MONOSPACE);
        button.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);
        button.setLayoutParams(params);
        return button;
    }

    // WHOIS Lookup
    private void performWhoisLookup() {
        final String domain = etDomain.getText().toString().trim();
        if (domain.isEmpty()) {
            tvResult.setText("Please enter a domain.");
            return;
        }
        new Thread(new Runnable() {
                @Override
                public void run() {
                    final String result = WhoisLookup.whois(domain);
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText(result);
                            }
                        });
                }
            }).start();
    }

    // DNS Lookup
    private void performDnsLookup() {
        final String domain = etDomain.getText().toString().trim();
        if (domain.isEmpty()) {
            tvResult.setText("Please enter a domain.");
            return;
        }
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InetAddress[] addresses = InetAddress.getAllByName(domain);
                        StringBuilder result = new StringBuilder();
                        for (InetAddress address : addresses) {
                            result.append(address.getHostAddress()).append("\n");
                        }
                        final String finalResult = result.toString();
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(finalResult);
                                }
                            });
                    } catch (final UnknownHostException e) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("Error: " + e.getMessage());
                                }
                            });
                    }
                }
            }).start();
    }

    // Host Lookup
    private void performHostLookup() {
        final String domain = etDomain.getText().toString().trim();
        if (domain.isEmpty()) {
            tvResult.setText("Please enter a domain.");
            return;
        }
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InetAddress[] addresses = InetAddress.getAllByName(domain);
                        StringBuilder result = new StringBuilder();
                        for (InetAddress address : addresses) {
                            result.append(address.getHostAddress()).append("\n");
                        }
                        final String finalResult = result.toString();
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(finalResult);
                                }
                            });
                    } catch (final UnknownHostException e) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("Error: " + e.getMessage());
                                }
                            });
                    }
                }
            }).start();
    }

    // Traceroute
    private void performTraceroute() {
        final String domain = etDomain.getText().toString().trim();
        if (domain.isEmpty()) {
            tvResult.setText("Please enter a domain.");
            return;
        }
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InetAddress address = InetAddress.getByName(domain);
                        StringBuilder result = new StringBuilder();
                        result.append("Traceroute to ").append(domain).append(" (").append(address.getHostAddress()).append("):\n");
                        for (int ttl = 1; ttl <= 30; ttl++) {
                            result.append(ttl).append(": ").append(address.getHostAddress()).append("\n");
                        }
                        final String finalResult = result.toString();
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(finalResult);
                                }
                            });
                    } catch (final UnknownHostException e) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("Error: " + e.getMessage());
                                }
                            });
                    }
                }
            }).start();
    }

    // Reverse DNS
    private void performReverseDns() {
        final String ip = etDomain.getText().toString().trim();
        if (ip.isEmpty()) {
            tvResult.setText("Please enter an IP address.");
            return;
        }
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InetAddress address = InetAddress.getByName(ip);
                        final String hostname = address.getHostName();
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("Reverse DNS: " + hostname);
                                }
                            });
                    } catch (final UnknownHostException e) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("Error: " + e.getMessage());
                                }
                            });
                    }
                }
            }).start();
    }

    // SSL Info
    private void performSslInfo() {
        final String domain = etDomain.getText().toString().trim();
        if (domain.isEmpty()) {
            tvResult.setText("Please enter a domain.");
            return;
        }
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                        SSLSocket socket = (SSLSocket) factory.createSocket(domain, 443);
                        socket.startHandshake();
                        SSLSession session = socket.getSession();
                        StringBuilder result = new StringBuilder();
                        result.append("SSL Info for ").append(domain).append(":\n");
                        result.append("Protocol: ").append(session.getProtocol()).append("\n");
                        result.append("Cipher Suite: ").append(session.getCipherSuite()).append("\n");
                        result.append("Peer Host: ").append(session.getPeerHost()).append("\n");
                        result.append("Peer Port: ").append(session.getPeerPort()).append("\n");
                        final String finalResult = result.toString();
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(finalResult);
                                }
                            });
                    } catch (final IOException e) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("Error: " + e.getMessage());
                                }
                            });
                    }
                }
            }).start();
    }

    // HTTP Headers
    private void performHttpHeaders() {
        final String url = etDomain.getText().toString().trim();
        if (url.isEmpty()) {
            tvResult.setText("Please enter a URL.");
            return;
        }
        final String fullUrl = addProtocolIfMissing(url); // Add protocol if missing
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL urlObj = new URL(fullUrl);
                        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();
                        StringBuilder result = new StringBuilder();
                        for (String header : connection.getHeaderFields().keySet()) {
                            result.append(header).append(": ").append(connection.getHeaderField(header)).append("\n");
                        }
                        final String finalResult = result.toString();
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(finalResult);
                                }
                            });
                    } catch (final IOException e) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("Error: " + e.getMessage());
                                }
                            });
                    }
                }
            }).start();
    }

    // Page Links
    private void performPageLinks() {
        final String url = etDomain.getText().toString().trim();
        if (url.isEmpty()) {
            tvResult.setText("Please enter a URL.");
            return;
        }
        final String fullUrl = addProtocolIfMissing(url); // Add protocol if missing
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL urlObj = new URL(fullUrl);
                        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder html = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            html.append(line);
                        }
                        String regex = "<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(html.toString());
                        StringBuilder result = new StringBuilder();
                        while (matcher.find()) {
                            result.append(matcher.group(2)).append("\n");
                        }
                        final String finalResult = result.toString();
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(finalResult);
                                }
                            });
                    } catch (final IOException e) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("Error: " + e.getMessage());
                                }
                            });
                    }
                }
            }).start();
    }

    // HTTP Status
    private void performHttpStatus() {
        final String url = etDomain.getText().toString().trim();
        if (url.isEmpty()) {
            tvResult.setText("Please enter a URL.");
            return;
        }
        final String fullUrl = addProtocolIfMissing(url); // Add protocol if missing
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL urlObj = new URL(fullUrl);
                        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();
                        final int statusCode = connection.getResponseCode();
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("HTTP Status Code: " + statusCode);
                                }
                            });
                    } catch (final IOException e) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("Error: " + e.getMessage());
                                }
                            });
                    }
                }
            }).start();
    }

    // Helper method to add protocol if missing
    private String addProtocolIfMissing(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "https://" + url; // Default to HTTPS
        }
        return url;
    }
}

// WhoisLookup class
class WhoisLookup {
    public static String whois(String domain) {
        StringBuilder result = new StringBuilder();
        Socket socket = null;
        BufferedReader reader = null;
        OutputStream outputStream = null;

        try {
            // Connect to a WHOIS server (e.g., whois.internic.net)
            socket = new Socket("whois.internic.net", 43);
            outputStream = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the domain name to the WHOIS server
            outputStream.write((domain + "\r\n").getBytes());
            outputStream.flush();

            // Read the response from the WHOIS server
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (Exception e) {
            // Handle errors (e.g., network issues)
            result.append("Error: ").append(e.getMessage());
        } finally {
            // Close resources
            try {
                if (reader != null) reader.close();
                if (outputStream != null) outputStream.close();
                if (socket != null) socket.close();
            } catch (Exception e) {
                result.append("Error closing resources: ").append(e.getMessage());
            }
        }

        return result.toString();
    }
}
