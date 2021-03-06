package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spotlightapp.spotlight_android.data.MainRepository

class OnboardingViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var staticPanhelValues: MutableLiveData<ArrayList<*>> = mainRepository.staticPanhelValues
    var currentlyCheckedBoxes: HashMap<Int, String>? = HashMap()
    var submittedCheckboxes: ArrayList<String>? = ArrayList()

    fun submitChosenValues(values: MutableMap<String, Any>): LiveData<String> {
        return mainRepository.updateUserInformation(values)
    }
}