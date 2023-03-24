package bagus2x.sosmed.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate

@Composable
fun DatePickerDialog(
    state: DatePickerDialogState,
    onResult: (LocalDate) -> Unit,
) {
    if (state.isVisible) {
        Dialog(onDismissRequest = state::dismiss) {
            Card {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    var value by remember { mutableStateOf(state.datePickerState.currentDate) }
                    DatePicker(
                        state = state.datePickerState,
                        onChange = { value = it }
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        TextButton(onClick = state::dismiss) {
                            Text(text = "Cancel")
                        }
                        TextButton(
                            onClick = {
                                state.dismiss()
                                onResult(value)
                            }
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                }
            }
        }
    }
}

@Stable
class DatePickerDialogState(
    val datePickerState: DatePickerState,
) {
    var isVisible by mutableStateOf(false)
        private set

    fun show() {
        isVisible = true
    }

    fun dismiss() {
        isVisible = false
    }
}

@Composable
fun rememberDatePickerDialogState(
    currentDate: LocalDate = LocalDate.now(),
    startDate: LocalDate = LocalDate.now().minusYears(100),
    endDate: LocalDate = LocalDate.now().plusYears(100)
): DatePickerDialogState {
    val datePickerState = rememberDatePickerState(currentDate, startDate, endDate)

    return DatePickerDialogState(datePickerState)
}
