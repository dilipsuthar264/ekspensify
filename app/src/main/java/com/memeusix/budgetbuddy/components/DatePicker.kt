package com.memeusix.budgetbuddy.components


import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.memeusix.budgetbuddy.utils.SetWindowDim
import java.time.LocalDate


@SuppressLint("NewApi")
@Composable
fun CustomDatePicker(
    datePickerState: MutableState<Pair<Boolean, Boolean>>,
    initialDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    Dialog(
        onDismissRequest = { datePickerState.value = datePickerState.value.copy(first = false) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        SetWindowDim(0.5F)
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(16.dp)
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WheelDatePicker(
                modifier = Modifier.fillMaxWidth(),
                maxDate = LocalDate.now(),
                startDate = initialDate ?: LocalDate.now(),
                textStyle = MaterialTheme.typography.bodyMedium,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    color = MaterialTheme.colorScheme.secondary,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ),
                onSnappedDate = { selectedDate = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilledButton(
                    text = "Cancel",
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    onClick = {
                        datePickerState.value = datePickerState.value.copy(
                            first = false,
                        )
                    }
                )
                FilledButton(
                    text = "Set Date",
                    textStyle = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDateSelected(selectedDate)
                        datePickerState.value = datePickerState.value.copy(
                            first = false,
                        )
                    }
                )
            }
        }
    }
}