package com.maestre.planneo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maestre.planneo.bd.EventoRepository
import com.maestre.planneo.bd.FavoritoRepository
import com.maestre.planneo.bd.LugarRepository
import com.maestre.planneo.bd.ValoracionRepository
import com.maestre.planneo.model.Evento
import com.maestre.planneo.model.LugarConFavorito
import com.maestre.planneo.model.Valoracion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.maestre.planneo.bd.AuthenticationRepositoryImpl

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Lugares
    private val _lugares = MutableStateFlow<List<LugarConFavorito>>(emptyList())
    val lugares: StateFlow<List<LugarConFavorito>> = _lugares

    private val _lugaresFavoritos = MutableStateFlow<List<LugarConFavorito>>(emptyList())
    val lugaresFavoritos: StateFlow<List<LugarConFavorito>> = _lugaresFavoritos

    // Eventos
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    // Valoraciones
    private val _valoraciones = MutableStateFlow<List<Valoracion>>(emptyList())
    val valoraciones: StateFlow<List<Valoracion>> = _valoraciones

    // Estados
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private fun getCurrentUserId(): String? {
        return AuthenticationRepositoryImpl.getCurrentUserId()
    }

    // LUGARES

    // Cargar lugares
    fun cargarLugares(tipo: String? = null) {
        viewModelScope.launch {
            _cargando.value = true
            val userId = getCurrentUserId()

            val todosLugares = if (tipo != null) {
                LugarRepository.getByTipo(tipo)
            } else {
                LugarRepository.getAll()
            }

            val favoritosIds = if (userId != null) {
                FavoritoRepository.getFavoritosByUser(userId)
                    .map { it.lugarId } // ✅ Usa lugarId
            } else {
                emptyList()
            }

            _lugares.value = todosLugares.map { lugar ->
                LugarConFavorito(
                    lugar = lugar,
                    esFavorito = lugar.id in favoritosIds
                )
            }
            _cargando.value = false
        }
    }

    // Alternar favorito
    fun alternarLugarFavorito(lugarConFavorito: LugarConFavorito) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            val exito = FavoritoRepository.alternarFavorito(userId, lugarConFavorito.lugar.id)

            if (exito) {
                _lugares.value = _lugares.value.map {
                    if (it.lugar.id == lugarConFavorito.lugar.id) {
                        it.copy(esFavorito = !it.esFavorito)
                    } else {
                        it
                    }
                }
                cargarLugaresFavoritos()
            }
        }
    }

    // Cargar favoritos
    fun cargarLugaresFavoritos() {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            val favoritosIds = FavoritoRepository.getFavoritosByUser(userId)
                .map { it.lugarId }

            val todosLugares = LugarRepository.getAll()
            val favoritos = todosLugares
                .filter { it.id in favoritosIds }
                .map { LugarConFavorito(it, true) }

            _lugaresFavoritos.value = favoritos
        }
    }


    fun buscarLugares(query: String, tipo: String? = null) {
        _searchQuery.value = query
        viewModelScope.launch {
            _cargando.value = true
            val userId = getCurrentUserId()

            val resultado = if (query.isBlank()) {
                if (tipo != null) LugarRepository.getByTipo(tipo)
                else LugarRepository.getAll()
            } else {
                if (tipo != null) LugarRepository.searchByTipo(query, tipo)
                else LugarRepository.search(query)
            }

            val favoritosIds = if (userId != null) {
                FavoritoRepository.getFavoritosByUser(userId)
                    .map { it.lugarId }
            } else {
                emptyList()
            }

            _lugares.value = resultado.map { lugar ->
                LugarConFavorito(lugar = lugar, esFavorito = lugar.id in favoritosIds)
            }
            _cargando.value = false
        }
    }

    // EVENTOS

    fun cargarEventos() {
        viewModelScope.launch {
            _eventos.value = EventoRepository.getAll()
        }
    }

    fun cargarEventosProximos() {
        viewModelScope.launch {
            _eventos.value = EventoRepository.getProximos()
        }
    }

    fun cargarEventosPorLugar(lugarId: Int) {
        viewModelScope.launch {
            _eventos.value = EventoRepository.getByLugarId(lugarId)
        }
    }

    // VALORACIONES

    fun cargarValoraciones(lugarId: Int) {
        viewModelScope.launch {
            _valoraciones.value = ValoracionRepository.getByLugarId(lugarId)
        }
    }

    fun insertValoracion(valoracion: Valoracion, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val resultado = ValoracionRepository.insertarValoracion(valoracion)
            if (resultado) onSuccess() else onError()
        }
    }
}