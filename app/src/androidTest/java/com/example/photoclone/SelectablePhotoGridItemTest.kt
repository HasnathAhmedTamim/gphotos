package com.example.photoclone

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.photoclone.presentation.components.SelectablePhotoGridItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SelectablePhotoGridItemTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun selectablePhoto_showsCheckIcon_whenSelectionMode() {
        var clicked = false
        composeTestRule.setContent {
            SelectablePhotoGridItem(
                imageUrl = "",
                isSelected = false,
                isSelectionMode = true,
                onClick = { clicked = !clicked },
                onLongPress = {}
            )
        }

        // The check icon uses contentDescription "Not selected" when not selected
        composeTestRule.onNodeWithContentDescription("Not selected").assertIsDisplayed()

        // Click the item and ensure the onClick toggles the clicked flag
        composeTestRule.onNodeWithContentDescription("Photo").performClick()
        assert(clicked)
    }
}
