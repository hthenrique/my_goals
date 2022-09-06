package br.hthenrique.mygoalskotlin.Model.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import br.hthenrique.mygoalskotlin.Model.LoginModel
import br.hthenrique.mygoalskotlin.Model.RegisterModel
import br.hthenrique.mygoalskotlin.Model.User
import br.hthenrique.mygoalskotlin.Utils.CollectionsConstants.USERS
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RepositoryFirebase() {
    private var application: Application? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var userMutableLiveData: MutableLiveData<FirebaseUser>? = null
    private var userDetailsLiveData: MutableLiveData<br.hthenrique.mygoalskotlin.Model.User>? = null
    private var errorMutableLiveData: MutableLiveData<String>? = null

    private val user: br.hthenrique.mygoalskotlin.Model.User by lazy { br.hthenrique.mygoalskotlin.Model.User() }

    init {
        getCurrentUser()
        firebaseAuth = FirebaseAuth.getInstance()
        userMutableLiveData = MutableLiveData()
        userDetailsLiveData = MutableLiveData()
        errorMutableLiveData = MutableLiveData()
    }

    fun registerNewUser(registerModel: br.hthenrique.mygoalskotlin.Model.RegisterModel){
        firebaseAuth?.createUserWithEmailAndPassword(
            registerModel.email.toString(),
            registerModel.password.toString()
        )
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    userMutableLiveData?.value = firebaseAuth!!.currentUser
                    user.name = registerModel.name
                    user.email = registerModel.email
                    user.uid = userMutableLiveData?.value?.uid!!
                    saveUserDetailsFirebase(user)
                }else{
                    errorMutableLiveData?.value = it.exception?.message.toString()
                }
            }
    }

    fun loginExistentUser(loginModel: br.hthenrique.mygoalskotlin.Model.LoginModel): Task<AuthResult>? {
        return firebaseAuth?.signInWithEmailAndPassword(loginModel.email.toString(), loginModel.password.toString())
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    userMutableLiveData?.value = firebaseAuth!!.currentUser
                }else{
                    errorMutableLiveData?.value = it.exception?.message.toString()
                }
            }
    }

    fun saveUserDetailsFirebase(user: br.hthenrique.mygoalskotlin.Model.User?): Boolean {
        var savingDetails: Boolean = false
        FirebaseFirestore.getInstance().collection("users").document(user?.uid.toString())
            .set(user!!)
            .addOnSuccessListener {
                Log.i("Upload user success", user?.uid.toString())
                savingDetails = true
            }
            .addOnFailureListener { e ->
                Log.i("Upload user fail", e.message, e)
                errorMutableLiveData?.value = e.message
            }
        return savingDetails
    }

    fun getUserDetailsFromFirebase(uid: String?): br.hthenrique.mygoalskotlin.Model.User?{

        val docRef: DocumentReference = FirebaseFirestore.getInstance().collection(USERS).document(uid!!)
        docRef.get().addOnCompleteListener {
            if (it.isSuccessful){
                val profile: DocumentSnapshot = it.result
                user.email = profile.getString("email")
                user.name = profile.getString("name")
                user.lastUpdate = profile.getString("lastUpdate")
                user.matches = profile.get("matches", Int::class.java)
                user.goals = profile.get("goals", Int::class.java)
                user.position = profile.getString("position")
                user.uid = profile.getString("uid").toString()

                userDetailsLiveData?.value = user
            }
            if (it.isCanceled){
                errorMutableLiveData?.value = it.exception?.message
            }
        }
        return user
    }

    fun updateUserFirebase(){
        val user = Firebase.auth.currentUser

        /*user!!.updateProfile()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Log.i("Firebase current", "User has been updated")
                }
            }*/
    }

    fun getCurrentUser(): FirebaseUser? {
        userMutableLiveData?.value = Firebase.auth.currentUser
        return Firebase.auth.currentUser
    }

    fun getUserDetailsLiveData(uid: String?): MutableLiveData<br.hthenrique.mygoalskotlin.Model.User>? {
        getUserDetailsFromFirebase(uid)
        return userDetailsLiveData
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser>? {
        return userMutableLiveData
    }

    fun getErrorMutableLiveData(): MutableLiveData<String>? {
        return errorMutableLiveData
    }

}