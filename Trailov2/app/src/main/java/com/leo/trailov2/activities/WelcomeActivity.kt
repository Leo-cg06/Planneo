package  com.leo.trailov2.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.leo.trailov2.R
import com.leo.trailov2.bd.AuthRepositoryImpl
import com.leo.trailov2.bd.SupabaseClient
import com.leo.trailov2.ui.theme.Trailov2Theme

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        SupabaseClient.init(this)
        AuthRepositoryImpl.init(this)

        // Si ya está logueado, ir directo a Actividades
        if (AuthRepositoryImpl.isAuthenticated()) {
            val intent = Intent(this, ActividadesActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContent {
            Trailov2Theme {
                WelcomeContent(
                    onComenzar = {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun WelcomeContent(onComenzar: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.trailologo),
                contentDescription = "Logo de la app",
                modifier = Modifier.size(400.dp)
            )

            Text(
                text = stringResource(R.string.bienvenida_titulo),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.bienvenida_descripcion),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onComenzar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = stringResource(R.string.comenzar),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}