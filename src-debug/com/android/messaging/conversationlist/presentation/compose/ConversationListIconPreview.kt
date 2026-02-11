package com.android.messaging.conversationlist.presentation.compose

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.android.messaging.R
import com.android.messaging.conversationlist.presentation.ConversationListState.Loaded.ConversationRow.Icon
import com.android.messaging.presentation.theme.AppTheme

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun DefaultIconPreview() {
    AppTheme {
        Surface {
            ConversationListIcon(
                modifier = Modifier.size(56.dp),
                icon = Icon.Default(
                    tintIndex = 5
                )
            )
        }
    }
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun InitialIconPreview() {
    AppTheme {
        Surface {
            ConversationListIcon(
                modifier = Modifier.size(56.dp),
                icon = Icon.Initials(
                    initials = "A",
                    tintIndex = 3
                )
            )
        }
    }
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun UriIconPreview() {
    AppTheme {
        Surface {
            val context: Context = LocalContext.current
            ConversationListIcon(
                modifier = Modifier.size(56.dp),
                icon = Icon.Media(
                    mediaUri = Uri.parse("android.resource://${context.packageName}/${R.drawable.graphene}")
                )
            )
        }
    }
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun DoubleIconPreview() {
    AppTheme {
        Surface {
            val context: Context = LocalContext.current
            ConversationListIcon(
                modifier = Modifier.size(56.dp),
                icon = Icon.Group(
                    icons = listOf(
                        Icon.Media(
                            mediaUri = "android.resource://${context.packageName}/${R.drawable.graphene}".toUri()
                        ),
                        Icon.Initials(
                            initials = "A",
                            tintIndex = 3
                        )
                    )
                )
            )
        }
    }
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun TripleIconPreview() {
    AppTheme {
        Surface {
            val context: Context = LocalContext.current
            ConversationListIcon(
                modifier = Modifier.size(56.dp),
                icon = Icon.Group(
                    icons = listOf(
                        Icon.Media(
                            mediaUri = "android.resource://${context.packageName}/${R.drawable.graphene}".toUri()
                        ),
                        Icon.Initials(
                            initials = "A",
                            tintIndex = 3
                        ),
                        Icon.Default(
                            tintIndex = 1
                        )
                    )
                )
            )
        }
    }
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun QuadrupleIconPreview() {
    AppTheme {
        Surface {
            val context: Context = LocalContext.current
            ConversationListIcon(
                modifier = Modifier.size(56.dp),
                icon = Icon.Group(
                    icons = listOf(
                        Icon.Media(
                            mediaUri = "android.resource://${context.packageName}/${R.drawable.graphene}".toUri()
                        ),
                        Icon.Initials(
                            initials = "A",
                            tintIndex = 3
                        ),
                        Icon.Default(
                            tintIndex = 1
                        ),
                        Icon.Initials(
                            initials = "B",
                            tintIndex = 5
                        )
                    )
                )
            )
        }
    }
}
