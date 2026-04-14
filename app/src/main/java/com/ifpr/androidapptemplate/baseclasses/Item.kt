package com.ifpr.androidapptemplate.baseclasses

data class Item(
    var nome: String? = null,
    var endereco: String? = null,
    val base64Image: String? = null,
    val imageUrl: String? = null,
    val genero: String? = null,
    val nascimento: String? = null,
    val data: String? = null,
    val horario: String? = null,
    val servico: String? = null
)
