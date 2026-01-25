package com.leo.trailov2.components


object Screen {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ACTIVIDADES = "actividades"
    const val PARQUES = "parques"
    const val PERFIL = "perfil"
    const val DETALLE_ACTIVIDAD = "detalle_actividad/{actividadId}"
    const val DETALLE_PARQUE = "detalle_parque/{parqueId}"
    const val VALORAR = "valorar/{tipo}/{id}/{nombre}"
    const val VER_RESENAS = "ver_resenas/{tipo}/{id}/{nombre}"


    fun detalleActividad(id: Int) = "detalle_actividad/$id"

    fun detalleParque(id: Int) = "detalle_parque/$id"

    fun valorar(tipo: String, id: Int, nombre: String) = "valorar/$tipo/$id/$nombre"

    fun verResenas(tipo: String, id: Int, nombre: String) = "ver_resenas/$tipo/$id/$nombre"
}