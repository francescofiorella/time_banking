package com.polito.timebanking

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import com.polito.timebanking.viewmodels.UserViewModel

class AuthFragment : Fragment() {
    private val userModel by activityViewModels<UserViewModel>()

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signUpRequest: BeginSignInRequest

    companion object {
        private const val REQUEST_CODE_ONE_TAP = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

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

        view.findViewById<SignInButton>(R.id.sign_in_btn).setOnClickListener {
            showSignInClient()
        }

        userModel.loggedIn.observe(viewLifecycleOwner) {
            Log.d("DEBUG", "AuthFragment - userModel.loggedIn.observe (loggedIn = ${it})")
            if (it) {
                findNavController().navigate(R.id.timeSlotListFragment)
            }
        }

        userModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Log.d("DEBUG", "AuthFragment - userModel.errorMessage.observe - $errorMessage")
            if (errorMessage != "") {
                showSnackbar(errorMessage)
                userModel.clearErrorMessage()
            }
        }

        return view
    }

    private fun showSignInClient() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQUEST_CODE_ONE_TAP,
                        null, 0, 0, 0, null
                    )
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
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQUEST_CODE_ONE_TAP,
                        null, 0, 0, 0, null
                    )
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
                showSnackbar("No Google Accounts found")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_ONE_TAP -> {
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
                            showSnackbar("No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(
                                "Google One Tap Sign-In",
                                "Google One Tap Sign-in dialog was closed"
                            )
                            showSnackbar("Google One Tap Sign-in dialog was closed")
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(
                                "Google One Tap Sign-In",
                                "Google One Tap Sign-in encountered a network error"
                            )
                            showSnackbar("Google One Tap Sign-in encountered a network error")
                        }
                        else -> {
                            Log.d(
                                "Google One Tap Sign-In",
                                "Couldn't get credential from result (Exception = ${e})"
                            )
                            showSnackbar("Couldn't get credential from result")
                        }
                    }
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar
            .make(
                requireActivity().findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
            )
            .setAction("DISMISS") {}
            .show()
    }
}