package com.leo.trailov2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.leo.trailov2.bd.ActividadRepository
import com.leo.trailov2.bd.AuthRepositoryImpl
import com.leo.trailov2.bd.FavoritoRepository
import com.leo.trailov2.bd.ParqueRepository
import com.leo.trailov2.bd.ValoracionRepository
import com.leo.trailov2.model.ActividadConFavorito
import com.leo.trailov2.model.ParqueConFavorito
import com.leo.trailov2.model.Valoracion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Actividades
    private val _actividades = MutableStateFlow<List<ActividadConFavorito>>(emptyList())
    val actividades: StateFlow<List<ActividadConFavorito>> = _actividades

    private val _actividadesFavoritas = MutableStateFlow<List<ActividadConFavorito>>(emptyList())
    val actividadesFavoritas: StateFlow<List<ActividadConFavorito>> = _actividadesFavoritas

    // Parques
    private val _parques = MutableStateFlow<List<ParqueConFavorito>>(emptyList())
    val parques: StateFlow<List<ParqueConFavorito>> = _parques

    private val _parquesFavoritos = MutableStateFlow<List<ParqueConFavorito>>(emptyList())
    val parquesFavoritos: StateFlow<List<ParqueConFavorito>> = _parquesFavoritos

    // Valoraciones
    private val _valoraciones = MutableStateFlow<List<Valoracion>>(emptyList())
    val valoraciones:  StateFlow<List<Valoracion>> = _valoraciones

    // Estados
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private fun getCurrentUserId(): String? {
        return AuthRepositoryImpl.getUserIdActual()
    }

    // ACTIVIDADES

    fun cargarActividades() {
        viewModelScope.launch {
            _cargando.value = true
            val userId = getCurrentUserId()
            val todasActividades = ActividadRepository.getAll()

            val favoritosIds = if (userId != null) {
                FavoritoRepository.getFavoritosByUserAndTipo(userId, "actividad")
                    .map { it.itemId }
            } else {
                emptyList()
            }

            _actividades.value = todasActividades.map { actividad ->
                ActividadConFavorito(
                    actividad = actividad,
                    esFavorito = actividad.id in favoritosIds
                )
            }
            _cargando.value = false
        }
    }

    //Carga todas las actividades combinándolas con el estado de favoritos del usuario
    fun cargarActividadesFavoritas() {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            val favoritosIds = FavoritoRepository.getFavoritosByUserAndTipo(userId, "actividad")
                .map { it.itemId }

            val todasActividades = ActividadRepository.getAll()
            val favoritas = todasActividades
                .filter { it.id in favoritosIds }
                .map { ActividadConFavorito(it, true) }

            _actividadesFavoritas.value = favoritas
        }
    }

    fun buscarActividades(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _cargando.value = true
            val userId = getCurrentUserId()

            val resultado = if (query.isBlank()) {
                ActividadRepository.getAll()
            } else {
                ActividadRepository.search(query)
            }

            val favoritosIds = if (userId != null) {
                FavoritoRepository.getFavoritosByUserAndTipo(userId, "actividad")
                    .map { it.itemId }
            } else {
                emptyList()
            }

            _actividades.value = resultado.map { actividad ->
                ActividadConFavorito(
                    actividad = actividad,
                    esFavorito = actividad.id in favoritosIds
                )
            }
            _cargando.value = false
        }
    }

    //Quitar o poner favorito
    fun alternarActividadFavorito(actividadConFavorito: ActividadConFavorito) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch
            FavoritoRepository.alternarFavorito(userId, "actividad", actividadConFavorito.actividad.id)
            cargarActividades()
            cargarActividadesFavoritas()
        }
    }

    // PARQUES

    fun buscarParques() {
        viewModelScope.launch {
            _cargando.value = true
            val userId = getCurrentUserId()
            val todosParques = ParqueRepository.getAll()

            val favoritosIds = if (userId != null) {
                FavoritoRepository.getFavoritosByUserAndTipo(userId, "parque")
                    .map { it.itemId }
            } else {
                emptyList()
            }

            _parques.value = todosParques.map { parque ->
                ParqueConFavorito(
                    parque = parque,
                    esFavorito = parque.id in favoritosIds
                )
            }
            _cargando.value = false
        }
    }

    fun buscarParquesFavoritos() {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            val favoritosIds = FavoritoRepository.getFavoritosByUserAndTipo(userId, "parque")
                .map { it.itemId }

            val todosParques = ParqueRepository.getAll()
            val favoritos = todosParques
                .filter { it.id in favoritosIds }
                .map { ParqueConFavorito(it, true) }

            _parquesFavoritos.value = favoritos
        }
    }

    fun buscarParques(query:  String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _cargando.value = true
            val userId = getCurrentUserId()

            val resultado = if (query.isBlank()) {
                ParqueRepository.getAll()
            } else {
                ParqueRepository.search(query)
            }

            val favoritosIds = if (userId != null) {
                FavoritoRepository.getFavoritosByUserAndTipo(userId, "parque")
                    .map { it.itemId }
            } else {
                emptyList()
            }

            _parques.value = resultado.map { parque ->
                ParqueConFavorito(
                    parque = parque,
                    esFavorito = parque.id in favoritosIds
                )
            }
            _cargando.value = false
        }
    }

    fun alternarParqueFavorito(parqueConFavorito: ParqueConFavorito) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch
            FavoritoRepository.alternarFavorito(userId, "parque", parqueConFavorito.parque.id)
            buscarParques()
            buscarParquesFavoritos()
        }
    }

    // VALORACIONES

    fun cargarValoraciones(tipo: String, idReferencia: Int) {
        viewModelScope.launch {
            _valoraciones.value = ValoracionRepository.getByTipoAndId(tipo, idReferencia)
        }
    }

    fun insertValoracion(valoracion:  Valoracion, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val resultado = ValoracionRepository.insertarValoracion(valoracion)
            if (resultado) onSuccess() else onError()
        }
    }


}