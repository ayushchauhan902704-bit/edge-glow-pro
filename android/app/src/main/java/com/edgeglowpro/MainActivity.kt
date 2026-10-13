package com.edgeglowpro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permission - App khulte hi mangega
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }

        setContent {
            var selectedTemplate by remember { mutableStateOf(0) }

            val templates = listOf(
                "Rainbow" to listOf(Color.Magenta, Color.Yellow, Color.Cyan),
                "Neon Blue" to listOf(Color.Blue, Color.Cyan),
                "Fire" to listOf(Color.Red, Color.Yellow),
                "Ocean" to listOf(Color.Blue, Color.Green),
                "Sunset" to listOf(Color(0xFFFF6B6B), Color(0xFFFFE66D)),
                "Galaxy" to listOf(Color(0xFF9D4EDD), Color(0xFF00F5D4)),
                "Lava" to listOf(Color.Red, Color(0xFFFF8500)),
                "Ice" to listOf(Color.White, Color.Cyan),
                "Forest" to listOf(Color.Green, Color(0xFF90BE6D)),
                "Candy" to listOf(Color.Magenta, Color(0xFFF72585)),
                "Gold" to listOf(Color(0xFFFFD700), Color(0xFFFFA500)),
                "Purple" to listOf(Color(0xFF7B2CBF), Color.Magenta),
                "Aqua" to listOf(Color.Cyan, Color.Blue),
                "Rose" to listOf(Color.Red, Color.Magenta),
                "Lime" to listOf(Color.Green, Color.Yellow),
                "Storm" to listOf(Color.Gray, Color.Blue)
            )

            Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F1A)).padding(16.dp)) {
                Text("Edge Glow Pro", color = Color.White, fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(templates.size) { index ->
                        val isUnlocked = index < 8
                        Box(
                            modifier = Modifier
                               .height(110.dp)
                               .background(
                                    brush = Brush.linearGradient(templates[index].second),
                                    shape = RoundedCornerShape(12.dp)
                                )
                               .clickable {
                                    if (isUnlocked) selectedTemplate = index
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isUnlocked) {
                                Box(
                                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🔒 LOCKED", color = Color.White, fontSize = 12.sp)
                                }
                            } else {
                                if (selectedTemplate == index) {
                                    Text("✓ SELECTED", color = Color.White, fontSize = 14.sp, modifier = Modifier.background(Color.Black.copy(alpha=0.5f), RoundedCornerShape(6.dp)).padding(6.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(this@MainActivity, EdgeLightingService::class.java)
                        intent.putExtra("template", selectedTemplate)
                        startForegroundService(intent)
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9D4EDD))
                ) {
                    Text("SET EDGE LIGHT", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
