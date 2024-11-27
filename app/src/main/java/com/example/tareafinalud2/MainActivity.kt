package com.example.tareafinalud2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.tareafinalud2.ui.theme.TareaFinalUd2Theme

/*
 * Clase de datos para representar un contacto con nombre, número de teléfono y si es favorito.
 */
data class Contact(
    val name: String,          // Nombre del contacto
    val phoneNumber: String,   // Número de teléfono del contacto
    var isFavorite: Boolean = false // Indica si el contacto es favorito (por defecto, no lo es)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TareaFinalUd2Theme {
                App()  // Ejecuta la función composable principal
            }
        }
    }
}
/*
* Composable App principal
* @var listaContactos: lista mutable con 3 objetos iniciales de ejemplo de tipo Contact
* @var mostrarContactosFavoritos: variable mutable para mostrar solo los contactos favoritos
* @var mostrarDialogo: variable mutable para manejar la visibilidad del formulario de añadir contactos
* @var mostrarFloatingButton: variable mutable para controlar la visibilidad del FloatingActionButton
*                           (se oculta al entrar en el formulario para añadir contactos)
* @var tituloTopBar: variable mutable para cambiar el título de la barra superior
* @var estadoBotones: variable mutable para manejar si los botones de la barra inferior están habilitados
*
*
*
* */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

    // Estado mutable para manejar la lista de contactos
    var listaContactos by remember {
        mutableStateOf(mutableListOf<Contact>(
            // Inicialización con contactos predefinidos
            Contact("Juan", "123456789", isFavorite = false),
            Contact("Maria", "987654321", isFavorite = false),
            Contact("Pedro", "555666777", isFavorite = true)
        ))
    }

    // Estado mutable para mostrar solo los contactos favoritos
    var mostrarContactosFavoritos by remember { mutableStateOf(false) }

    // Estado mutable para manejar la visibilidad del formulario de añadir contactos
    var mostrarFormulario by remember { mutableStateOf(false) }

    // Estado mutable para controlar la visibilidad del FloatingActionButton
    var mostrarFloatingButton by remember { mutableStateOf(true) }

    // Estado mutable para el título de la barra superior
    var tituloTopBar by remember { mutableStateOf("Contactos") }

    // Estado mutable para manejar si los botones de la barra inferior están habilitados
    var estadoBotones by remember { mutableStateOf(true) }

    // Scaffold define la estructura general de la interfaz de usuario
    Scaffold(
        topBar = {
            TopAppBar(
                // Establece los colores de la barra superior
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                // Muestra el título de la barra superior segun la "vista"
                title = { Text(tituloTopBar) }
            )
        },
        bottomBar = {
            BottomAppBar(
                // Colores para la barra inferior
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                // Solo muestra los botones si están habilitados(desaparecen si se pulsa el boton flotante)
                if(estadoBotones){
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            mostrarContactosFavoritos = false
                            tituloTopBar = "Contactos"
                        }
                    ) {
                        Icon(Icons.Filled.Call, contentDescription = "Contactos")
                        Text(text = "Contactos")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            mostrarContactosFavoritos = true
                            tituloTopBar = "Favoritos"
                        }
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Favoritos")
                        Text(text = "Favoritos")
                    }
                }
            }
        },
        floatingActionButton = {
            // Solo muestra el botón flotante si está habilitado(desaparece si el formulario de añadir contacto)
            if(mostrarFloatingButton) {
                FloatingActionButton(onClick = {
                    mostrarFormulario = true
                    mostrarFloatingButton = false
                    tituloTopBar = "Añadir Contacto"
                    estadoBotones=false
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Contacto")
                }
            }
        }
    ) { innerPadding ->

        // Filtra la lista de contactos según si se muestran favoritos o todos
        if (mostrarContactosFavoritos) {
            Content(innerPadding, listaContactos.filter{it.isFavorite}.toMutableList())
        } else {
            Content(innerPadding, listaContactos)
        }

        /* Si se muestra el formulario costumizado, se invoca el formulario para añadir contactos, con los siguientes parametros:
           @param onDismiss (@see segundo Button("Cancelar"): funcion de orden superior que maneja los booleanos y el titulo
           @param onAddContact (@see primer Button("Añadir")): funcion de orden superior que maneja los booleanos, el titulo y añade
           un nuevo objeto de tipo Contact a la lista

         */
        if (mostrarFormulario) {
            FormularioAnadirContacto(
                innerPadding,
                onDismiss = {
                    // Cierra el formulario y restablece el estado de los botones
                    mostrarFormulario = false
                    mostrarFloatingButton = true
                    tituloTopBar = "Contactos"
                    estadoBotones=true
                },
                onAddContact = { name, phone ->
                    // Añade un nuevo contacto a la lista
                    listaContactos.add(Contact(name, phone, isFavorite = false))
                    mostrarFormulario = false
                    mostrarFloatingButton = true
                    tituloTopBar = "Contactos"
                    estadoBotones=true
                }
            )
        }
    }
}
/*
* Content es una función composable encargada de mostrar una lista de contactos.
*
* @param listaContactos: Recibe una lista mutable de objetos Contact, que se renderiza como una lista vertical.
*                        Cada contacto se muestra en un componente Card que incluye detalles como el nombre,
*                        número de teléfono y un switch para marcarlo como favorito.
*
* La lista de contactos se muestra usando LazyColumn
*
* Los cambios en los contactos, como marcar o desmarcar un favorito, son reactivamente actualizados en la interfaz de usuario,
* gracias a la @var: isFavorite mutable y el by remember para el estado de si misma
*

*/

@Composable
fun Content(innerPadding: PaddingValues, listaContactos: MutableList<Contact>) {
    // Muestra una lista vertical (LazyColumn) de contactos
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(listaContactos) { item ->
            val imgRef = R.drawable.image

            // Hacer que isFavorite sea reactivo, actualizando la interfaz en tiempo real
            var isFavorite by remember { mutableStateOf(item.isFavorite) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Row(
                    Modifier
                        .padding(10.dp)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(imgRef),
                        contentDescription = "Imagen Contacto",
                        modifier = Modifier
                            .size(30.dp)
                            .aspectRatio(1f)
                    )
                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = item.phoneNumber,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = if (isFavorite) "Favorito" else "")
                        // El switch permite que el usuario marque o desmarque un contacto como favorito.
                        // El estado se actualiza reactivamente en la interfaz y en la lista original.
                        Switch(
                            checked = isFavorite,
                            onCheckedChange = {
                                isFavorite = it
                                item.isFavorite = it  // Actualiza el valor en la lista
                            }
                        )
                    }
                }
            }
        }
    }
}
/*
* FormularioAnadirContacto: función composable utilizada para añadir un nuevo contacto a la lista.
*
* Usa funciones de orden superior para manejar eventos específicos en la interfaz de usuario:
*
* @param onDismiss: Es una función de orden superior que se ejecuta cuando el usuario decide cancelar el proceso de añadir un contacto.
*                   Restablece los valores relacionados con la visibilidad del formulario y de los botones, devolviendo la aplicación al estado anterior.
*
* @param onAddContact: Es una función de orden superior que se llama al confirmar la adición de un contacto.
*                      Recibe los datos introducidos por el usuario (nombre y teléfono), añade el contacto a la lista y actualiza la interfaz.
*

*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioAnadirContacto(innerPadding: PaddingValues, onDismiss: () -> Unit, onAddContact: (String, String) -> Unit) {
    // Variables para el nombre, teléfono y mensaje de error del formulario
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    // Estructura del formulario para añadir un contacto
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = MaterialTheme.colorScheme.primaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Añade un contacto nuevo: ", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Telefono") }
            )

            Text(error)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botones para añadir el contacto o cancelar el formulario
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                //validacion basica de datos
                val isNombreValido = nombre.isNotEmpty() // Verifica si el nombre no está vacío
                val isTelefonoValido = (
                        telefono.length == 9 || telefono.length == 4) &&
                        telefono.isNotEmpty() &&
                        telefono.all { it.isDigit() } // Valida el teléfono (número y longitud)

                if (isNombreValido && isTelefonoValido) {
                    onAddContact(nombre, telefono)  // Añade el contacto si es válido
                    error = ""
                } else {
                    error = "Revise los datos e intente de nuevo\n" +
                            "Los campos no puedes estar vacios \n" +
                            "Solo se acceptan telefonos de 4 o 9 digitos"  // Muestra mensaje de error
                }
            }) {
                Text("Añadir")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TareaFinalUd2Theme {
        App()  // Vista previa de la aplicación
    }
}
