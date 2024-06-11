package uz.techie.wheeldatepicker.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class DateOfBirthViewModel : ViewModel() {

    private val _dateOfBirth = MutableStateFlow(LocalDate(2001, 7, 4))
    val dateOfBirth = _dateOfBirth.asStateFlow()

    fun setDateOfBirth(date: LocalDate) {
        _dateOfBirth.value = date
    }
}