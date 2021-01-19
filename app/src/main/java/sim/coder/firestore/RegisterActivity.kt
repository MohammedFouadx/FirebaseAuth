package sim.coder.firestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    lateinit var registerName: EditText
    lateinit var registerEmail: EditText
    lateinit var registerPassword: EditText
    lateinit var registerButton:Button
    lateinit var loginActivityButton:Button
    private lateinit var auth: FirebaseAuth
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        loginActivityButton=findViewById(R.id.login_activity_button)
        loginActivityButton.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
            startActivity(intent)
        }
        registerName = findViewById(R.id.register_et_name)
        registerEmail = findViewById(R.id.register_et_email)
        registerPassword = findViewById(R.id.register_et_password)
        registerButton =findViewById(R.id.signUp_button)

        registerButton.setOnClickListener {
            registerUser()
        }



    }
    fun registerUser(){

        auth= FirebaseAuth.getInstance()
        val email =registerEmail.text.toString()
        val pass = registerPassword.text.toString()
        auth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful){
                    val intent=Intent(this,LoginActivity::class.java)
                    overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
                    startActivity(intent)
                    Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                    Log.d("failed",it.exception.toString())
                }

        }

    }

}