package com.example.one_auth_project_1

import android.app.PendingIntent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.one_auth_project_1.ui.theme.Oneauthproject1Theme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult

@Composable
fun WebViewScreen(url: String) {

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()

                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}

class MainActivity : ComponentActivity() {
    private fun auth() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
        )

        // Create and launch siqgn-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Oneauthproject1Theme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting {
                        auth()
                    }
                }
            }
        }
    }
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        val response = res.idpResponse
        setContent {
            Oneauthproject1Theme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (res.resultCode == RESULT_OK) {
                        // Successfully signed in
                        val user = FirebaseAuth.getInstance().currentUser

                        val (token, setToken) = remember {
                            mutableStateOf<GetTokenResult?>(null)
                        }
                        LaunchedEffect("") {
                            user!!.getIdToken(false).addOnCompleteListener {
                                setToken(it.result)
                            }
                        }
                        Column {
                            Text("Authenticated as " + user!!.email)
                            if (token === null)
                                Text(text = "Wait for token to load")
                            else
                                WebViewScreen("http://localhost:5173?auth_token=${token.token}")
                        }
                    } else {
                        Greeting {
                            auth()
                        }
                        Text("Authentication failed: " + response?.error.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(authenticate: () -> Unit) {
    Button(onClick = authenticate) {
        Text(text = "Authenticate")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Oneauthproject1Theme {
        Greeting {

        }
    }
}