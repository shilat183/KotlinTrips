package com.trips.project.presentation.ui.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.trips.project.R
import com.trips.project.data.db.DestinationDatabse
import com.trips.project.data.model.DestinationModel
import com.squareup.picasso.Picasso

class AddDestinationFragment : Fragment() {
    private lateinit var name_edittext: EditText
    private lateinit var price_edittext: EditText
    private lateinit var destinationImageView1: ImageView
    private lateinit var destinationImageView2: ImageView
    private lateinit var destinationImageView3: ImageView
    private lateinit var flighttime_edittext: EditText
    private lateinit var flightcompany_edittext: EditText
    private lateinit var tripduration_edittext: EditText
    private lateinit var tripreview_edittext: EditText
    private lateinit var countryname_edittext: EditText
    private lateinit var add_button: Button
    private lateinit var cancel_button: Button
    private var selectedImageUri1: Uri? = null
    private var selectedImageUri2: Uri? = null
    private var selectedImageUri3: Uri? = null

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var localDb: DestinationDatabse
    private  var imageViewIndex: Int = 0
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result ->
        if (result != null) {
            try {
                when (imageViewIndex) {
                    1 -> {
                        Picasso.get().load(result).into(destinationImageView1, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                selectedImageUri1 = result
                            }
                            override fun onError(e: Exception?) {
                                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                                e?.printStackTrace()
                            }
                        })
                    }
                    2 -> {
                        Picasso.get().load(result).into(destinationImageView2, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                selectedImageUri2 = result
                            }
                            override fun onError(e: Exception?) {
                                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                                e?.printStackTrace()
                            }
                        })
                    }
                    3 -> {
                        Picasso.get().load(result).into(destinationImageView3, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                selectedImageUri3 = result
                            }
                            override fun onError(e: Exception?) {
                                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                                e?.printStackTrace()
                            }
                        })
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun selectAddress() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireContext())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                val address = place.name
                countryname_edittext.setText(address)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigationBar()
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigationBar()
    }

    private fun hideBottomNavigationBar() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation1)
        bottomNavigationView.visibility = View.GONE
    }

    private fun showBottomNavigationBar() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation1)
        bottomNavigationView.visibility = View.VISIBLE
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        localDb = DestinationDatabse.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_destination, container, false)
        initializeViews(rootView)
        setupListeners()
        return rootView
    }

    private fun initializeViews(rootView: View) {
        name_edittext = rootView.findViewById(R.id.name_edittext)
        price_edittext = rootView.findViewById(R.id.price_edittext)
        destinationImageView1 = rootView.findViewById(R.id.destinationImageView1)
        destinationImageView2 = rootView.findViewById(R.id.destinationImageView2)
        destinationImageView3 = rootView.findViewById(R.id.destinationImageView3)
        countryname_edittext = rootView.findViewById(R.id.countryname_edittext)
        flighttime_edittext = rootView.findViewById(R.id.flighttime_edittext)
        flightcompany_edittext = rootView.findViewById(R.id.flightcompany_edittext)
        tripduration_edittext = rootView.findViewById(R.id.tripduration_edittext)
        tripreview_edittext = rootView.findViewById(R.id.tripreview_edittext)
        add_button = rootView.findViewById(R.id.add_button)
        cancel_button = rootView.findViewById(R.id.cancel_button)

    }


    private fun setupListeners() {
        destinationImageView1.setOnClickListener { selectImage(1) }
        destinationImageView2.setOnClickListener { selectImage(2) }
        destinationImageView3.setOnClickListener { selectImage(3) }
        add_button.setOnClickListener { uploadDestinationData() }
        cancel_button.setOnClickListener { NavHostFragment.findNavController(this).popBackStack() }
        countryname_edittext.setOnClickListener { selectAddress() }
    }

    private fun selectImage(index: Int) {
        imageViewIndex = index;
        imagePickerLauncher.launch("image/*")
    }


    private fun uploadDestinationData() {
        if (name_edittext.text.toString().isEmpty() || price_edittext.text.toString().isEmpty()
            || selectedImageUri1 == null || selectedImageUri2 == null || selectedImageUri3 == null
            || countryname_edittext.text.toString().isEmpty() || flighttime_edittext.text.toString().isEmpty()
            || tripduration_edittext.text.toString().isEmpty() || tripreview_edittext.text.toString().isEmpty() || flightcompany_edittext.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Adding destination...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val storage = FirebaseStorage.getInstance()
        val imageRef1 = storage.reference.child("destination_images/${name_edittext.text.toString()}_1.jpg")
        val imageRef2 = storage.reference.child("destination_images/${name_edittext.text.toString()}_2.jpg")
        val imageRef3 = storage.reference.child("destination_images/${name_edittext.text.toString()}_3.jpg")

        imageRef1.putFile(selectedImageUri1!!)
            .addOnSuccessListener { taskSnapshot ->
                imageRef1.downloadUrl
                    .addOnSuccessListener { uri1 ->
                        imageRef2.putFile(selectedImageUri2!!)
                            .addOnSuccessListener { taskSnapshot ->
                                imageRef2.downloadUrl
                                    .addOnSuccessListener { uri2 ->
                                        imageRef3.putFile(selectedImageUri3!!)
                                            .addOnSuccessListener { taskSnapshot ->
                                                imageRef3.downloadUrl
                                                    .addOnSuccessListener { uri3 ->

                                                        val destination = hashMapOf<String, Any>()
                                                        destination["name"] = name_edittext.text.toString().trim()
                                                        destination["price"] = price_edittext.text.toString().trim()
                                                        destination["userEmail"] = FirebaseAuth.getInstance().currentUser?.email!!
                                                        destination["imageUrl1"] = uri1.toString()
                                                        destination["imageUrl2"] = uri2.toString()
                                                        destination["imageUrl3"] = uri3.toString()
                                                        destination["country_name"] = countryname_edittext.text.toString().trim()
                                                        destination["flight_time"] = flighttime_edittext.text.toString().trim()
                                                        destination["flight_company"] = flightcompany_edittext.text.toString().trim()
                                                        destination["trip_duration"] = tripduration_edittext.text.toString().trim()
                                                        destination["trip_review"] = tripreview_edittext.text.toString().trim()

                                                        firestore.collection("Destinations")
                                                            .add(destination)
                                                            .addOnSuccessListener { documentReference ->
                                                                progressDialog.dismiss()
                                                                Toast.makeText(requireContext(), "Destination added successfully", Toast.LENGTH_SHORT).show()
                                                                NavHostFragment.findNavController(this).popBackStack()
                                                                val destination = DestinationModel(
                                                                    key = documentReference.id,
                                                                    userEmail = FirebaseAuth.getInstance().currentUser?.email!!,
                                                                    imageUrl1 = uri1.toString(),
                                                                    imageUrl2 = uri2.toString(),
                                                                    imageUrl3 = uri3.toString(),
                                                                    name = name_edittext.text.toString().trim(),
                                                                    price = price_edittext.text.toString().trim(),
                                                                    country_name = countryname_edittext.text.toString().trim(),
                                                                    flight_time = flighttime_edittext.text.toString().trim(),
                                                                    flight_company = flightcompany_edittext.text.toString().trim(),
                                                                    trip_duration = tripduration_edittext.text.toString().trim(),
                                                                    trip_review = tripreview_edittext.text.toString().trim()
                                                                )
                                                                saveDestinationToLocalDatabase(destination)
                                                                NavHostFragment.findNavController(this).popBackStack()
                                                            }
                                                            .addOnFailureListener { e ->
                                                                progressDialog.dismiss()
                                                                Toast.makeText(requireContext(), "Failed to add destination", Toast.LENGTH_SHORT).show()
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDestinationToLocalDatabase(destination: DestinationModel) {
        Thread { localDb.destinationDao().insert(destination) }.start()
    }
}
