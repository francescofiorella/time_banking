package com.polito.timebanking.view.profile

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.firebase.auth.GoogleAuthProvider
import com.polito.timebanking.BuildConfig
import com.polito.timebanking.R
import com.polito.timebanking.utils.snackBar
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.viewmodels.UserViewModel

class AuthFragment : Fragment() {
    private val userModel by activityViewModels<UserViewModel>()

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signUpRequest: BeginSignInRequest

    private val signInClientForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                getSignInCredentialFromIntent(activityResult.data)
            }
        }

    private val signUpClientForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                getSignInCredentialFromIntent(activityResult.data)
            }
        }

    private fun getSignInCredentialFromIntent(data: Intent?) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            when {
                idToken != null -> {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    userModel.signInWithCredential(firebaseCredential)
                }
                else -> {
                    Log.d(
                        "Google One Tap Sign-In",
                        "No ID token!"
                    )
                    activity?.snackBar(
                        "No ID token!",
                        length = BaseTransientBottomBar.LENGTH_LONG,
                        isDismissible = true
                    )
                }
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d(
                        "Google One Tap Sign-In",
                        "Google One Tap Sign-in dialog was closed"
                    )
                    activity?.snackBar(
                        "Google One Tap Sign-in dialog was closed",
                        length = BaseTransientBottomBar.LENGTH_LONG,
                        isDismissible = true
                    )
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    Log.d(
                        "Google One Tap Sign-In",
                        "Google One Tap Sign-in encountered a network error"
                    )
                    activity?.snackBar(
                        "Google One Tap Sign-in encountered a network error",
                        length = BaseTransientBottomBar.LENGTH_LONG,
                        isDismissible = true
                    )
                }
                else -> {
                    Log.d(
                        "Google One Tap Sign-In",
                        "Couldn't get credential from result (Exception = ${e})"
                    )
                    activity?.snackBar(
                        "Couldn't get credential from result",
                        length = BaseTransientBottomBar.LENGTH_LONG,
                        isDismissible = true
                    )
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        userModel.isLoading.value = true

        userModel.isUserLogged.observe(viewLifecycleOwner) { isLogged ->
            if (isLogged) {
                userModel.getCurrentUser()
                findNavController().navigate(R.id.action_authFragment_to_skillListFragment)
            } else {
                userModel.isLoading.value = false
            }
        }

        userModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                findNavController().navigate(R.id.action_authFragment_to_skillListFragment)
            }
        }

        userModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            val loadingCPI = view.findViewById<CircularProgressIndicator>(R.id.loading_cpi)
            val authFragmentLL = view.findViewById<LinearLayout>(R.id.auth_fragment_ll)
            loadingCPI.isVisible = isLoading
            authFragmentLL.isVisible = !isLoading
        }

        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        view.findViewById<MaterialButton>(R.id.sign_in_with_email_btn).setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_emailSignInFragment)
        }

        view.findViewById<MaterialButton>(R.id.sign_in_with_google_btn).setOnClickListener {
            showSignInClient()
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            supportActionBar?.title = getString(R.string.app_name)
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        setHasOptionsMenu(false)

        return view
    }

    private fun showSignInClient() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    val signInClientIntentSenderRequest =
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    signInClientForActivityResult.launch(signInClientIntentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(
                        "Google One Tap Sign-In",
                        "Couldn't start One Tap UI (Exception = ${e})"
                    )
                }
            }
            .addOnFailureListener { e ->
                // No saved credentials found: launch the One Tap sign-up flow
                Log.d(
                    "Google One Tap Sign-In",
                    "No saved credentials found (Exception = ${e})"
                )
                showSignUpClient()
            }
    }

    private fun showSignUpClient() {
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener { result ->
                try {
                    val signUpClientIntentSenderRequest =
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    signUpClientForActivityResult.launch(signUpClientIntentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(
                        "Google One Tap Sign-In",
                        "Couldn't start One Tap UI (Exception = ${e})"
                    )
                }
            }
            .addOnFailureListener { e ->
                // No Google Accounts found: show error message
                Log.d(
                    "Google One Tap Sign-In",
                    "No Google Accounts found (Exception = ${e})"
                )
                activity?.snackBar(
                    "No Google Accounts found",
                    length = BaseTransientBottomBar.LENGTH_LONG,
                    isDismissible = true
                )
            }
    }
}