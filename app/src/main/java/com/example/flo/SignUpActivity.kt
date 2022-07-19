package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityLoginBinding
import com.example.flo.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(), SignUpView{

    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupSignUpBtn.setOnClickListener{
            signUp()
            //finish()
        }
    }

    private fun getUser() : User {
        val email : String = binding.signupIdEt.text.toString() + "@" + binding.signupDirectInputEt.text.toString()
        val pwd : String = binding.signupPasswordEt.text.toString()
        var name : String = binding.signupNameEt.text.toString()


        return User(email, pwd, name)
    }

//    private fun signUp(){
//        if(binding.signupIdEt.text.toString().isEmpty() || binding.signupDirectInputEt.text.toString().isEmpty()){
//            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if(binding.signupPasswordEt.text.toString() != binding.signupPasswordCheckEt.text.toString()){
//            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val userDB = SongDatabase.getInstance(this)!!
//        userDB.userDao().insert(getUser())
//
//        val user = userDB.userDao().getUsers()
//        Log.d("SIGNUPACT", user.toString())
//    }

    private fun signUp(){
        if(binding.signupIdEt.text.toString().isEmpty() || binding.signupDirectInputEt.text.toString().isEmpty()){
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.signupNameEt.text.toString().isEmpty()){
            Toast.makeText(this, "이름 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.signupPasswordEt.text.toString() != binding.signupPasswordCheckEt.text.toString()){
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }
/*
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)
        authService.signUp(getUser()).enqueue(object: Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("SIGNUP/SUCCESS", response.toString())
                val resp: AuthResponse = response.body()!!
                when(resp.code){
                    1000->finish()
                    2016, 2018->{
                        binding.signUpEmailErrorTv.visibility= View.VISIBLE
                        binding.signUpEmailErrorTv.text = resp.message
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("SIGNUP/FAILURE", t.message.toString())
            }

        })
        Log.d("SIGNUP", "HELLO")

 */

        val authService = AuthService()
        authService.setSignUpView(this)
        authService.signUp(getUser())
    }

    override fun onSignUpSuccess() {
        finish()
    }

    override fun onSignUpFailure() {
        TODO("Not yet implemented")
    }
}