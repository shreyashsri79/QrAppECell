package com.example.qrcodeecelltest2

import PostData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.AppTheme
import com.example.qrcodeecelltest2.ui.screens.HomeScreen
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme( dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        scan = { callback ->
                            if (isScannerInstalled) {
                                startScanning(callback) // Use the callback function
                            } else {
                                callback("")
                            }
                        }
                    )

                }
            }
        }

        installGoogleScanner()
        initVars()




    }
    var isScannerInstalled : Boolean = false
    private lateinit var scanner: GmsBarcodeScanner

    private fun installGoogleScanner() {
        val moduleInstall = ModuleInstall.getClient(this)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(this))
            .build()

        moduleInstall.installModules(moduleInstallRequest).addOnSuccessListener {
            isScannerInstalled = true
        }.addOnFailureListener {
            isScannerInstalled = false
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun initVars() {

        val options = initializeGoogleScanner()
        scanner = GmsBarcodeScanning.getClient(this, options)
    }
    private fun initializeGoogleScanner(): GmsBarcodeScannerOptions {
        return GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom().build()
    }

    private fun startScanning(onResult: (String) -> Unit) {
        scanner.startScan().addOnSuccessListener {
            val result = it.rawValue.toString()
            onResult(result) // Pass the result to the callback
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            onResult("") // Pass an empty string on failure
        }
    }

}

fun sendDataToGoogleSheet(data: String, onResult: (String) -> Unit) {
    val postData = PostData(data)  // Create the POST data object

    RetrofitClient.instance.postData(postData).enqueue(object :
        Callback<Map<String, String>> {
        override fun onResponse(
            call: Call<Map<String, String>>,
            response: Response<Map<String, String>>
        ) {
            if (response.isSuccessful) {
                val status = response.body()?.get("status") ?: "Unknown status"
                onResult("Success: $status")  // Update UI with success message
            } else {
                onResult("Failed: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
            onResult("Error: ${t.message}")
        }
    })
}

fun fetchDataFromGoogleSheet(onResult: (List<String>?, String) -> Unit) {
    RetrofitClient.instance.getData().enqueue(object : Callback<Map<String, List<String>>> {
        override fun onResponse(
            call: Call<Map<String, List<String>>>,
            response: Response<Map<String, List<String>>>
        ) {
            if (response.isSuccessful) {
                val data = response.body()?.get("data")
                if (data != null) {
                    onResult(data, "Data fetched successfully")
                } else {
                    onResult(null, "No data found")
                }
            } else {
                onResult(null, "Failed: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Map<String, List<String>>>, t: Throwable) {
            onResult(null, "Error: ${t.message}")
        }
    })
}
