package com.spotlightapp.spotlight_android.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.StorageReference
import com.spotlightapp.spotlight_android.util.UserState

class MainRepository {
    private var firebaseService: FirebaseService = FirebaseService.getInstance()
    var isBottomNavVisible: MutableLiveData<Boolean> = MutableLiveData()
    var isAppBarVisible: MutableLiveData<Boolean> = MutableLiveData()

    // Static data
    var staticHouseData: MutableLiveData<HashMap<String, HashMap<String, String>>> = firebaseService.getStaticHouseData()
    var panhelValues: LiveData<ArrayList<*>> = firebaseService.getPanhelValues()

    fun refreshStaticHouseData() {
        staticHouseData.value = firebaseService.getStaticHouseData().value
    }

    fun attemptLogin(email: String, password: String): LiveData<String> {
        return firebaseService.attemptLogin(email, password)
    }

    fun validateUser(): MutableLiveData<UserState> {
        return firebaseService.validateUser()
    }

    fun accountsLogout(): MutableLiveData<String> {
        isBottomNavVisible.value = false
        isAppBarVisible.value = false
        return firebaseService.firebaseLogout()
    }

    fun attemptCreateAccount(email: String, password: String, displayName: String): LiveData<String> {
        return firebaseService.attemptCreateAccount(email, password, displayName)
    }

    fun attemptEmailVerification(): LiveData<String> {
        return firebaseService.sendEmailVerification()
    }

    fun areValuesSet(): LiveData<Boolean> {
        return firebaseService.areValuesSet()
    }

    fun overwriteUserInformation(values: MutableMap<String, Any>): LiveData<String> {
        return firebaseService.overwriteUserInformation(values)
    }

    fun updateUserInformation(values: MutableMap<String, Any>): LiveData<String> {
        return firebaseService.updateUserInformation(values)
    }

    fun getSchedule(): MutableLiveData<HashMap<String, HashMap<String, String>>> {
        return firebaseService.getSchedule()
    }

    fun getDisplayName(): MutableLiveData<String> {
        return firebaseService.getDisplayName()
    }

    fun getEmail(): MutableLiveData<String> {
        return firebaseService.getEmail()
    }

    fun getUserValues(): MutableLiveData<ArrayList<String?>> {
        return firebaseService.getUserValues()
    }

    fun sendPasswordResetEmail(email: String): MutableLiveData<String> {
        return firebaseService.sendPasswordResetEmail(email)
    }

    fun changeDisplayName(name: String): MutableLiveData<String> {
        return firebaseService.changeDisplayName(name)
    }

    fun reauthenticateUser(password: String): MutableLiveData<String> {
        return firebaseService.reauthenticateUser(password)
    }

    fun updatePassword(newPassword: String): MutableLiveData<String> {
        return firebaseService.updatePassword(newPassword)
    }

    fun getStaticHouseImageReference(fileName: String): StorageReference {
        return firebaseService.getStaticHouseImageReference(fileName)
    }

    fun getScheduleData(): MutableLiveData<Triple<Long?, Boolean, String?>> {
        return firebaseService.getScheduleData()
    }

    fun updateNoteInfo(houseId: String, comments: String, valueOne: Boolean, valueTwo: Boolean, valueThree: Boolean) {
        return firebaseService.updateNote(houseId, comments, valueOne, valueTwo, valueThree)
    }

    fun performDatabaseChangesForNoteSubmission(houseIndex: String, houseId: String, comments: String, valueOne: Boolean, valueTwo: Boolean, valueThree: Boolean): MutableLiveData<String> {
        return firebaseService.submitNote(houseIndex, houseId, comments, valueOne, valueTwo, valueThree)
    }

    fun getCurrentRanking(): MutableLiveData<HashMap<String, Int>> {
        return firebaseService.getCurrentRanking()
    }

    fun getNote(houseId: String): MutableLiveData<HashMap<String, Any>> {
        return firebaseService.getNote(houseId)
    }

    fun updateRanking(updatedRanking: HashMap<String, Int>): MutableLiveData<String> {
        return firebaseService.updateRanking(updatedRanking)
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: MainRepository().also { instance = it }
                }
    }
}