package br.hthenrique.mygoalskotlin.UI.Login.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.hthenrique.mygoalskotlin.Model.LoginModel
import br.hthenrique.mygoalskotlin.Model.repository.RepositoryFirebase
import com.google.firebase.auth.FirebaseUser


class LoginViewModel(val application: Application) : ViewModel() {
    private var repositoryFirebase: RepositoryFirebase? = null
    private var loginUserLiveData: MutableLiveData<FirebaseUser>? = null
    private var errorMutableLiveData: MutableLiveData<String>? = null

    init {
        repositoryFirebase = RepositoryFirebase()
        loginUserLiveData = repositoryFirebase!!.getMutableLiveData()
        errorMutableLiveData = repositoryFirebase!!.getErrorMutableLiveData()
    }

    fun login(loginModel: LoginModel){
        val loginUser = repositoryFirebase?.loginExistentUser(loginModel)
        loginUser?.addOnSuccessListener {
            saveUserInSharedPrefs(it.user?.uid, loginModel)
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveUserInSharedPrefs(uid: String?, loginModel: LoginModel) {
        val sharedPreferences: SharedPreferences = application.getSharedPreferences("UserSaved", Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.apply {
            putBoolean("isUserLogin", true)
            putString("uid", uid)
            putString("email", loginModel.email)
            putString("password", loginModel.password)
            apply()
            commit()
        }
    }

    fun resetPassword(email: String){
        repositoryFirebase?.resetPasswordFirebase(email)
    }

    fun getLoginMutableLiveData(): MutableLiveData<FirebaseUser>?{
        return loginUserLiveData
    }

    fun getErrorLiveData(): MutableLiveData<String>?{
        return errorMutableLiveData
    }
}