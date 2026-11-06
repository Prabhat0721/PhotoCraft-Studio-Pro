package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun PhotoCraftSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search photos",
) {
    val colors = MaterialTheme.photoCraftColors
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary,
            )
        },
        leadingIcon = {
            PhotoCraftIcon(
                glyph = PhotoCraftGlyph.Search,
                tint = if (value.isNotBlank()) colors.gold else colors.textSecondary,
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                PhotoCraftIconButton(
                    onClick = { onValueChange("") },
                    contentDescription = "Clear search",
                    containerColor = Color.Transparent,
                    contentColor = colors.textSecondary,
                    bordered = false,
                ) {
                    PhotoCraftIcon(glyph = PhotoCraftGlyph.Close)
                }
            }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = RoundedCornerShape(PhotoCraftCornerRadius.md),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.card,
            unfocusedContainerColor = colors.card,
            disabledContainerColor = colors.card,
            focusedBorderColor = colors.gold,
            unfocusedBorderColor = colors.divider,
            disabledBorderColor = colors.divider,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            disabledTextColor = colors.textSecondary,
            cursorColor = colors.gold,
            focusedLeadingIconColor = colors.gold,
            unfocusedLeadingIconColor = colors.textSecondary,
            focusedTrailingIconColor = colors.textSecondary,
            unfocusedTrailingIconColor = colors.textSecondary,
        ),
    )
}
