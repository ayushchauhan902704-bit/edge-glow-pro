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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Template(val name: String, val colors: List<Color>)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Pehli baar permission check
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }

        setContent {
            var preview by remember { mutableStateOf<Template?>(null) }
            val templates = listOf(
                Template("Rainbow", listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue)),
                Template("Neon Blue", listOf(Color(0xFF00F5FF), Color(0xFF0080FF))),
                Template("Fire", listOf(Color.Red, Color(0xFFFF6A00))),
                Template("Purple Mix", listOf(Color.Magenta, Color.Blue)),
                Template("Green", listOf(Color(0xFF00FF00), Color(0xFF00AA00))),
                Template("Gold", listOf(Color(0xFFFFD700), Color(0xFFFF8C00)))
            )

            if (preview != null) {
                // Preview Screen - Template pe click karne ke baad
                PreviewScreen(template = preview!!, onBack = { preview = null }, onSet = {
                    startEdgeService(preview!!)
                    preview = null
                })
            } else {
                // Main Screen - Bade Templates
                Column(Modifier.fillMaxSize().background(Color.Black).padding(12.dp)) {
                    Text("Edge Glow Pro", color = Color.White, fontSize = 22.sp, modifier = Modifier.padding(8.dp))
                    LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(templates) { temp ->
                            Column(Modifier.background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp)).clickable { preview = temp }.padding(10.dp)) {
                                // Bada Template Box
                                Box(Modifier.fillMaxWidth().height(120.dp).background(Brush.linearGradient(temp.colors), RoundedCornerShape(12.dp)))
                                Spacer(Modifier.height(8.dp))
                                Text(temp.name, color = Color.White, fontSize = 14.sp)
                                Spacer(Modifier.height(8.dp))
                                // Har template ke niche SET button
                                Button(onClick = { startEdgeService(temp) }, modifier = Modifier.fillMaxWidth().height(38.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                                    Text("SET", color = Color.Black, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun startEdgeService(temp: Template) {
        val intent = Intent(this, EdgeLightService::class.java)
        intent.putExtra("colors", temp.colors.map { it.value.toLong() }.toLongArray())
        startService(intent)
    }
}

@Composable
fun PreviewScreen(template: Template, onBack: () -> Unit, onSet: () -> Unit) {
    // Patli light jo pure kinare pe chalegi
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // Yahi hai patli border - 2.5dp sirf, status bar ke upar tak
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(template.colors)).padding(2.5.dp).background(Color.Black))
        Column(Modifier.align(Alignment.Center)) {
            Text(template.name, color = Color.White, fontSize = 20.sp)
            Spacer(Modifier.height(20.dp))
            Button(onClick = onSet) { Text("SET THIS EDGE") }
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("Back") }
        }
    }
}
