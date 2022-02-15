package com.example.mygoalskotlin.Register.View

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.mygoalskotlin.Register.Model.RegisterDTO
import com.example.mygoalskotlin.Register.ViewModel.RegisterViewModel
import com.example.mygoalskotlin.Utils.Validator
import com.example.mygoalskotlin.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    private val validator: Validator by lazy { Validator() }
    private val registerDTO: RegisterDTO by lazy { RegisterDTO() }
    //private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(baseContext)
        firebaseAuth = FirebaseAuth.getInstance()

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtonRegister()
    }

    private fun setupButtonRegister() {

        binding.buttonCreateUser.setOnClickListener {
            registerDTO.email = binding.editTextRegisterEmail.text.toString().trim()
            registerDTO.password = binding.editTextPassword.text.toString().trim()
            registerDTO.confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

            if (validator.isValidEmail(registerDTO.email)){
                if (registerDTO.password == registerDTO.confirmPassword){
                    if (validator.isValidPassword(registerDTO.password)){
                        isValidUser(true)
                    }else{
                        Toast.makeText(this, "Senha inválida", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this, "Senhas diferentes", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Email inválido", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isValidUser(valid: Boolean){
        if (valid){
            registerViewModel.register(registerDTO)
            /*firebaseAuth?.createUserWithEmailAndPassword(registerDTO.email, registerDTO.password)
                ?.addOnCompleteListener {
                    if (it.isSuccessful){
                        onBackPressed()
                    }else{
                        Toast.makeText(this, "Não foi possível criar o usuário", Toast.LENGTH_LONG).show()
                    }
                }*/
        }
    }
}