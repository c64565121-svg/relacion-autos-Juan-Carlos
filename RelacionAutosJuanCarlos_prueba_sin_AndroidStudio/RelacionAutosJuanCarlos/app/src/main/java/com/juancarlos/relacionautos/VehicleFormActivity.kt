package com.juancarlos.relacionautos

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juancarlos.relacionautos.data.AppDatabase
import com.juancarlos.relacionautos.data.Vehicle
import com.juancarlos.relacionautos.databinding.ActivityVehicleFormBinding
import kotlinx.coroutines.launch
import java.io.File

class VehicleFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleFormBinding
    private lateinit var photoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photoPath = intent.getStringExtra("photoPath").orEmpty()
        binding.edtChasis.setText(intent.getStringExtra("chassis").orEmpty())
        binding.edtModelo.setText(intent.getStringExtra("model").orEmpty())
        binding.edtAnio.setText(intent.getStringExtra("year").orEmpty())

        if (photoPath.isNotBlank()) {
            binding.imgFoto.setImageBitmap(BitmapFactory.decodeFile(photoPath))
        }

        binding.btnGuardar.setOnClickListener { saveVehicle() }
        binding.btnWord.setOnClickListener { exportWord() }
    }

    private fun currentVehicle() = Vehicle(
        photoPath = photoPath,
        chassis = binding.edtChasis.text.toString().trim().uppercase(),
        model = binding.edtModelo.text.toString().trim(),
        year = binding.edtAnio.text.toString().trim(),
        mechanic = binding.edtMecanico.text.toString().trim(),
        observations = binding.edtObservaciones.text.toString().trim()
    )

    private fun saveVehicle() {
        val vehicle = currentVehicle()
        if (vehicle.chassis.isBlank()) {
            binding.edtChasis.error = "Ingrese o corrija el número de chasis"
            return
        }
        if (vehicle.model.isBlank()) {
            binding.edtModelo.error = "Escriba el modelo del auto"
            return
        }

        lifecycleScope.launch {
            AppDatabase.get(this@VehicleFormActivity).vehicleDao().insert(vehicle)
            Toast.makeText(this@VehicleFormActivity, "Registro guardado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportWord() {
        val vehicle = currentVehicle()
        val output = File(
            getExternalFilesDir(null),
            "Ficha_${vehicle.chassis.ifBlank { "vehiculo" }}.docx"
        )
        DocxExporter.export(vehicle, output)
        Toast.makeText(this, "Word creado: ${output.absolutePath}", Toast.LENGTH_LONG).show()
    }
}
