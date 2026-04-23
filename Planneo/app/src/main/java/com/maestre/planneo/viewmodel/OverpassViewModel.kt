package com.maestre.planneo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestre.planneo.model.LugarMapa
import com.maestre.planneo.model.OverpassResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class OverpassViewModel : ViewModel() {

    private val _lugaresMapa = MutableStateFlow<List<LugarMapa>>(emptyList())
    val lugaresMapa: StateFlow<List<LugarMapa>> = _lugaresMapa

    private val _cargandoMapa = MutableStateFlow(false)
    val cargandoMapa: StateFlow<Boolean> = _cargandoMapa

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val categoriasOSM = mapOf(
        "Restaurantes" to """nwr["amenity"~"restaurant|cafe|fast_food"]""",
        "Parques" to """nwr["leisure"="park"]""",
        "Museos" to """nwr["tourism"~"museum|gallery"]""",
        "Ocio" to """nwr["leisure"~"cinema|theatre|bowling_alley|amusement_arcade"]""",
        "Naturaleza" to """nwr["natural"~"wood|water|beach|peak"]""",
        "Deporte" to """nwr["leisure"~"sports_centre|pitch|stadium|swimming_pool"]""",
        "Fiesta" to """nwr["amenity"~"bar|pub|nightclub"]""",
        "Familia" to """nwr["tourism"~"theme_park|zoo|aquarium"]"""
    )

    fun buscarEnOverpass(
        textoBuscado: String,
        categoriasSeleccionadas: Set<String>,
        sur: Double, oeste: Double, norte: Double, este: Double
    ) {
        viewModelScope.launch {
            _cargandoMapa.value = true
            try {
                val consultasFiltros = categoriasSeleccionadas.mapNotNull { categoria ->
                    categoriasOSM[categoria]?.plus("($sur,$oeste,$norte,$este);")
                }.joinToString("\n")

                val consultaTexto = if (textoBuscado.isNotBlank()) {
                    // Para buscar en poligonos(areas)
                    """nwr["name"~"$textoBuscado", i]($sur,$oeste,$norte,$este);"""
                } else ""

                if (consultasFiltros.isBlank() && consultaTexto.isBlank()) {
                    _lugaresMapa.value = emptyList()
                    _cargandoMapa.value = false
                    return@launch
                }

                val query = """
                    [out:json];
                    (
                      $consultasFiltros
                      $consultaTexto
                    );
                    out center 50; // El 'center' calcula el centroide de los polígonos
                """.trimIndent()

                val response: OverpassResponse = client.get("https://overpass-api.de/api/interpreter") {
                    parameter("data", query)
                }.body()

                _lugaresMapa.value = response.elements.mapNotNull { element ->
                    val latFinal = element.lat ?: element.center?.lat
                    val lonFinal = element.lon ?: element.center?.lon

                    if (latFinal != null && lonFinal != null) {
                        LugarMapa(
                            id = element.id,
                            nombre = element.tags?.get("name") ?: "Lugar sin nombre",
                            categoria = element.tags?.get("amenity") ?: element.tags?.get("leisure") ?: element.tags?.get("tourism") ?: "Otro",
                            latitud = latFinal,
                            longitud = lonFinal
                        )
                    } else null
                }.filter { it.nombre != "Lugar sin nombre" }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _cargandoMapa.value = false
            }
        }
    }
}