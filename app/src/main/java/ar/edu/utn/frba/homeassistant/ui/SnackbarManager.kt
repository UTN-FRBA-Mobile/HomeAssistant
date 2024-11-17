package ar.edu.utn.frba.homeassistant.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SnackbarManager {
    val snackbarHostState = SnackbarHostState()
    private val currentMessage = mutableStateOf<String?>(null)

    fun showMessage(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            currentMessage.value = message
            val result = snackbarHostState.showSnackbar(message, actionLabel)
            if (result == androidx.compose.material3.SnackbarResult.ActionPerformed && onAction != null) {
                onAction()
            }
            currentMessage.value = null
        }
    }
}
