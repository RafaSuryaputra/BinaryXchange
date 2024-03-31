    package org.d3if3162.aplikasi.ui.screen

    import android.content.Context
    import android.content.Intent
    import androidx.compose.material3.Text
    import androidx.compose.material3.Button
    import android.content.res.Configuration
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.PaddingValues
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Warning
    import androidx.compose.material.icons.outlined.Info
    import androidx.compose.material3.Divider
    import androidx.compose.material3.DropdownMenuItem
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.ExposedDropdownMenuBox
    import androidx.compose.material3.ExposedDropdownMenuDefaults
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.TextField
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.TopAppBarDefaults
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.saveable.rememberSaveable
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.input.ImeAction
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavHostController
    import androidx.navigation.compose.rememberNavController
    import org.d3if3162.aplikasi.R
    import org.d3if3162.aplikasi.navigation.Screen
    import org.d3if3162.aplikasi.ui.theme.AplikasiTheme
    import kotlin.math.pow

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(navController: NavHostController) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Screen.About.route)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Info ,
                                contentDescription = stringResource(R.string.tentang_aplikasi),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        ) { padding ->
            ScreenContent(Modifier.padding(padding))
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ScreenContent(modifier: Modifier) {
        var binaryInput by rememberSaveable { mutableStateOf("") }
        var binaryInputError by rememberSaveable { mutableStateOf(false) }

        var output by rememberSaveable { mutableStateOf("") }

        var selectedOption by rememberSaveable { mutableStateOf(BinaryConversionOption.DECIMAL) }
        val options = listOf("DECIMAL", "ASCII", "OCTAL")
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(options[0]) }
        var previousSelectedOption by remember { mutableStateOf(selectedOption) }

        val context = LocalContext.current


        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier.size(150.dp)
            )
            OutlinedTextField(
                value = binaryInput,
                onValueChange = { binaryInput = it },
                label = { Text(text = stringResource(id = R.string.bilangan_biner)) },
                isError = binaryInputError,
                trailingIcon = { IconPicker(binaryInputError, unit = "") },
                supportingText = { ErrorHint(binaryInputError) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = {},
                    label = { Text("Pilih Converter") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onDismissRequest = { expanded = !expanded },
                ) {
                    options.forEach { select ->
                        DropdownMenuItem(
                            text = { Text(select) },
                            onClick = {
                                selectedOptionText = select
                                expanded = false
                                previousSelectedOption = selectedOption
                                selectedOption = when (select) {
                                    "DECIMAL" -> BinaryConversionOption.DECIMAL
                                    "ASCII" -> BinaryConversionOption.ASCII
                                    "OCTAL" -> BinaryConversionOption.OCTAL
                                    else -> BinaryConversionOption.DECIMAL
                                }
                                output = ""
                            },
                        )
                    }
                }
            }
            Button(
                onClick = {
                    binaryInputError = !binaryInput.matches("^[01]+$".toRegex())
                    if (!binaryInputError) {
                        output = when (selectedOption) {
                            BinaryConversionOption.DECIMAL -> binaryToDecimal(binaryInput).toString()
                            BinaryConversionOption.ASCII -> binaryToText(binaryInput)
                            BinaryConversionOption.OCTAL -> binaryToOctal(binaryInput)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.hitung))
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (output.isNotEmpty()) {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp
                )
                Text(
                    text = when (selectedOption) {
                        BinaryConversionOption.DECIMAL -> stringResource(
                            R.string.hasil_konversi_desimal,
                            "Hasil konversi Desimal",
                            output
                        )

                        BinaryConversionOption.ASCII -> stringResource(
                            R.string.hasil_konversi_ascii,
                            "Hasil konversi ASCII",
                            output
                        )

                        BinaryConversionOption.OCTAL -> stringResource(
                            R.string.hasil_konversi_octal,
                            "Hasil konversi Octal",
                            output
                        )
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Row(
                        horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            binaryInput = ""
                            output = ""
                            expanded = false
                        },
                        modifier = Modifier.padding(top = 8.dp, end = 8.dp), // Menambahkan jarak dari atas dan sisi kanan
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        Text(text = stringResource(id = R.string.restart))
                    }

                    Spacer(modifier = Modifier.width(16.dp)) // Menambahkan jarak antara tombol

                    Button(
                        onClick = {
                            shareData(
                                context = context,
                                message = context.getString(R.string.bagikan_template, binaryInput, selectedOptionText, output)
                            )
                        },
                        modifier = Modifier.padding(top = 8.dp), // Menambahkan jarak dari atas
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.bagikan))
                    }
                }

            }
        }
    }
    enum class BinaryConversionOption {
        DECIMAL,
        ASCII,
        OCTAL
    }

    private fun shareData(context: Context, message: String){
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        if (shareIntent.resolveActivity(context.packageManager) !=null) {
            context.startActivity(shareIntent)
        }
    }

    @Composable
    fun IconPicker(isError: Boolean, unit: String) {
        if (isError) {
            Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
        } else {
            Text(text = unit)
        }
    }

    @Composable
    fun ErrorHint(isError: Boolean) {
        if (isError) {
            Text(text = stringResource(R.string.input_invalid))
        }
    }
    fun binaryToText(binaryNumber: String): String {
        val textList = mutableListOf<Char>()
        val binaryDigits = binaryNumber.replace("\\s".toRegex(), "") // Remove spaces
        // Convert each group of 8 bits to ASCII character
        for (i in 0 until binaryDigits.length step 8) {
            val subString = binaryDigits.substring(i, minOf(i + 8, binaryDigits.length))
            val decimalValue = binaryToDecimal(subString)
            if (decimalValue in 0..127) { // Ensure ASCII range
                textList.add(decimalValue.toChar())
            } else {
                // Handle invalid ASCII values
                return "Invalid ASCII value detected."
            }
        }
        return textList.joinToString(separator = "")
    }

    fun binaryToDecimal(binaryNumber: String): Int {
        var decimalNumber = 0
        // Loop through each character in the binary string starting from the rightmost
        for ((power, i) in (binaryNumber.length - 1 downTo 0).withIndex()) {
            val digit = binaryNumber[i] - '0' // Convert char to int value (0 or 1)
            decimalNumber += digit * 2.0.pow(power.toDouble()).toInt()
        }
        return decimalNumber
    }

    fun binaryToOctal(binaryNumber: String): String {
        var octalNumber = ""
        val binaryDigits = binaryNumber.replace("\\s".toRegex(), "") // Remove spaces
        // Pad the binary string to make its length a multiple of 3
        val paddedBinary = if (binaryDigits.length % 3 != 0) {
            "0".repeat(3 - binaryDigits.length % 3) + binaryDigits
        } else {
            binaryDigits
        }
        // Convert each group of 3 bits to octal digit
        for (i in 0 until paddedBinary.length step 3) {
            val subString = paddedBinary.substring(i, minOf(i + 3, paddedBinary.length))
            val decimalValue = binaryToDecimal(subString)
            octalNumber += decimalValue.toString(8)
        }
        return octalNumber
    }

    @Preview(showBackground = true)
        @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
        @Composable
        fun GreetingPreview() {
            AplikasiTheme {
                MainScreen(rememberNavController())
            }
        }
