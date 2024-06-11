package uz.techie.wheeldatepicker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames
import uz.techie.wheeldatepicker.ui.dialogs.SelectDateBottomSheet

@Composable
fun DateOfBirthScreen(viewModel: DateOfBirthViewModel) {
    val localDate by viewModel.dateOfBirth.collectAsState()
    var isVisibleSelectDateBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.aligned(Alignment.CenterVertically)
    ) {
        Text(text = localDate.toUIFormat())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { isVisibleSelectDateBottomSheet = true }) {
            Text(text = "Pick date")
        }
    }

    if (isVisibleSelectDateBottomSheet) {
        SelectDateBottomSheet(
            initLocalDate = localDate,
            onDismiss = { isVisibleSelectDateBottomSheet = false },
            onSelectDate = { viewModel.setDateOfBirth(it) }
        )
    }
}

fun LocalDate.toUIFormat(): String {
    return LocalDate.Format {
        dayOfMonth()
        chars(" ")
        monthName(MonthNames.ENGLISH_FULL)
        chars(", ")
        year()
    }.format(this)
}