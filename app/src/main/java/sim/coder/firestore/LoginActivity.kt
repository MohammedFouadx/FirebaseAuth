package sim.coder.firestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var registerActivityButton:Button
    private lateinit var phoneNumberButton: Button
    private lateinit var loginButton:Button
    private lateinit var loginEmail:EditText
    private lateinit var loginPassword:EditText
    private lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth=FirebaseAuth.getInstance()

        var currentUser = auth.currentUser
        if(currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }


        loginEmail=findViewById(R.id.login_et_email)
        loginPassword=findViewById(R.id.login_et_password)
        registerActivityButton=findViewById(R.id.signUp_activity_button)
        phoneNumberButton=findViewById(R.id.phoneNumber_btn)

        phoneNumberButton.setOnClickListener {
            email_layout.visibility=View.GONE
            password_layout.visibility=View.GONE
            view_view.visibility=View.VISIBLE
            phone_InputLayout.visibility=View.VISIBLE
            loginUsingPhone()

        }

        registerActivityButton.setOnClickListener {
            val intent=Intent(this,RegisterActivity::class.java)
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            startActivity(intent)
        }
        loginButton=findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            val email=loginEmail.text.toString()
            val pass = loginPassword.text.toString()
            if (email.isEmpty()){
                email_layout.visibility=View.VISIBLE
                password_layout.visibility=View.VISIBLE
                view_view.visibility=View.GONE
                phone_InputLayout?.visibility=View.GONE
                Toast.makeText(this,"Enter Email",Toast.LENGTH_LONG).show()
            }else if(pass.isEmpty()){
                Toast.makeText(this,"Enter Password",Toast.LENGTH_LONG).show()
            }
            else{
                loginusingEmail()
            }


        }



        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG","onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                var intent = Intent(applicationContext,VerifyPhoneNumber::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                startActivity(intent)
            }
        }

    }

    fun loginusingEmail(){

        val email=loginEmail.text.toString()
        val pass = loginPassword.text.toString()
        auth= FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this,"Logged Successfully",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"Error email or password",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun loginUsingPhone() {
        val mobileNumber=findViewById<EditText>(R.id.login_et_phoneNumber)
        var number=mobileNumber.text.toString().trim()

        if(!number.isEmpty()){
            number="+967"+number
            sendVerificationcode (number)
        }else{
            Toast.makeText(this,"Enter mobile number",Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendVerificationcode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}
