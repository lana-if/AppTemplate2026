package com.ifpr.androidapptemplate.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import android.util.Base64
import android.graphics.BitmapFactory
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.baseclasses.Item

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val containerLayout = view.findViewById<LinearLayout>(R.id.itemContainer)
        carregarItensMarketplace(containerLayout)

        return view
    }

    private fun carregarItensMarketplace(container: LinearLayout) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("itens")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                container.removeAllViews()

                for (userSnapshot in snapshot.children) {
                    for (itemSnapshot in userSnapshot.children) {

                        val item = itemSnapshot.getValue(Item::class.java) ?: continue

                        val itemView = LayoutInflater.from(container.context)
                            .inflate(R.layout.item_template, container, false)

                        // 🔥 COMPONENTES
                        val imageView = itemView.findViewById<ImageView>(R.id.item_image)

                        val nomeText = itemView.findViewById<TextView>(R.id.item_nome)
                        val servicoText = itemView.findViewById<TextView>(R.id.item_servico)
                        val dataText = itemView.findViewById<TextView>(R.id.item_data)
                        val horarioText = itemView.findViewById<TextView>(R.id.item_horario)

                        val enderecoText = itemView.findViewById<TextView>(R.id.item_endereco)
                        val generoText = itemView.findViewById<TextView>(R.id.item_genero)
                        val nascimentoText = itemView.findViewById<TextView>(R.id.item_nascimento)

                        // 📝 DADOS
                        nomeText.text = "👤 ${item.nome ?: "Não informado"}"
                        servicoText.text = "💼 Serviço: ${item.servico ?: "Não informado"}"
                        dataText.text = "📅 Data: ${item.data ?: "Não informado"}"
                        horarioText.text = "⏰ Horário: ${item.horario ?: "Não informado"}"

                        enderecoText.text = "📍 ${item.endereco ?: "Não informado"}"
                        generoText.text = "👤 Gênero: ${item.genero ?: "Não informado"}"
                        nascimentoText.text = "🎂 Nascimento: ${item.nascimento ?: "Não informado"}"

                        // 🖼️ IMAGEM
                        if (!item.imageUrl.isNullOrEmpty()) {
                            Glide.with(container.context)
                                .load(item.imageUrl)
                                .into(imageView)
                        } else if (!item.base64Image.isNullOrEmpty()) {
                            try {
                                val bytes = Base64.decode(item.base64Image, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                imageView.setImageBitmap(bitmap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        // ➕ ADICIONAR NA TELA
                        container.addView(itemView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(container.context, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
