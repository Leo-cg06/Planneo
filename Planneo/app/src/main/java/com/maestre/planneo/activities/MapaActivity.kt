package com.maestre.planneo.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.components.BottomNavBar
import com.maestre.planneo.ui.theme.PlanneoTheme
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.maestre.planneo.model.LugarMapa
import com.maestre.planneo.viewmodel.OverpassViewModel

class MapaActivity : ComponentActivity(), MapEventsReceiver {
    private val MULTIPLE_PERMISSIONS_REQUEST_CODE: Int = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissionState()

        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge()
        setContent {
            PlanneoTheme {
                val context = LocalContext.current

                val overpassViewModel: OverpassViewModel = viewModel()

                val myMapView = remember { MapView(context) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(currentScreen = "mapa", context = context)
                    }
                ) { innerPadding ->
                    MapaContent(
                        overpassViewModel = overpassViewModel,
                        mapView = myMapView,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkPermissionState() {
        val fineLocationPermissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (fineLocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MULTIPLE_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode == MULTIPLE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                val somePermissionWasDenied = grantResults.any { it == PackageManager.PERMISSION_DENIED }
                if (somePermissionWasDenied) {
                    Toast.makeText(
                        this,
                        "Can´t load maps without all permissions granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Can´t load maps without all permissions granted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun MapaContent(
        overpassViewModel: OverpassViewModel,
        mapView: MapView,
        modifier: Modifier = Modifier
    ) {
        var searchQuery by remember { mutableStateOf("") }
        var categoriasSeleccionadas by remember { mutableStateOf(setOf<String>()) }
        var isSearchFocused by remember { mutableStateOf(false) }

        val opciones = listOf("Restaurantes", "Parques", "Museos", "Ocio", "Naturaleza", "Deporte", "Fiesta", "Familia")
        val keyboardController = LocalSoftwareKeyboardController.current

        val lugaresEncontrados by overpassViewModel.lugaresMapa.collectAsState()
        val estaCargando by overpassViewModel.cargandoMapa.collectAsState()

        val ejecutarBusqueda = {
            val boundingBox = mapView.boundingBox
            overpassViewModel.buscarEnOverpass(
                textoBuscado = searchQuery,
                categoriasSeleccionadas = categoriasSeleccionadas,
                sur = boundingBox.latSouth,
                oeste = boundingBox.lonWest,
                norte = boundingBox.latNorth,
                este = boundingBox.lonEast
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> isSearchFocused = focusState.isFocused },
                placeholder = { Text("Buscar lugares...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty() || categoriasSeleccionadas.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            categoriasSeleccionadas = emptySet()
                            // Al limpiar también borramos los resultados actuales
                            overpassViewModel.buscarEnOverpass("", emptySet(), 0.0, 0.0, 0.0, 0.0)
                        }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    ejecutarBusqueda()
                })
            )

            AnimatedVisibility(visible = isSearchFocused || categoriasSeleccionadas.isNotEmpty() || searchQuery.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    opciones.forEach { opcion ->
                        val isSelected = categoriasSeleccionadas.contains(opcion)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                categoriasSeleccionadas = if (isSelected) {
                                    categoriasSeleccionadas - opcion
                                } else {
                                    categoriasSeleccionadas + opcion
                                }
                            },
                            label = { Text(opcion) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = "Seleccionado") }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            if (estaCargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OsmMap(lugares = lugaresEncontrados, mapView = mapView)
            }
        }
    }

    @Composable
    fun OsmMap(
        lugares: List<LugarMapa>,
        mapView: MapView
    ) {
        AndroidView(
            factory = {
                mapView.apply {
                    setupMap(this)
                    myLocation(this)
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { map ->
            map.overlays.removeAll { it is Marker }

            lugares.forEach { lugar ->
                val marker = Marker(map)
                marker.position = GeoPoint(lugar.latitud, lugar.longitud)
                marker.title = lugar.nombre
                marker.subDescription = "Categoría: ${lugar.categoria}"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                map.overlays.add(marker)
            }

            map.invalidate()
        }
    }

    private fun setupMap(map: MapView) {
        map.setClickable(true)
        map.setDestroyMode(false)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.getLocalVisibleRect(Rect())

        map.overlays.add(MapEventsOverlay(this))

        val dm: DisplayMetrics = this.resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(map)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 40)
        map.overlays.add(scaleBarOverlay)
    }

    private fun myLocation(map: MapView) {
        val mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        mLocationOverlay.enableMyLocation()
        mLocationOverlay.enableFollowLocation()

        mLocationOverlay.runOnFirstFix {
            runOnUiThread {
                map.controller.setCenter(mLocationOverlay.myLocation)
                map.controller.animateTo(mLocationOverlay.myLocation)
                map.controller.setZoom(18.0)
                map.invalidate()
            }
        }

        map.overlays.add(mLocationOverlay)
    }

    override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean {
        return false
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        return false
    }
}