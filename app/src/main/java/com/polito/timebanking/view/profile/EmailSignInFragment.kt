package com.polito.timebanking.view.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.polito.timebanking.R
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.viewmodels.UserViewModel

class EmailSignInFragment : Fragment() {
    private val userModel by activityViewModels<UserViewModel>()

    private lateinit var emailET: TextInputEditText
    private lateinit var passwordET: TextInputEditText
    private lateinit var signInBtn: MaterialButton
    private lateinit var signUpBtn: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email_sign_in, container, false)

        emailET = view.findViewById(R.id.email_et)
        passwordET = view.findViewById(R.id.password_et)
        signInBtn = view.findViewById(R.id.sign_in_btn)
        signUpBtn = view.findViewById(R.id.sign_up_btn)

        userModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            Log.d(
                "EmailSignInFragment",
                "userModel.isLoggedIn.observe (isLoggedIn = ${isLoggedIn})"
            )
            if (isLoggedIn) {
                findNavController().navigate(R.id.action_emailSignInFragment_to_skillListFragment)
            }
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInBtn.setOnClickListener {
            performSignIn()
        }

        signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_emailSignInFragment_to_emailSignUpFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performSignIn() {
        var isBlank = true
        var isOk = false
        val email = emailET.text.toString()
        val password = passwordET.text.toString()

        if (email.isBlank()) {
            emailET.error = "Please enter an e-mail"
            isBlank = false
        }
        if (password.isBlank()) {
            passwordET.error = "Please enter a password"
            isBlank = false
        }

        if (isBlank) {
            isOk = true
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailET.error = "Please enter a valid e-mail"
                isOk = false
            }
            if (password.length <= 5) {
                passwordET.error = "Password should be at least 6 characters"
                isOk = false
            }
        }

        if (isOk) {
            userModel.signInWithEmailAndPassword(email, password)
        }
    }
}