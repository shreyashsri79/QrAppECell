package com.example.qrcodeecelltest2.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qrcodeecelltest2.R
import com.example.qrcodeecelltest2.fetchDataFromGoogleSheet
import com.example.qrcodeecelltest2.sendDataToGoogleSheet
import com.example.qrcodeecelltest2.viewmodel.HomeScreenViewModel

@Preview(showSystemUi = true) @Composable
fun HomeScreenPreview() {
    HomeScreen(scan = { " " })
}

@Preview(showSystemUi = true) @Composable
fun DisplayDataScreenPreview() {
    DisplayDataScreen(exit = {})
}

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    scan: ((String) -> Unit) -> Unit
){

    val uiState by viewModel.uiState.collectAsState()
    if(uiState.isDataScreen){
        DisplayDataScreen(
            exit = { viewModel.exitDataScreen() }
        )
    }
    else{
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.size(220.dp))
            ScanQrButton(
                scan = {
                    scan { result ->
                        viewModel.updateResult(result)
                        viewModel.showSend()
                    }
                }
            )

            Spacer(modifier = Modifier.size(100.dp))
            ScannedResult(
                confirmSend = { viewModel.expandSend() },
                contractSend = {
                    viewModel.contractSend()
                    viewModel.updateResult("Cancelled")
                },
                isSendExpanded = uiState.isSendExpanded,
                showSend = uiState.isScanned,
                result = uiState.scannedData,
                send = {
                    sendDataToGoogleSheet(uiState.scannedData){}
                    viewModel.contractSend()
                    viewModel.updateResult("Sent to Database")
                },
                showResult = { viewModel.showDataScreen() }
            )
        }
    }

}

@Composable
fun ScanQrButton(
    scan: () -> Unit,
){
    Icon(
        painter = painterResource(id = R.drawable.scan_qr_icon),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                scan()
            }

    )
}

@Composable
fun ScannedResult(
    result: String,
    confirmSend: () -> Unit,
    contractSend: () -> Unit,
    isSendExpanded: Boolean,
    showSend: Boolean,
    send: () -> Unit,
    showResult: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                ),
        ) {
            Text(
                text = if(result.length != 0) "Result" else "Open Sheet",
                style = MaterialTheme.typography.displayMedium,
            )
            Spacer(modifier = Modifier.size(30.dp))
            SendToExcelButton(
                confirmSend = confirmSend,
                isSendExpanded = isSendExpanded,
                contractSend = contractSend,
                showSend = showSend,
                send = send,
                showResult = showResult
            )
        }
        Spacer(modifier = Modifier.size(30.dp))
        Text(
            text = result,
            style = MaterialTheme.typography.headlineSmall,
        )
        }
}

@Composable
fun SendToExcelButton(
    confirmSend : () -> Unit,
    contractSend : () -> Unit,
    isSendExpanded : Boolean,
    showSend : Boolean,
    send : () -> Unit,
    showResult : () -> Unit
){
    if(showSend){
        if (isSendExpanded){
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Send to Excel",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.size(10.dp))
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(15.dp)
                        .clickable {
                            contractSend()
                        }
                )
                Spacer(modifier = Modifier.size(20.dp))
                Icon(
                    painter = painterResource(id = R.drawable.send_to_excel_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            send()
                        }
                )
            }
        }
        else{
            Icon(
                painter = painterResource(id = R.drawable.send_to_excel_icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        confirmSend()
                    }
            )
        }
    }
    else{
        Icon(
            painter = painterResource(id = R.drawable.table),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(35.dp)
                .clickable {
                    showResult()
                }
        )
    }


}

@Composable
fun DisplayDataScreen(
    exit : () -> Unit
) {
    var dataList by remember { mutableStateOf(listOf<String>()) }
    var statusMessage by remember { mutableStateOf("Fetching data...") }

    // Fetch data when the screen loads
    LaunchedEffect(Unit) {
        fetchDataFromGoogleSheet { data, message ->
            if (data != null) {
                dataList = data
                statusMessage = ""
            } else {
                statusMessage = message
            }
        }
    }
    Card(
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.primary,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.size(100.dp))
            Row {
                Spacer(modifier = Modifier.width(300.dp))
                ExitButton(
                    exit = exit
                )
            }
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(dataList) { item ->
                    OutlinedCard(
                        modifier = Modifier.padding(16.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.displaySmall,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                    }
                }
            }
        }
    }

}

@Composable
fun ExitButton(
    exit: () -> Unit
){
    Icon(
        painter = painterResource(id = R.drawable.exit),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .size(25.dp)
            .clickable {
                exit()
            }
    )
}