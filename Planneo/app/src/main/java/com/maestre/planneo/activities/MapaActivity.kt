package com.maestre.planneo.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.maestre.planneo.R
import com.maestre.planneo.components.BottomNavBar
import com.maestre.planneo.ui.theme.PlanneoTheme
import com.maestre.planneo.viewmodel.MainViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MinimapOverlay
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapaActivity : ComponentActivity(), MapEventsReceiver {
    private val MULTIPLE_PERMISSIONS_REQUEST_CODE: Int = 4

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissionState()

        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge()
        setContent {
            PlanneoTheme {
                val mainViewModel: MainViewModel = viewModel()
                val context = LocalContext.current

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(currentScreen = "mapa", context = context)
                    }
                ) { innerPadding ->
                    MapaContent(
                        viewModel = mainViewModel,
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
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
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

        when (requestCode) {
            MULTIPLE_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    var somePermissionWasDenied = false
                    for (result in grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) {
                            somePermissionWasDenied = true
                        }
                    }
                    if (somePermissionWasDenied) {
                        Toast.makeText(
                            this,
                            "Can´t load maps without al the permissions granted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Can´t load maps without al the permissions granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    @Composable
    fun MapaContent(
        viewModel: MainViewModel,
        modifier: Modifier = Modifier
    ) {
        var searchQuery by remember { mutableStateOf("") }

        Column(modifier = modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar lugares por nombre o tipo") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = null)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                OsmMap()
            }
        }
    }

    @Composable
    fun OsmMap() {
        val context = LocalContext.current

        mapView = remember {
            MapView(this)
        }

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        setupMap()
        createMarkers()
        myLocation()
        mapView.invalidate()
    }

    private fun setupMap() {
        mapView.setClickable(true)
        mapView.setDestroyMode(false)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.getLocalVisibleRect(Rect())

        mapView.overlays.add(MapEventsOverlay(this))

        val dm: DisplayMetrics = this.resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        scaleBarOverlay.setCentred(true)

        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 40)
        mapView.overlays.add(scaleBarOverlay)

        val minimapOverlay = MinimapOverlay(this, mapView.tileRequestCompleteHandler)
        minimapOverlay.setWidth(dm.widthPixels / 5)
        minimapOverlay.setHeight(dm.heightPixels / 5)

        minimapOverlay.setTileSource(TileSourceFactory.OpenTopo)
        mapView.overlays.add(minimapOverlay)
    }

    private fun myLocation() {
        var mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        mLocationOverlay.enableMyLocation()

        mLocationOverlay.enableFollowLocation()

        //val icon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_location)
        //mLocationOverlay.setDirectionIcon(icon)

        mLocationOverlay.runOnFirstFix {
            runOnUiThread {
                mapView.controller.setCenter(mLocationOverlay.myLocation)
                mapView.controller.animateTo(mLocationOverlay.myLocation)
                mapView.controller.setZoom(18.0)
                mapView.invalidate()
            }
        }

        mapView.overlays.add(mLocationOverlay)
    }

    private fun createMarkers() {
        val latidudIesMaestre = 38.9908
        var longitudIesMaestre = -3.9206
        val marker = Marker(mapView)
        marker.position = GeoPoint(latidudIesMaestre, longitudIesMaestre)
        marker.title = "IES Maestre de Calatrava"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
    }

    override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean {
        val marker = Marker(mapView)
        marker.position = point;
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        val circle = Polygon(mapView)
        circle.points = Polygon.pointsAsCircle(point, 75.0)
        circle.fillPaint.color = Color.argb(50, 0, 0, 255)
        circle.fillPaint.strokeWidth = 2.0f
        mapView.overlays.add(circle)

        mapView.invalidate()
        return true
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        return false
    }
}