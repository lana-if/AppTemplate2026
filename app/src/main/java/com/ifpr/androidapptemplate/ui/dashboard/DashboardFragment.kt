package com.ifpr.androidapptemplate.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.baseclasses.Item

class DashboardFragment : Fragment() {

    private lateinit var nomeEditText: EditText
    private lateinit var enderecoEditText: EditText
    private lateinit var generoEditText: EditText
    private lateinit var nascimentoEditText: EditText
    private lateinit var dataEditText: EditText
    private lateinit var horarioEditText: EditText
    private lateinit var servicoEditText: EditText

    private lateinit var itemImageView: ImageView
    private lateinit var salvarButton: Button
    private lateinit var selectImageButton: Button

    private var imageUri: Uri? = null

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // 🔥 CAPTURAR CAMPOS
        nomeEditText = view.findViewById(R.id.nomeItemEditText)
        enderecoEditText = view.findViewById(R.id.enderecoItemEditText)
        generoEditText = view.findViewById(R.id.generoItemEditText)
        nascimentoEditText = view.findViewById(R.id.nascimentoItemEditText)
        dataEditText = view.findViewById(R.id.dataItemEditText)
        horarioEditText = view.findViewById(R.id.horarioItemEditText)
        servicoEditText = view.findViewById(R.id.servicoItemEditText)

        itemImageView = view.findViewById(R.id.image_item)
        salvarButton = view.findViewById(R.id.salvarItemButton)
        selectImageButton = view.findViewById(R.id.button_select_image)

        auth = FirebaseAuth.getInstance()

        selectImageButton.setOnClickListener {
            openFileChooser()
        }

        salvarButton.setOnClickListener {
            salvarItem()
        }

        return view
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun salvarItem() {

        val nome = nomeEditText.text.toString().trim()
        val endereco = enderecoEditText.text.toString().trim()
        val genero = generoEditText.text.toString().trim()
        val nascimento = nascimentoEditText.text.toString().trim()
        val data = dataEditText.text.toString().trim()
        val horario = horarioEditText.text.toString().trim()
        val servico = servicoEditText.text.toString().trim()

        if (nome.isEmpty() || endereco.isEmpty() || genero.isEmpty() ||
            nascimento.isEmpty() || data.isEmpty() || horario.isEmpty() ||
            servico.isEmpty() || imageUri == null
        ) {
            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        uploadImageToFirebase(nome, endereco, genero, nascimento, data, horario, servico)
    }

    private fun uploadImageToFirebase(
        nome: String,
        endereco: String,
        genero: String,
        nascimento: String,
        data: String,
        horario: String,
        servico: String
    ) {

        val inputStream = context?.contentResolver?.openInputStream(imageUri!!)
        val bytes = inputStream?.readBytes()
        inputStream?.close()

        if (bytes != null) {

            val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

            // 🔥 ORDEM CORRETA DO ITEM
            val item = Item(
                nome,
                endereco,
                base64Image,
                null,
                genero,
                nascimento,
                data,
                horario,
                servico
            )

            saveItemIntoDatabase(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null &&
            data.data != null
        ) {
            imageUri = data.data
            Glide.with(this).load(imageUri).into(itemImageView)
        }
    }

    private fun saveItemIntoDatabase(item: Item) {

        databaseReference = FirebaseDatabase.getInstance().getReference("itens")

        val itemId = databaseReference.push().key

        if (itemId != null) {
            databaseReference.child(auth.uid.toString()).child(itemId).setValue(item)
                .addOnSuccessListener {
                    Toast.makeText(context, "Item salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao salvar item", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
