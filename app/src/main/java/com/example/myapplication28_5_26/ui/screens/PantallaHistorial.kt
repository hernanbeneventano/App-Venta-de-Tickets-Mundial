package com.example.myapplication28_5_26.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication28_5_26.models.DTOHistorialCompra
import com.example.myapplication28_5_26.viewmodels.AuthViewModel
import com.example.myapplication28_5_26.viewmodels.MundialViewModel
import java.text.SimpleDateFormat
import java.util.*
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    navController: NavController,
    viewModel: MundialViewModel,
    authViewModel: AuthViewModel
) {
    val userId = authViewModel.user?.uid
    var compraAEliminar by remember { mutableStateOf<DTOHistorialCompra?>(null) }

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.fetchHistorial(userId)
        }
    }

    if (compraAEliminar != null) {
        AlertDialog(
            onDismissRequest = { compraAEliminar = null },
            title = { Text("¿Cancelar Compra?") },
            text = { Text("¿Estás seguro de que deseas cancelar tu ticket para ${compraAEliminar?.equipo1} vs ${compraAEliminar?.equipo2}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        userId?.let { uid ->
                            viewModel.cancelarCompra(compraAEliminar!!.id, uid)
                        }
                        compraAEliminar = null
                    }
                ) {
                    Text("Sí, Cancelar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { compraAEliminar = null }) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Historial de Compras") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.historialCompras.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text("Aún no tienes compras realizadas", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.historialCompras) { compra ->
                    ItemHistorial(compra, onDelete = { compraAEliminar = compra })
                }
            }
        }
    }
}

@Composable
fun ItemHistorial(compra: DTOHistorialCompra, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ticket de Compra",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Cancelar",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = "$${compra.total}",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini flags en el historial
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(compra.flag1)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                
                Text(
                    text = " vs ",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(compra.flag2)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${compra.equipo1} vs ${compra.equipo2}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Estadio: ${compra.estadio}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Fecha: ${formatTimestamp(compra.fechaCompra)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Método: ${compra.metodoPago} (x${compra.cantidad})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val netDate = Date(timestamp)
        sdf.format(netDate)
    } catch (e: Exception) {
        "N/A"
    }
}
