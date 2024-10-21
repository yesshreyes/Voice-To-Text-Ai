package com.example.voicetotextai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicetotextai.ui.theme.ColorModelMessage
//import com.example.voicetotextai.ui.theme.ColorUserMessage
import com.example.voicetotextai.ui.theme.Purple80
import java.util.Locale

// Colors for the theme
val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1D1F33), Color(0xFF141A2F))
)

val GeminiPurple = Color(0xFF6A4DDC)
val GeminiBlue = Color(0xFF4D8DDC)
val NeonGreen = Color(0xFF39FF14)
val NeonPink = Color(0xFFFF1493)
val MessageBubbleGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF4D8DDC), Color(0xFF6A4DDC)) // Use GeminiBlue and GeminiPurple
)
val ColorUserMessage = Brush.horizontalGradient(
    colors = listOf(Color(0xFF1E90FF), Color.Black) // Use GeminiBlue and GeminiPurple
)


@Composable
fun ChatPage(modifier: Modifier = Modifier, viewModel: ChatViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient) // Apply the gradient background
    ) {
        AppHeader()
        MessageList(modifier = Modifier.weight(1f), messageList = viewModel.messageList)
        MessageInput(
            onMessageSend = {
                viewModel.sendMessage(it)
            }
        )
    }
}


@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundGradient) // Apply the gradient background
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.bg), // Replace with your background image resource
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop, // Adjust how the image is scaled
            modifier = Modifier.fillMaxSize() // Fill the entire available space
        )

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Adjust the alpha for darkness
        )

        if (messageList.isEmpty()) {
            // Display a colorful empty state with a gradient background
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGradient), // Apply the background gradient
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF6A4DDC), Color(0xFF4D8DDC)),
                                radius = 200f
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(16.dp),
                    painter = painterResource(id = R.drawable.baseline_question_answer_24),
                    contentDescription = "Question Icon",
                    tint = Color.White
                )
                Text(
                    "Ask me anything..",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6A4DDC)
                )
            }
        } else {
            // Display the message list with reverse layout for a chat-like experience
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Transparent), // Make LazyColumn background transparent
                reverseLayout = true
            ) {
                items(messageList.reversed()) {
                    MessageRow(messageModel = it)
                }
            }
        }
    }
}


@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"
    val sentiment = analyzeSentiment(messageModel.message)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        start = if (isModel) 16.dp else 70.dp,
                        end = if (isModel) 70.dp else 16.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = if (isModel) MessageBubbleGradient else ColorUserMessage
                    ) // Corrected to use `brush` parameter
//                    .shadow(5.dp, RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
            ) {
                Column {
                    SelectionContainer {
                        Text(
                            text = messageModel.message,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "Sentiment: $sentiment",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = when (sentiment) {
                            "Positive" -> NeonGreen
                            "Negative" -> NeonPink
                            else -> Color.White
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(GeminiPurple, GeminiBlue)))
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(35.dp) // Set the size of the box for the icon
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.gemini), // Replace with your icon resource
                    contentDescription = "Gemini Icon",
                    modifier = Modifier
                        .fillMaxSize() // Make the icon fill the box

                )
            }
            Text(
                " Gemini",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Enhancements to the MessageInput with modern styling and gradient icons
@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showToast by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val recognizedSpeech = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            message = recognizedSpeech?.get(0) ?: "No speech detected."

            val sentiment = analyzeSentiment(message)
            Toast.makeText(context, "Sentiment: $sentiment", Toast.LENGTH_SHORT).show()
            onMessageSend(message)
        } else {
            showToast = true
        }
    }

    if (showToast) {
        Toast.makeText(context, "Speech recognition failed", Toast.LENGTH_SHORT).show()
        showToast = false
    }

    Row(
        modifier = Modifier
            .padding(12.dp)
            .background(BackgroundGradient)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, BackgroundGradient, RoundedCornerShape(12.dp))
                .shadow(5.dp, RoundedCornerShape(12.dp))
                .padding(8.dp),
            value = message,
            onValueChange = {
                message = it
            },
            placeholder = {
                Text("Type a message...", color = Color.Gray,)
            },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White)
        )
        IconButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Go on then, say something.")
                }
                launcher.launch(intent)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_mic_24),
                contentDescription = "Mic",
                tint = Color(0xFF4D8DDC),
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black, shape = CircleShape)
                    .padding(6.dp)
            )
        }
        IconButton(onClick = {
            if (message.isNotEmpty()) {
                onMessageSend(message)
                message = ""
            }
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color(0xFF6A4DDC),
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black, shape = CircleShape)
                    .padding(6.dp)
            )
        }
    }
}

fun analyzeSentiment(text: String): String {
    // Simple keyword-based sentiment analysis logic
    return when {
        text.contains("love", ignoreCase = true) || text.contains("amazing", ignoreCase = true) || text.contains("happy", ignoreCase = true) -> "Positive"
        text.contains("hate", ignoreCase = true) || text.contains("terrible", ignoreCase = true) || text.contains("sad", ignoreCase = true) -> "Negative"
        else -> "Neutral"
    }
}


@Preview(showBackground = true, name = "Chat Page Preview")
@Composable
fun ChatPagePreview() {
    // Mock data for testing the UI preview
    val mockMessages = listOf(
        MessageModel(message = "Hello! How can I assist you today?", role = "model"),
        MessageModel(message = "What can you do?", role = "user"),
        MessageModel(message = "I can answer your questions and assist you with AI capabilities.", role = "model")
    )

    // Mock ChatViewModel with the mock data
    val mockViewModel = ChatViewModel().apply {
        messageList.addAll(mockMessages)
    }

    // Call the ChatPage composable with the mocked data
    ChatPage(viewModel = mockViewModel)
}