package ar.edu.utn.frba.homeassistant.ui.automations.forms.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ar.edu.utn.frba.homeassistant.R

@Composable
fun <T> MultiSelectDropdownComponent(
    options: List<T>,
    selectedOptions: Set<T>,
    onOptionSelected: (Set<T>) -> Unit,
    modifier: Modifier = Modifier,
    getOptionLabel: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }

    println(stringResource(R.string.selected_opt)+"$options")

    Box(modifier = modifier) {
        OutlinedTextField(
            value = if (selectedOptions.isEmpty()) stringResource(R.string.select_scenes) else selectedOptions.joinToString(
                ", ", transform = getOptionLabel
            ),
            onValueChange = { },
            label = { Text(stringResource(R.string.scenes_label)) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = stringResource(R.string.dropdown_menu))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                DropdownMenuItem(
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                        onOptionSelected(newSelection)
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null // Prevents event bubbling
                            )
                            Text(text = getOptionLabel(option))
                        }
                    }
                )
            }
        }
    }
}