
package com.example.nfctitan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TitanBlack = Color(0xFF090909)
private val TitanGold = Color(0xFFD4AF37)
private val TitanCard = Color(0xFF181818)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TitanApp()
        }
    }
}

@Composable
fun TitanApp() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = TitanBlack,
            surface = TitanCard,
            primary = TitanGold,
            onPrimary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        HomeScreen()
    }
}

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = TitanBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "NFC TITAN",
                color = TitanGold,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "ADVANCED NFC ANALYZER",
                color = Color.Gray,
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "مرحباً بك",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "مركز تحليل وفحص تقنيات NFC",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TitanCard
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "حالة النظام",
                        color = TitanGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "جاهز لبدء الفحص",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val buttons = listOf(
                "فحص NFC",
                "سجل الفحوصات",
                "الإعدادات"
            )

            buttons.forEachIndexed { index, label ->
                Button(
                    onClick = { selectedTab = index },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == index)
                            TitanGold else TitanCard,
                        contentColor = if (selectedTab == index)
                            Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "NFC TITAN V2 • OFFLINE",
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
