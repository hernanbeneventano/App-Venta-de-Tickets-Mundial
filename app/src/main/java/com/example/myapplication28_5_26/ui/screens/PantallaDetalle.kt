package com.example.myapplication28_5_26.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.myapplication28_5_26.viewmodels.AuthViewModel
import com.example.myapplication28_5_26.viewmodels.MundialViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    navController: NavController,
    viewModel: MundialViewModel,
    authViewModel: AuthViewModel,
    partidoId: String
) {
    LaunchedEffect(partidoId) {
        viewModel.getPartidoById(partidoId)
    }

    val partido = viewModel.partidoSeleccionado
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            },
            title = { Text(text = "¡Compra Exitosa!", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Tu ticket para el partido ${partido?.equipo1} vs ${partido?.equipo2} ha sido reservado correctamente.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showPaymentSheet && partido != null) {
        ModalBottomSheet(
            onDismissRequest = { showPaymentSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            PaymentSheetContent(
                partido = partido,
                viewModel = viewModel,
                authViewModel = authViewModel,
                onSuccess = {
                    showPaymentSheet = false
                    showSuccessDialog = true
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Partido") },
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
        } else if (partido != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Grupo ${partido.grupo}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (partido.estado == "finalizado") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = partido.estado.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(partido.flag1)
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                        Text(partido.equipo1, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    if (partido.estado == "finalizado") {
                        Text(
                            text = "${partido.goles1} - ${partido.goles2}",
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text("VS", fontWeight = FontWeight.Black, fontSize = 24.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(partido.flag2)
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                        Text(partido.equipo2, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailItem(label = "Fecha y Hora:", value = formatFechaDetalle(partido.fecha))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailItem(label = "Estadio:", value = partido.estadio)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailItem(label = "Precio de entrada:", value = "$${partido.precio}")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { showPaymentSheet = true },
                    enabled = partido.estado != "finalizado",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    val buttonText = if (partido.estado == "finalizado") "Venta Cerrada" else "Comprar Tickets"
                    Text(buttonText, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun PaymentSheetContent(
    partido: com.example.myapplication28_5_26.models.DTOPartidosDetalle,
    viewModel: MundialViewModel,
    authViewModel: AuthViewModel,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var metodoSeleccionado by remember { mutableStateOf("Tarjeta de Crédito") }
    var numeroTarjeta by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var nombreTitular by remember { mutableStateOf("") }
    var cvuAlias by remember { mutableStateOf("") }
    var cantidadEntradas by remember { mutableStateOf(1) }
    
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isKeyboardOpen = WindowInsets.ime.asPaddingValues().calculateBottomPadding() > 0.dp
    val precioUnitario = partido.precio.toDoubleOrNull() ?: 0.0
    val totalCalculado = precioUnitario * cantidadEntradas

    if (mostrarConfirmacion) {
        // PANTALLA DE REVISIÓN Y CONFIRMACIÓN ANTES DE GUARDAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Confirmar tu Compra",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailItem(label = "Partido:", value = "${partido.equipo1} vs ${partido.equipo2}")
                    DetailItem(label = "Estadio:", value = partido.estadio)
                    DetailItem(label = "Método de Pago:", value = metodoSeleccionado)
                    val detalleMascara = if (metodoSeleccionado == "Tarjeta de Crédito") {
                        "Tarjeta: **** **** **** ${numeroTarjeta.takeLast(4)}"
                    } else {
                        "CVU/Alias: $cvuAlias"
                    }
                    DetailItem(label = "Detalle:", value = detalleMascara)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailItem(label = "Cantidad:", value = "$cantidadEntradas entradas")
                    DetailItem(label = "Total a Pagar:", value = "$$totalCalculado")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        val detalle = if (metodoSeleccionado == "Tarjeta de Crédito") {
                            "Tarjeta: $numeroTarjeta | Titular: $nombreTitular"
                        } else {
                            "Billetera: $cvuAlias"
                        }
                        authViewModel.user?.uid?.let { uid ->
                            viewModel.comprarTicket(partido, uid, cantidadEntradas, metodoSeleccionado, detalle) { exito ->
                                if (exito) {
                                    onSuccess()
                                } else {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Error al procesar la compra. Verifica los datos y que no contengan emojis.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Confirmar y Registrar Compra", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { mostrarConfirmacion = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Modificar Datos / Volver", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    } else {
        // FORMULARIO DE INGRESO DE DATOS (FASE 1)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState)
                .navigationBarsPadding()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isKeyboardOpen) {
                Text(
                    text = "Finalizar Compra",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total: $$totalCalculado",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Selector de Cantidad de Entradas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cantidad de entradas:", fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = { if (cantidadEntradas > 1) cantidadEntradas-- },
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("-", fontSize = 20.sp)
                    }
                    Text(
                        text = "$cantidadEntradas",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = { if (cantidadEntradas < 10) cantidadEntradas++ },
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Tarjeta de Crédito", "Billetera Virtual").forEach { opcion ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = (opcion == metodoSeleccionado),
                                onClick = { metodoSeleccionado = opcion }
                            )
                    ) {
                        RadioButton(
                            selected = (opcion == metodoSeleccionado),
                            onClick = { metodoSeleccionado = opcion }
                        )
                        Text(text = opcion, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (metodoSeleccionado == "Tarjeta de Crédito") {
                        OutlinedTextField(
                            value = nombreTitular,
                            onValueChange = { if (it.length <= 40 && it.all { c -> c.isLetter() || c.isWhitespace() }) nombreTitular = it },
                            label = { Text("Nombre del Titular") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = numeroTarjeta,
                            onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) numeroTarjeta = it },
                            label = { Text("Número de Tarjeta (16 dígitos)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = fechaVencimiento,
                                onValueChange = { input ->
                                    val clean = input.filter { it.isDigit() }
                                    if (clean.length <= 4) {
                                        var formatted = ""
                                        if (clean.isNotEmpty()) {
                                            val firstDigit = clean[0].digitToInt()
                                            if (firstDigit > 1) {
                                                formatted = "0$firstDigit/"
                                            } else {
                                                formatted = clean.take(1)
                                                if (clean.length >= 2) {
                                                    val month = clean.substring(0, 2).toInt()
                                                    if (month in 1..12) {
                                                        formatted = "${clean.substring(0, 2)}/"
                                                        if (clean.length > 2) {
                                                            formatted += clean.substring(2)
                                                        }
                                                    } else {
                                                        formatted = clean.take(1)
                                                    }
                                                }
                                            }
                                        }
                                        fechaVencimiento = formatted
                                    }
                                },
                                label = { Text("MM/AA") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) cvv = it },
                                label = { Text("CVV") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = cvuAlias,
                            onValueChange = { if (it.length <= 30) cvuAlias = it },
                            label = { Text("CVU o Alias de la cuenta") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Se descontará el saldo de tu billetera virtual.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { mostrarConfirmacion = true },
                enabled = if (metodoSeleccionado == "Tarjeta de Crédito") {
                    // Validaciones: tarjeta 16 dígitos, fecha válida y futura, CVV 3 dígitos, titular completo
                    val esFechaValida = validarFechaVencimiento(fechaVencimiento)
                    numeroTarjeta.length == 16 && esFechaValida && cvv.length == 3 && nombreTitular.isNotBlank()
                } else {
                    cvuAlias.isNotBlank()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Continuar a Revisión", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}

fun formatFechaDetalle(fechaStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getDefault()
        }
        val date = inputFormat.parse(fechaStr)
        if (date != null) outputFormat.format(date) else fechaStr
    } catch (e: Exception) {
        fechaStr
    }
}

/**
 * Valida que la fecha de vencimiento MM/AA sea válida y no esté vencida.
 * - Formato debe ser MM/AA (ej: 12/25)
 * - Mes debe estar entre 01 y 12
 * - Año debe ser actual o futuro
 */
fun validarFechaVencimiento(fechaStr: String): Boolean {
    if (fechaStr.isBlank() || !fechaStr.contains("/")) return false
    
    return try {
        val partes = fechaStr.split("/")
        if (partes.size != 2) return false
        
        val mes = partes[0].toIntOrNull() ?: return false
        val año = partes[1].toIntOrNull() ?: return false
        
        // Validar rango de mes
        if (mes !in 1..12) return false
        
        // Obtener año y mes actual
        val calendario = java.util.Calendar.getInstance()
        val añoActual = calendario.get(java.util.Calendar.YEAR) % 100  // Últimos 2 dígitos
        val mesActual = calendario.get(java.util.Calendar.MONTH) + 1    // Calendar usa 0-11
        
        // Si el año es anterior al actual, está vencida
        if (año < añoActual) return false
        
        // Si el año es igual pero el mes es anterior, está vencida
        if (año == añoActual && mes < mesActual) return false
        
        true
    } catch (e: Exception) {
        false
    }
}

