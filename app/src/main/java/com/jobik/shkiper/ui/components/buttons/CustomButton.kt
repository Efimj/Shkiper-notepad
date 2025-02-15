package com.jobik.shkiper.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jobik.shkiper.ui.helpers.MultipleEventsCutter
import com.jobik.shkiper.ui.helpers.get
import com.jobik.shkiper.ui.modifiers.circularRotation
import com.jobik.shkiper.ui.theme.AppTheme

data class ButtonProperties(
    val buttonColors: ButtonColors,
    val border: BorderStroke?,
    val shape: Shape,
    val horizontalPaddings: Dp,
    val textColor: Color,
    val textStyle: TextStyle,
    val iconTint: Color,
)

@Composable
fun DefaultButtonProperties(
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = AppTheme.colors.container,
        disabledContainerColor = Color.Transparent
    ),
    border: BorderStroke? = null,
    shape: Shape = RoundedCornerShape(10.dp),
    horizontalPaddings: Dp = 0.dp,
    textColor: Color = AppTheme.colors.text,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
    iconTint: Color = AppTheme.colors.text,
): ButtonProperties {
    return ButtonProperties(
        buttonColors = buttonColors,
        border = border,
        shape = shape,
        horizontalPaddings = horizontalPaddings,
        textColor = textColor,
        textStyle = textStyle,
        iconTint = iconTint,
    )
}

enum class ButtonStyle {
    Filled,
    Outlined,
    Text,
}

@Composable
fun getButtonProperties(style: ButtonStyle, properties: ButtonProperties? = null, enabled: Boolean): ButtonProperties {
    if (properties !== null) return properties
    val style = when (style) {
        ButtonStyle.Filled -> ButtonProperties(
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colors.primary,
                disabledContainerColor = Color.Transparent
            ),
            border = null,
            shape = RoundedCornerShape(10.dp),
            horizontalPaddings = ButtonDefaults.ContentPadding.calculateLeftPadding(
                LayoutDirection.Ltr
            ),
            textColor = AppTheme.colors.onPrimary,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            iconTint = AppTheme.colors.onPrimary,
        )

        ButtonStyle.Outlined -> ButtonProperties(
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            border = BorderStroke(width = 1.dp, color = AppTheme.colors.border),
            shape = RoundedCornerShape(10.dp),
            horizontalPaddings = ButtonDefaults.ContentPadding.calculateLeftPadding(
                LayoutDirection.Ltr
            ),
            textColor = AppTheme.colors.text,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            iconTint = AppTheme.colors.text,
        )

        ButtonStyle.Text -> ButtonProperties(
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            border = null,
            shape = RoundedCornerShape(10.dp),
            horizontalPaddings = 0.dp,
            textColor = AppTheme.colors.text,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            iconTint = AppTheme.colors.text,
        )
    }

    if (!enabled) {
        return style.copy(
            textColor = AppTheme.colors.textSecondary,
            iconTint = AppTheme.colors.textSecondary,
        )
    }

    return style
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
    style: ButtonStyle = ButtonStyle.Outlined,
    properties: ButtonProperties? = null,
    content: @Composable (() -> Unit)? = null,
) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    val buttonProperties = getButtonProperties(style, properties, enabled)
    val paddingValues = PaddingValues(
        start = buttonProperties.horizontalPaddings,
        end = buttonProperties.horizontalPaddings,
        top = ButtonDefaults.ContentPadding.calculateTopPadding(),
        bottom = ButtonDefaults.ContentPadding.calculateBottomPadding(),
    )

    Button(
        modifier = modifier,
        onClick = { multipleEventsCutter.processEvent { onClick() } },
        shape = buttonProperties.shape,
        colors = buttonProperties.buttonColors,
        border = buttonProperties.border,
        elevation = null,
        enabled = enabled,
        contentPadding = paddingValues,
    ) {
        if (content != null) {
            content()
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = buttonProperties.iconTint,
                    modifier = if (loading) Modifier
                        .size(20.dp)
                        .circularRotation() else Modifier.size(20.dp)
                )
                if (text != null)
                    Spacer(Modifier.width(5.dp))
            }
            if (text != null) {
                Text(
                    text = text,
                    color = buttonProperties.textColor,
                    style = buttonProperties.textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
