
package com.example.nfctitan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
            TitanApp(this)
        }
    }
}

@Composable
fun TitanApp(activity: MainActivity) {
    val context = LocalContext.current

    val database = remember {
        DatabaseManager(context)
    }

    val settings = remember {
        SettingsManager(context)
    }

    var history by remember {
        mutableStateOf(database.getHistory())
    }

    var selectedPage by remember {
        mutableIntStateOf(0)
    }

    var scanning by remember {
        mutableStateOf(false)
    }

    var status by remember {
        mutableStateOf("مرحباً بك في NFC TITAN")
    }

    var autoSave by remember {
        mutableStateOf(settings.isAutoSaveEnabled())
    }

    var animations by remember {
        mutableStateOf(settings.areAnimationsEnabled())
    }

    var sound by remember {
        mutableStateOf(settings.isSoundEnabled())
    }

    val reader = remember(activity) {
        NfcReaderManager(
            activity,
            onScanCompleted = { result ->
                if (autoSave) {
                    database.saveScan(result)
                    history = database.getHistory()
                }

                status = "اكتمل الفحص بنجاح"
            },
            onError = { error ->
                status = error
                scanning = false
            }
        )
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            val success = ReportManager().createPdf(
                context,
                uri,
                history
            )

            status = if (success) {
                "تم إنشاء التقرير"
            } else {
                "تعذر إنشاء التقرير"
            }
        }
    }

    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                val backup = BackupManager(context).createBackup()

                context.contentResolver.openOutputStream(uri)?.use {
                    it.write(backup.toByteArray(Charsets.UTF_8))
                }

                status = "تم إنشاء النسخة الاحتياطية"
            } catch (_: Exception) {
                status = "فشل إنشاء النسخة الاحتياطية"
            }
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = TitanGold,
            background = TitanBlack,
            surface = TitanCard,
            onPrimary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = TitanBlack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                Text(
                    text = "NFC TITAN",
                    color = TitanGold,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "ADVANCED NFC ANALYZER",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(22.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TitanCard
                    )
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            text = "حالة النظام",
                            color = TitanGold,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = status,
                            color = Color.White
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = when {
                                !reader.isSupported() ->
                                    "هذا الجهاز لا يدعم NFC"

                                !reader.isEnabled() ->
                                    "NFC متوقف"

                                else ->
                                    "NFC جاهز للعمل"
                            },
                            color = if (
                                reader.isSupported() &&
                                reader.isEnabled()
                            ) TitanGold else Color.LightGray
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PageButton("الرئيسية", selectedPage == 0) {
                        selectedPage = 0
                    }

                    PageButton("الفحص", selectedPage == 1) {
                        selectedPage = 1
                    }

                    PageButton("السجل", selectedPage == 2) {
                        selectedPage = 2
                    }

                    PageButton("الإعدادات", selectedPage == 3) {
                        selectedPage = 3
                    }
                }

                Spacer(Modifier.height(20.dp))

                when (selectedPage) {
                    0 -> {
                        Text(
                            "مرحباً بك",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "مركز تحليل وفحص تقنيات NFC",
                            color = Color.LightGray
                        )

                        Spacer(Modifier.height(18.dp))

                        InfoCard(
                            "عدد الفحوصات المحفوظة",
                            history.size.toString()
                        )

                        Spacer(Modifier.height(12.dp))

                        ActionButton("بدء فحص جديد") {
                            selectedPage = 1
                        }

                        ActionButton("عرض سجل الفحوصات") {
                            selectedPage = 2
                        }
                    }

                    1 -> {
                        Text(
                            "فحص NFC",
                            color = TitanGold,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "قرّب بطاقة NFC متوافقة من ظهر الهاتف.",
                            color = Color.White
                        )

                        Spacer(Modifier.height(20.dp))

                        if (!reader.isSupported()) {
                            Text(
                                "جهازك لا يحتوي على وحدة NFC.",
                                color = Color.Red
                            )
                        } else if (!reader.isEnabled()) {
                            ActionButton("فتح إعدادات NFC") {
                                try {
                                    context.startActivity(
                                        Intent(Settings.ACTION_NFC_SETTINGS)
                                    )
                                } catch (_: Exception) {
                                    context.startActivity(
                                        Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                    )
                                }
                            }
                        } else {
                            ActionButton(
                                if (scanning) "إيقاف الفحص"
                                else "ابدأ الفحص"
                            ) {
                                if (scanning) {
                                    reader.stopScanning()
                                    scanning = false
                                    status = "تم إيقاف الفحص"
                                } else {
                                    scanning = reader.startScanning()

                                    status = if (scanning) {
                                        "قرّب البطاقة من الهاتف..."
                                    } else {
                                        "تعذر بدء الفحص"
                                    }
                                }
                            }
                        }

                        if (scanning) {
                            Spacer(Modifier.height(15.dp))

                            CircularProgressIndicator(
                                color = TitanGold
                            )

                            Text(
                                "في انتظار بطاقة NFC...",
                                color = TitanGold
                            )
                        }
                    }

                    2 -> {
                        Text(
                            "سجل الفحوصات",
                            color = TitanGold,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(12.dp))

                        if (history.isEmpty()) {
                            Text(
                                "لا توجد فحوصات محفوظة حتى الآن.",
                                color = Color.LightGray
                            )
                        } else {
                            history.forEachIndexed { index, scan ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = TitanCard
                                    )
                                ) {
                                    Column(
                                        Modifier.padding(14.dp)
                                    ) {
                                        Text(
                                            "الفحص ${index + 1}",
                                            color = TitanGold,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            "Tag ID: ${scan.tagId}",
                                            color = Color.White
                                        )

                                        Text(
                                            "NDEF: ${scan.isNdef}",
                                            color = Color.LightGray
                                        )

                                        Text(
                                            scan.technologies.joinToString(", "),
                                            color = Color.LightGray
                                        )

                                        scan.records.forEach {
                                            Text(
                                                it,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(15.dp))

                        ActionButton("مسح السجل") {
                            database.clearHistory()
                            history = emptyList()
                            status = "تم مسح السجل"
                        }
                    }

                    3 -> {
                        Text(
                            "الإعدادات",
                            color = TitanGold,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold
                        )

                        SettingItem(
                            "الحفظ التلقائي",
                            autoSave
                        ) {
                            autoSave = it
                            settings.setAutoSaveEnabled(it)
                        }

                        SettingItem(
                            "تفعيل الحركات",
                            animations
                        ) {
                            animations = it
                            settings.setAnimationsEnabled(it)
                        }

                        SettingItem(
                            "تفعيل الأصوات",
                            sound
                        ) {
                            sound = it
                            settings.setSoundEnabled(it)
                        }

                        Spacer(Modifier.height(15.dp))

                        ActionButton("تصدير تقرير PDF") {
                            pdfLauncher.launch("NFC-TITAN-Report.pdf")
                        }

                        ActionButton("إنشاء نسخة احتياطية") {
                            backupLauncher.launch("NFC-TITAN-Backup.json")
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                HorizontalDivider(color = TitanGold.copy(alpha = 0.4f))

                Spacer(Modifier.height(12.dp))

                Text(
                    "NFC TITAN V2 • OFFLINE",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun PageButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (selected) TitanGold else Color.White
        )
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TitanGold,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(13.dp)
    ) {
        Text(
            label,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TitanCard
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = Color.LightGray)
            Text(
                value,
                color = TitanGold,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.White)

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TitanGold,
                checkedTrackColor = TitanGold.copy(alpha = 0.4f)
            )
        )
    }
}
