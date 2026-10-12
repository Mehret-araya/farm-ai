package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgronomistMessage
import com.example.data.model.MessageSender
import com.example.ui.viewmodel.FarmViewModel
import com.example.util.AmharicStrings

@Composable
fun AgronomistChatScreen(viewModel: FarmViewModel) {
  val isAmharic by viewModel.isAmharic.collectAsState()
  val messages by viewModel.chatMessages.collectAsState()
  val isLoading by viewModel.isChatLoading.collectAsState()
  var inputQuestion by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  // Auto scroll to latest message
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val quickPrompts = if (isAmharic) {
    listOf(
      "የበቆሎ ተምች መከላከያ",
      "ለስንዴ የዩሪያ ማዳበሪያ አጠቃቀም",
      "የቡና ቅጠል ዝገት",
      "የጤፍ በመስመር መዝራት ጥቅም",
      "ተፈጥሯዊ ማዳበሪያ (ኮምፖስት)"
    )
  } else {
    listOf(
      "How to treat Fall Armyworm?",
      "Best time to apply Urea to Wheat?",
      "Preventing Coffee Leaf Rust",
      "Benefits of row planting Teff",
      "Organic compost recipe"
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // 1. Intro Banner
    Spacer(modifier = Modifier.height(4.dp))
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = if (isAmharic) "ዶ/ር አልማዝ • የእርሻ ከፍተኛ ባለሙያ" else "Dr. Almaz • Senior AI Agronomist",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
          Text(
            text = if (isAmharic) "በኢትዮጵያ ግብርና ምርምር ተቋም (EIAR) ምክሮች ላይ የተመሰረተ" else "Trained in tropical & highland agronomy and soil science",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
          )
        }
        IconButton(
          onClick = { viewModel.resetChat() },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset chat",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2. Chat messages list
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        ChatMessageBubble(
          message = msg,
          isAmharic = isAmharic,
          onSaveAdvice = {
            viewModel.saveAdviceFromChat(
              question = "Agronomist Advice",
              answer = msg.content,
              amharic = msg.contentAmharic ?: ""
            )
          }
        )
      }

      if (isLoading) {
        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              strokeWidth = 2.dp,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isAmharic) "ዶ/ር አልማዝ መልስ በማዘጋጀት ላይ ነው..." else "Dr. Almaz is preparing advice...",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }
      }
    }

    // 3. Quick Suggestion Prompts
    Spacer(modifier = Modifier.height(6.dp))
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(quickPrompts) { prompt ->
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable {
              inputQuestion = prompt
              viewModel.sendChatMessage(prompt)
              inputQuestion = ""
            }
        ) {
          Text(
            text = prompt,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 4. Input Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputQuestion,
        onValueChange = { inputQuestion = it },
        placeholder = {
          Text(
            if (isAmharic) AmharicStrings.CHAT_INPUT_HINT_AM else AmharicStrings.CHAT_INPUT_HINT_EN,
            fontSize = 13.sp
          )
        },
        modifier = Modifier
          .weight(1f)
          .testTag("chat_input_field"),
        shape = RoundedCornerShape(24.dp)
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = {
          if (inputQuestion.isNotBlank() && !isLoading) {
            val q = inputQuestion
            inputQuestion = ""
            viewModel.sendChatMessage(q)
          }
        },
        enabled = inputQuestion.isNotBlank() && !isLoading,
        modifier = Modifier
          .size(48.dp)
          .background(MaterialTheme.colorScheme.primary, CircleShape)
          .testTag("chat_send_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send question",
          tint = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun ChatMessageBubble(
  message: AgronomistMessage,
  isAmharic: Boolean,
  onSaveAdvice: () -> Unit
) {
  val isUser = message.sender == MessageSender.USER

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    if (!isUser) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Text("🌱", fontSize = 16.sp)
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Card(
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 16.dp
      ),
      colors = CardDefaults.cardColors(
        containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = if (isUser) 0.dp else 2.dp),
      modifier = Modifier.widthIn(max = 290.dp)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = message.content,
          style = MaterialTheme.typography.bodyMedium,
          color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )

        // Amharic bilingual explanation if present and user or sender included it
        if (!message.contentAmharic.isNullOrBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text(
                text = "🇪🇹 አማርኛ ማብራሪያ:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = message.contentAmharic,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        if (!isUser) {
          var isSaved by remember { mutableStateOf(false) }
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSaved) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  onSaveAdvice()
                  isSaved = true
                }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (isSaved) Icons.Default.Check else Icons.Default.BookmarkBorder,
                  contentDescription = "Save advice",
                  modifier = Modifier.size(12.dp),
                  tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isSaved) (if (isAmharic) "ተቀምጧል ✓" else "Saved ✓") else (if (isAmharic) "አስቀምጥ" else "Save Advice"),
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }
    }
  }
}
