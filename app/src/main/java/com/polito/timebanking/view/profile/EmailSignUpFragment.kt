package com.polito.timebanking.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.polito.timebanking.R
import com.polito.timebanking.models.User
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.viewmodels.UserViewModel

class EmailSignUpFragment : Fragment() {
    private val userModel by activityViewModels<UserViewModel>()

    private lateinit var fullNameET: TextInputEditText
    private lateinit var emailET: TextInputEditText
    private lateinit var passwordET: TextInputEditText
    private lateinit var confirmPasswordET: TextInputEditText
    private lateinit var signUpBtn: MaterialButton
    private lateinit var signInBtn: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email_sign_up, container, false)

        fullNameET = view.findViewById(R.id.full_name_et)
        emailET = view.findViewById(R.id.email_et)
        passwordET = view.findViewById(R.id.password_et)
        confirmPasswordET = view.findViewById(R.id.confirm_password_et)
        signUpBtn = view.findViewById(R.id.sign_up_btn)
        signInBtn = view.findViewById(R.id.sign_in_btn)

        userModel.isUserLogged.observe(viewLifecycleOwner) { isLogged ->
            if (isLogged) {
                findNavController().navigate(R.id.action_emailSignUpFragment_to_skillListFragment)
            }
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = getString(R.string.app_name)
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signUpBtn.setOnClickListener {
            performSignUp()
        }

        signInBtn.setOnClickListener {
            findNavController().navigateUp()
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

    private fun performSignUp() {
        var isBlank = true
        var isOk = false
        val fullName = fullNameET.text.toString()
        val email = emailET.text.toString()
        val password = passwordET.text.toString()
        val confirmPassword = confirmPasswordET.text.toString()

        if (fullName.isBlank()) {
            fullNameET.error = "Please enter a full name"
            isBlank = false
        }
        if (email.isBlank()) {
            emailET.error = "Please enter an e-mail"
            isBlank = false
        }
        if (password.isBlank()) {
            passwordET.error = "Please enter a password"
            isBlank = false
        }
        if (confirmPassword.isBlank()) {
            confirmPasswordET.error = "Please enter a password"
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
            if (password != confirmPassword) {
                confirmPasswordET.error = "Passwords don't match"
                isOk = false
            }
        }

        if (isOk) {
            val user = User("", fullName, email)
            userModel.signUpWithEmailAndPassword(email, password, user)
        }
    }
}