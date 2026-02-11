package org.example.fakeshop_clients.features.profile.presentation.components

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import fakeshop_clients.composeapp.generated.resources.Res
import fakeshop_clients.composeapp.generated.resources.language
import fakeshop_clients.composeapp.generated.resources.language_english
import fakeshop_clients.composeapp.generated.resources.language_spanish
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private data class LanguageOption(
    val tag: String,
    val labelRes: StringResource
)

private val languages = listOf(
    LanguageOption("en", Res.string.language_english),
    LanguageOption("es", Res.string.language_spanish)
)

@Composable
fun LanguagePickerSection() {
    var expanded by remember { mutableStateOf(false) }

    val currentLocale = AppCompatDelegate.getApplicationLocales()
    val currentTag = if (currentLocale.isEmpty) {
        "en"
    } else {
        currentLocale.get(0)?.language ?: "en"
    }

    val currentLanguage = languages.find { it.tag == currentTag } ?: languages[0]

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = stringResource(Res.string.language),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Text(
                text = stringResource(currentLanguage.labelRes),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(stringResource(language.labelRes)) },
                        onClick = {
                            expanded = false
                            if (language.tag != currentTag) {
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags(language.tag)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
